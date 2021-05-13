package com.gprs.srp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;


public class fragment_schedulledcounselling extends Fragment {

    ArrayList<String> name, role, time, message;
    ArrayList<UserRegistrationHelper> users;
    CustomCounsellingApplicationAdapter c;
    ListView list;
    private ArrayList<Double> pos,neg;

    SharedPreferences pref;
    SharedPreferences.Editor editor;
    View view;
    private ArrayList<Integer> rank;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_schedulledcounselling, container, false);

        name = new ArrayList<>();
        role = new ArrayList<>();
        time = new ArrayList<>();
        users = new ArrayList<>();
        pos = new ArrayList<>();
        neg = new ArrayList<>();
        rank=new ArrayList<>();
        message = new ArrayList<>();
        list = view.findViewById(R.id.list);
        c = new CustomCounsellingApplicationAdapter(getActivity(), name, role, message, time,rank);
        list.setAdapter(c);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
                view(position);
                view.findViewById(R.id.refresh).setVisibility(View.VISIBLE);
            }
        });
        pref = getActivity().getApplicationContext().getSharedPreferences("user", 0); // 0 - for private mode

        view.findViewById(R.id.refresh).setVisibility(View.INVISIBLE);

        view.findViewById(R.id.refresh).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                view.findViewById(R.id.refresh).setVisibility(View.INVISIBLE);
                name.clear();
                users.clear();
                role.clear();
                message.clear();
                time.clear();
                rank.clear();
                pos.clear();
                neg.clear();
                FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(pref.getString("user", "")).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot != null) {
                            for (DataSnapshot d : dataSnapshot.getChildren()) {
                                CounsellorHelper ch = d.getValue(CounsellorHelper.class);
                                users.add(ch.getU());
                                name.add(ch.getU().getFname());
                                role.add(ch.getU().getRole());
                                message.add(ch.getMessage());
                                time.add(ch.getTime());
                                rank.add(ch.getRank());
                                pos.add(ch.getPos());
                                neg.add(ch.getPos());
                            }
                            c.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        });

        FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(pref.getString("user", "")).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    for (DataSnapshot d : dataSnapshot.getChildren()) {
                        CounsellorHelper ch = d.getValue(CounsellorHelper.class);
                        users.add(ch.getU());
                        name.add(ch.getU().getFname());
                        role.add(ch.getU().getRole());
                        message.add(ch.getMessage());
                        time.add(ch.getTime());
                        rank.add(ch.getRank());
                        pos.add(ch.getPos());
                        neg.add(ch.getPos());
                    }
                    c.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return view;
    }

    private void view(int position) {
        Bundle bundle = new Bundle();
        bundle.putString("name", users.get(position).getFname());
        bundle.putString("phone", users.get(position).getPhone());
        bundle.putString("email", users.get(position).getEmail());
        bundle.putString("role", users.get(position).getRole());
        bundle.putString("message", message.get(position));
        bundle.putString("time", time.get(position));
        bundle.putDouble("lat", users.get(position).getLat());
        bundle.putDouble("lon", users.get(position).getLon());
        bundle.putDouble("pos", pos.get(position));
        bundle.putDouble("neg", neg.get(position));
        bundle.putInt("rank", rank.get(position));

        BottomSheetDialogFragment f = new Bottomsheetcounsellingscheduleviewfragment();
        f.setArguments(bundle);
        f.show(getActivity().getSupportFragmentManager(), "Dialog");
    }

}