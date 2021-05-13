package com.gprs.srp;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class languageadapter extends ArrayAdapter {


    ArrayList<String> name, key;
    Activity context;

    public languageadapter(ArrayList<String> name, ArrayList<String> key, Activity context) {
        super(context, R.layout.alarm_item, name);
        this.context = context;
        this.name = name;
        this.key = key;
    }


    public View getView(final int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = null;
        rowView = inflater.inflate(R.layout.simple_language_item, null, true);

        TextView textView = rowView.findViewById(R.id.txt_bundle);
        textView.setText(name.get(position));

        SharedPreferences pref;
        SharedPreferences.Editor editor;
        pref = context.getApplicationContext().getSharedPreferences("language", 0); // 0 - for private mode

        if (pref.getString("lang", "en").equals(key.get(position))) {
            textView.setBackgroundColor(Color.parseColor("#EFEFEF"));
            textView.setTextColor(Color.BLACK);
            textView.setEnabled(false);
            textView.setClickable(false);
        }

        return rowView;
    }
}
