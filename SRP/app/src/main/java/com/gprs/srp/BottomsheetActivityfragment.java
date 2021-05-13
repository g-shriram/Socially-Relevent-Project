package com.gprs.srp;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;



public class BottomsheetActivityfragment extends BottomSheetDialogFragment {

    ImageView proimg;
    String name, desc;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.bottom_sheet_activity_layout, container, false);

        proimg = view.findViewById(R.id.proimg);

        name = this.getArguments().getString("name");
        desc = this.getArguments().getString("desc");


        Button close=view.findViewById(R.id.logout);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        TextView name1, desc1;
        name1 = view.findViewById(R.id.name);
        desc1 = view.findViewById(R.id.desc);


        name1.setText(name);
        desc1.setText(desc);




        return view;
    }


}