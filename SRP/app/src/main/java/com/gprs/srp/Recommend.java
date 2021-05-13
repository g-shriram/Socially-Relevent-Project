/*
 * Copyright 2019 The TensorFlow Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gprs.srp;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gprs.srp.RecommendationClient.Result;
import com.gprs.srp.data.FileUtil;
import com.gprs.srp.data.MovieItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/** The main activity to provide interactions with users. */
public class Recommend extends AppCompatActivity
        implements MovieFragment.OnListFragmentInteractionListener,
        RecommendationFragment.OnListFragmentInteractionListener {
  private static final String TAG = "OnDeviceRecommendationDemo";
  private static final String CONFIG_PATH = "config.json";  // Default config path in assets.

  private Config config;
  private RecommendationClient client;
  private final List<MovieItem> allMovies = new ArrayList<>();
  private final List<MovieItem> selectedMovies = new ArrayList<>();
  private SharedPreferences pref;
  SharedPreferences.Editor editor;
  private Handler handler;
  private MovieFragment movieFragment;
  private RecommendationFragment recommendationFragment;
  List<Result> recommendations;
  List<MovieItem> favoriteMovies;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_recommend);
    Log.v(TAG, "onCreate");

    recommendations=new ArrayList<>();
    pref = getApplicationContext().getSharedPreferences("language", 0); // 0 - for private mode
    editor = pref.edit();

    if(pref.getString("rec",null)!=null){
      Gson gson=new Gson();

      recommendations= (List<Result>) gson.fromJson(pref.getString("rec",null),
              new TypeToken<List<Result>>() {
              }.getType());
    }

    // Load config file.
    try {
      config = FileUtil.loadConfig(getAssets(), CONFIG_PATH);
    } catch (IOException ex) {
      Log.e(TAG, String.format("Error occurs when loading config %s: %s.", CONFIG_PATH, ex));
    }

    // Load movies list.
    try {
      allMovies.clear();
      allMovies.addAll(FileUtil.loadMovieList(getAssets(), config.movieListPath));
    } catch (IOException ex) {
      Log.e(
              TAG, String.format("Error occurs when loading movies %s: %s.", config.movieListPath, ex));
    }

    client = new RecommendationClient(this, config);
    handler = new Handler();
    movieFragment =
            (MovieFragment) getSupportFragmentManager().findFragmentById(R.id.movie_fragment);
    recommendationFragment =
            (RecommendationFragment)
                    getSupportFragmentManager().findFragmentById(R.id.recommendation_fragment);
  }

  @SuppressWarnings("AndroidJdkLibsChecker")
  @Override
  protected void onStart() {
    super.onStart();
    Log.v(TAG, "onStart");

    // Add favorite movies to the fragment.
    favoriteMovies =
            allMovies.stream().limit(config.favoriteListSize).collect(Collectors.toList());
    movieFragment.setMovies(favoriteMovies,recommendations);

    handler.post(
            () -> {
              client.load();
            });
  }

  @Override
  protected void onStop() {
    super.onStop();
    Log.v(TAG, "onStop");
    handler.post(
            () -> {
              client.unload();
            });
  }

  /** Sends selected movie list and get recommendations. */
  private void recommend(final List<MovieItem> movies) {
    handler.post(
            () -> {
              // Run inference with TF Lite.
              Log.d(TAG, "Run inference with TFLite model.");
              List<Result> recommendations = client.recommend(movies);

              // Show result on screen
              showResult(recommendations);
            });
  }

  /** Shows result on the screen. */
  private void showResult(final List<Result> recommendations) {
    // Run on UI thread as we'll updating our app UI
    runOnUiThread(
            () -> {
              recommendationFragment.setRecommendations(recommendations);
              movieFragment.setMovies(favoriteMovies,recommendations);

              Gson gson=new Gson();
              String recommend=gson.toJson(recommendations);
              editor.putString("rec",recommend).commit();

            });
  }

  @Override
  public void onItemSelectionChange(MovieItem item) {
    if (item.selected) {
      if (!selectedMovies.contains(item)) {
        selectedMovies.add(item);
      }
    } else {
        selectedMovies.remove(item);
    }

    if (!selectedMovies.isEmpty()) {
      // Log selected movies.
      StringBuilder sb = new StringBuilder();
      sb.append("Select movies in the following order:\n");
      for (MovieItem movie : selectedMovies) {
        sb.append(String.format("  movie: %s\n", movie));
      }
      Log.d(TAG, sb.toString());

      // Recommend based on selected movies.
      recommend(selectedMovies);
    } else {
      // Clear result list.
      showResult(new ArrayList<Result>());
    }
  }

  /** Handles click event of recommended movie. */
  @Override
  public void onClickRecommendedMovie(MovieItem item) {
    // Show message for the clicked movie.
    String message = String.format("Clicked recommended movie: %s.", item.title);
    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
  }
}
