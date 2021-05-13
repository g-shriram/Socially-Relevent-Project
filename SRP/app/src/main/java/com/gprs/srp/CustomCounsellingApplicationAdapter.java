package com.gprs.srp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

class CustomCounsellingApplicationAdapter extends ArrayAdapter {


    private final ArrayList<Integer> rank;
    ArrayList<String> name, role, message, time;
    private Activity context;


    public CustomCounsellingApplicationAdapter(Activity context, ArrayList<String> name, ArrayList<String> role, ArrayList<String> message, ArrayList<String> time,ArrayList<Integer> rank) {
        super(context, R.layout.counsellingapplicationitem, name);
        this.context = context;
        this.name = name;
        this.role = role;
        this.message = message;
        this.time = time;
        this.rank=rank;

    }

    public View getView(int position, View view, ViewGroup parent) {

        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = null;
        rowView = inflater.inflate(R.layout.counsellingapplicationitem, null, true);
        //this code gets references to objects in the listview_row.xml file
        TextView name1 = rowView.findViewById(R.id.name);
        TextView role1 = rowView.findViewById(R.id.role);
        TextView time1 = rowView.findViewById(R.id.time);
        TextView message1 = rowView.findViewById(R.id.message);
        TextView rank1 = rowView.findViewById(R.id.rank);

        name1.setText(name.get(position));
        role1.setText(role.get(position));
        time1.setText(time.get(position));
        message1.setText(message.get(position));
        rank1.setText("Rank : "+rank.get(position));


        return rowView;

    }


}
