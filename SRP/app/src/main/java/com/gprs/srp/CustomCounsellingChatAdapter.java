package com.gprs.srp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

class CustomCounsellingChatAdapter extends ArrayAdapter {


    ArrayList<String> text;
    private final ArrayList<Integer> toggle;
    private Activity context;


    public CustomCounsellingChatAdapter(Activity context, ArrayList<Integer> togle, ArrayList<String> text) {
        super(context, R.layout.their_message, text);
        this.context = context;
        this.toggle = togle;
        this.text = text;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = null;
        if (toggle.get(position) == 0) {
            rowView = inflater.inflate(R.layout.counsellingtheirchatitem, null, true);
            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = rowView.findViewById(R.id.name);
            TextView infoTextField = rowView.findViewById(R.id.text);
            ImageView imageView = rowView.findViewById(R.id.avatar);


            //this code sets the values of the objects to values from the arrays
            nameTextField.setText("Covid19Relief");
            infoTextField.setText(text.get(position));
            imageView.setImageResource(R.drawable.collective_intelligence_icon_use);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MMM:hh:mm:a");
            String currentDateTime = dateFormat.format(new Date());
            TextView time = rowView.findViewById(R.id.time);
            time.setText(currentDateTime);

        } else {
            rowView = inflater.inflate(R.layout.councellingmychatitem, null, true);

            SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MMM:hh:mm:a");
            String currentDateTime = dateFormat.format(new Date());
            //this code gets references to objects in the listview_row.xml file
            TextView nameTextField = rowView.findViewById(R.id.text);
            nameTextField.setText(text.get(position));
            TextView time = rowView.findViewById(R.id.time);
            time.setText(currentDateTime);
        }
        return rowView;

    }


}
