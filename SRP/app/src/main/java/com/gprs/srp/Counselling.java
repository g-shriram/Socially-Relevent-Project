package com.gprs.srp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

public class Counselling extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_counselling);

        ImageView imageView = findViewById(R.id.logo);
        new Counselling.SetImage(imageView).execute("https://www.cuchd.in/covid-19/img/icon-4.png");
        imageView = findViewById(R.id.apply);
        imageView.setImageDrawable(getDrawable(R.drawable.counsellingbackground));
        findViewById(R.id.viewmore).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Counselling.this, pdfViewer.class);
                intent.putExtra("text", "https://www.nhs.uk/oneyou/every-mind-matters/coronavirus-covid-19-anxiety-tips/");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Counselling.this).toBundle());
            }
        });
        findViewById(R.id.viewmore1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Counselling.this, pdfViewer.class);
                intent.putExtra("text", " https://drive.google.com/file/d/11dMhP4N8iKm92HvYj0ok4_AhGXrnmgal/view?usp=sharing");
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(Counselling.this).toBundle());
            }
        });

        findViewById(R.id.applybutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialogFragment f = new BottomsheetCounsellingformfragment();
                f.show(getSupportFragmentManager(), "Dialog");
            }
        });
        final SharedPreferences pref = getSharedPreferences("user", 0);
        FirebaseDatabase.getInstance().getReference().child("counselling").child("application").child(pref.getString("user", "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    CounsellorHelper c = dataSnapshot.getValue(CounsellorHelper.class);
                    Button button = findViewById(R.id.applybutton);
                    button.setText("Form Submitted");
                    findViewById(R.id.applybutton).setEnabled(false);

                } else {
                    Button button = findViewById(R.id.applybutton);
                    button.setText(getString(R.string.apply_for_counselling));
                    findViewById(R.id.applybutton).setEnabled(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        FirebaseDatabase.getInstance().getReference().child("counselling").child("enable").child(pref.getString("user", "")).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    final CounsellorHelper c = dataSnapshot.getValue(CounsellorHelper.class);
                    Button button = findViewById(R.id.applybutton);
                    button.setText("Open chat window");
                    findViewById(R.id.applybutton).setEnabled(true);
                    button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(Counselling.this, CounsellingChat.class);
                            i.putExtra("my", pref.getString("user", ""));
                            i.putExtra("them", c.getCounsellor());
                            startActivity(i, ActivityOptions.makeSceneTransitionAnimation(Counselling.this).toBundle());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        imageView = findViewById(R.id.logo1);
        new Counselling.SetImage(imageView).execute("https://www.studying-in-germany.org/wp-content/uploads/2017/12/light-bulb.png");


        final ScrollView sv = findViewById(R.id.sv);

    }


    private class SetImage extends AsyncTask<String, Void, Bitmap> {

        ImageView imageView;

        public SetImage(ImageView bmImage) {
            this.imageView = bmImage;
        }

        protected Bitmap doInBackground(String... urls) {
            String urldisplay = urls[0];
            Bitmap mIcon11 = null;
            try {
                InputStream in = new URL(urldisplay).openStream();
                mIcon11 = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();
            }
            return mIcon11;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);

        }
    }

}