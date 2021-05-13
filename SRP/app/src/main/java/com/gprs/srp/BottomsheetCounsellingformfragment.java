package com.gprs.srp;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;


public class BottomsheetCounsellingformfragment extends BottomSheetDialogFragment {

    SharedPreferences pref;
    UserRegistrationHelper u;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.counselling_form, container, false);
        pref = getActivity().getSharedPreferences("user", 0); //


        final TextView name, phone, email, role;
        name = view.findViewById(R.id.name);
        phone = view.findViewById(R.id.phone);
        email = view.findViewById(R.id.email);
        role = view.findViewById(R.id.role);


        FirebaseDatabase.getInstance().getReference().child("Users").child(pref.getString("user", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    u = dataSnapshot.getValue(UserRegistrationHelper.class);
                    name.setText(u.getFname());
                    phone.setText(pref.getString("user", ""));
                    email.setText(u.getEmail());
                    role.setText(u.getRole());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        view.findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.submit).setEnabled(false);
                view.findViewById(R.id.submit).setBackgroundColor(Color.parseColor("#FFCFCFCF"));
                CheckBox checkBox = view.findViewById(R.id.checkBox);
                EditText message = view.findViewById(R.id.summary);
                if (checkBox.isChecked()) {
                    if (!message.getText().toString().isEmpty()) {
                        if (u != null && u.getPhone() != null) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd:MMM:hh:mm:a");
                            String currentDateTime = dateFormat.format(new Date());
                            CounsellorHelper c = new CounsellorHelper(u, message.getText().toString(), currentDateTime, "pending",pref.getFloat("pos",0.0f),pref.getFloat("neg",0.0f),pref.getInt("status",0));
                            FirebaseDatabase.getInstance().getReference().child("counselling").child("application").child(pref.getString("user", "")).setValue(c);
                            Toast.makeText(getActivity(), "Submitted Successfully", Toast.LENGTH_SHORT).show();
                            dismiss();

                        } else {
                            Toast.makeText(getActivity(), "Error ,Try Again", Toast.LENGTH_SHORT).show();
                            view.findViewById(R.id.submit).setEnabled(true);
                            view.findViewById(R.id.submit).setBackgroundColor(Color.parseColor("#000000"));
                        }
                    } else
                        Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                    view.findViewById(R.id.submit).setEnabled(true);
                    view.findViewById(R.id.submit).setBackgroundColor(Color.parseColor("#000000"));

                } else {
                    Toast.makeText(getActivity(), "Please select the checkbox", Toast.LENGTH_SHORT).show();
                    view.findViewById(R.id.submit).setEnabled(true);
                    view.findViewById(R.id.submit).setBackgroundColor(Color.parseColor("#000000"));
                }
            }
        });
        return view;
    }
}