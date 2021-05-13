package com.gprs.srp;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.gprs.srp.data.MovieItem;

import java.util.ArrayList;
import java.util.List;


class CustomMovieListAdapter extends ArrayAdapter {

    List<MovieItem> items;
    private Activity context;
    MovieFragment.OnListFragmentInteractionListener listener;
    List<String> movies;
    List<ImageView> imageViews;

    public CustomMovieListAdapter(Activity context,List<MovieItem> items, MovieFragment.OnListFragmentInteractionListener listener) {
        super(context, R.layout.tfe_re_fragment_selection, items);
        this.context = context;
        this.items = items;
        this.listener=listener;
        movies=null;
        imageViews=new ArrayList<>();
    }

    public CustomMovieListAdapter(Activity activity, List<MovieItem> items, MovieFragment.OnListFragmentInteractionListener listener, List<RecommendationClient.Result> recommendations) {
        super(activity, R.layout.tfe_re_fragment_selection, items);
        this.context = activity;
        this.items = items;
        this.listener=listener;
        movies=new ArrayList<>();
        imageViews=new ArrayList<>();
        for(int i=0;i<recommendations.size();i++){
            movies.add(recommendations.get(i).item.title);
        }

    }

    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = null;

        rowView = inflater.inflate(R.layout.tfe_re_fragment_selection, null, true);
        final TextView title = rowView.findViewById(R.id.movie_title);
        title.setText(items.get(position).title);

        imageViews.add(rowView.findViewById(R.id.imageView));
        if (movies!=null){
            if(movies.contains(items.get(position).title)){
                title.setTextColor(Color.parseColor("#D72222"));
                imageViews.get(position).setVisibility(View.VISIBLE);
            }
            else {
                title.setTextColor(Color.parseColor("#555555"));
                imageViews.get(position).setVisibility(View.INVISIBLE);
            }
        }

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                items.get(position).selected = !items.get(position).selected;
               listener.onItemSelectionChange(items.get(position));
                Bundle bundle = new Bundle();
                bundle.putString("name", items.get(position).title);
                bundle.putString("desc", items.get(position).desc);


                BottomSheetDialogFragment f = new BottomsheetActivityfragment();
                f.setArguments(bundle);
                f.show(((Recommend)context).getSupportFragmentManager(), "Dialog");
            }
        });
        return rowView;
    }


}


