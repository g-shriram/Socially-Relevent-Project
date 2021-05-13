package com.gprs.srp;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class Bottomsheetcounsellingapplicationviewfragment extends BottomSheetDialogFragment {

    String name, email, role, phone, time, message;
    Double lat, lon;
    EditText fixedtime;
    TextView name1, email1, role1, phone1, time1, message1, location,pos,neg;
    SharedPreferences pref;
    RadioGroup radioGroup;
    AlertDialog alertDialog;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                BottomSheetDialog bottomSheetDialog = (BottomSheetDialog) dialogInterface;
                setupFullHeight(bottomSheetDialog);
            }
        });
        return dialog;
    }


    private void setupFullHeight(BottomSheetDialog bottomSheetDialog) {
        FrameLayout bottomSheet = (FrameLayout) bottomSheetDialog.findViewById(R.id.design_bottom_sheet);
        BottomSheetBehavior behavior = BottomSheetBehavior.from(bottomSheet);
        ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();

        int windowHeight = getWindowHeight();
        if (layoutParams != null) {
            layoutParams.height = windowHeight;
        }
        bottomSheet.setLayoutParams(layoutParams);
        behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
    }

    private int getWindowHeight() {
        // Calculate window height for fullscreen use
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics.heightPixels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.counsellingapplicationview, container, false);

        pref = getActivity().getSharedPreferences("user", 0); // 0 - for private mode

        fixedtime = view.findViewById(R.id.fixedtime);
        name1 = view.findViewById(R.id.name);
        phone1 = view.findViewById(R.id.phone);
        email1 = view.findViewById(R.id.email);
        role1 = view.findViewById(R.id.role);
        message1 = view.findViewById(R.id.message);
        time1 = view.findViewById(R.id.time);
        location = view.findViewById(R.id.location);
        radioGroup = view.findViewById(R.id.attendradio);
        pos=view.findViewById(R.id.pos);
        neg=view.findViewById(R.id.neg);
        pos.setText("Positivity : "+ this.getArguments().getDouble("pos"));
         neg.setText("Negativity : "+ this.getArguments().getDouble("neg"));

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioButton = view.findViewById(checkedId);


                if (radioButton.getText().equals(getString(R.string.schedule))) {
                    view.findViewById(R.id.fixedtimelayout).setVisibility(View.VISIBLE);
                } else {
                    view.findViewById(R.id.fixedtimelayout).setVisibility(View.INVISIBLE);
                }
            }
        });

        view.findViewById(R.id.yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioGroup.getCheckedRadioButtonId() != -1) {
                    RadioButton radioButton = view.findViewById(radioGroup.getCheckedRadioButtonId());
                    if (radioButton.getText().equals("Schedule")) {
                        if (!fixedtime.getText().toString().isEmpty()) {
                            alertDialog = new AlertDialog.Builder(getActivity()).create();
                            alertDialog.setTitle("Confirm");
                            alertDialog.setMessage("Schedule counselling at " + fixedtime.getText().toString());
                            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                            final String currentDateTime = dateFormat.format(new Date()); // Find todays date
                                            FirebaseDatabase.getInstance().getReference().child("counselling").child("application").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                    if (dataSnapshot != null) {
                                                        CounsellorHelper c = dataSnapshot.getValue(CounsellorHelper.class);
                                                        c.setCounsellor(pref.getString("user", ""));
                                                        c.setFixedtime(fixedtime.getText().toString());
                                                        FirebaseDatabase.getInstance().getReference().child("counselling").child("application").child(phone).removeValue();
                                                        CounsellorHelper counsellorHelper = new CounsellorHelper();
                                                        counsellorHelper.setCounsellor(pref.getString("user", ""));
                                                        FirebaseDatabase.getInstance().getReference().child("counselling").child("enable").child(phone).setValue(counsellorHelper);
                                                        FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(pref.getString("user", "")).child(phone).setValue(c);
                                                        FirebaseDatabase.getInstance().getReference().child("Notification").child(phone).child("counselling").child(currentDateTime).setValue("You counselling is scheduled at" + fixedtime.getText().toString());
                                                        dismiss();
                                                    }
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError databaseError) {

                                                }
                                            });

                                            alertDialog.hide();
                                        }
                                    });
                            alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                            alertDialog.show();
                        } else
                            Toast.makeText(getActivity(), "Please enter time", Toast.LENGTH_SHORT).show();
                    } else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        final String currentDateTime = dateFormat.format(new Date()); // Find todays date
                        alertDialog = new AlertDialog.Builder(getActivity()).create();
                        alertDialog.setTitle("Confirm");
                        alertDialog.setMessage("Schedule counselling at " + currentDateTime);
                        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {


                                        FirebaseDatabase.getInstance().getReference().child("counselling").child("application").child(phone).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                if (dataSnapshot != null) {
                                                    CounsellorHelper c = dataSnapshot.getValue(CounsellorHelper.class);
                                                    c.setCounsellor(pref.getString("user", ""));
                                                    c.setFixedtime(currentDateTime);
                                                    FirebaseDatabase.getInstance().getReference().child("counselling").child("application").child(phone).removeValue();
                                                    CounsellorHelper counsellorHelper = new CounsellorHelper();
                                                    counsellorHelper.setCounsellor(pref.getString("user", ""));
                                                    FirebaseDatabase.getInstance().getReference().child("counselling").child("enable").child(phone).setValue(counsellorHelper);
                                                    FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(pref.getString("user", "")).child(phone).setValue(c);
                                                    FirebaseDatabase.getInstance().getReference().child("Notification").child(phone).child("counselling").child(currentDateTime).setValue("You counselling is scheduled now");
                                                    Intent i = new Intent(getActivity(), CounsellingChat.class);
                                                    i.putExtra("my", pref.getString("user", ""));
                                                    i.putExtra("them", phone);
                                                    i.putExtra("couns", true);
                                                    startActivity(i, ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                                                    dismiss();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });

                                        alertDialog.hide();
                                    }
                                });
                        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                        alertDialog.show();
                    }
                } else {
                    Toast.makeText(getActivity(), "Please fill all the fields", Toast.LENGTH_SHORT).show();
                }
            }
        });

        name = this.getArguments().getString("name");
        role = this.getArguments().getString("role");
        phone = this.getArguments().getString("phone");
        time = this.getArguments().getString("time");
        email = this.getArguments().getString("email");
        message = this.getArguments().getString("message");
        lat = this.getArguments().getDouble("lat");
        lon = this.getArguments().getDouble("lon");

        name1.setText(name);
        email1.setText(email);
        phone1.setText(phone);
        role1.setText(role);
        message1.setText(message);
        time1.setText(time);
        location.setText(getlocation(lat, lon));

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (mStorageRef.child(phone) != null) {
            StorageReference sr = mStorageRef.child("proImg").child(phone + ".jpg");
            sr.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                @Override
                public void onSuccess(byte[] bytes) {
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageView proimg = view.findViewById(R.id.proimg);
                    proimg.setImageBitmap(bitmap);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                }
            });
        }

        view.findViewById(R.id.no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog = new AlertDialog.Builder(getActivity()).create();
                alertDialog.setTitle("Confirm");
                alertDialog.setMessage("Reject application of " + name);
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                final String currentDateTime = dateFormat.format(new Date()); // Find todays date
                                FirebaseDatabase.getInstance().getReference().child("counselling").child("application").child(phone).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("Notification").child(phone).child("counselling").child(currentDateTime).setValue("You application for counselling is rejected");
                                alertDialog.hide();
                                dismiss();

                            }
                        });
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                alertDialog.show();
            }
        });

        view.findViewById(R.id.close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private String getlocation(Double lat, Double lon) {
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(getActivity(), Locale.getDefault());

        try {
            addresses = geocoder.getFromLocation(lat, lon, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5


            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();

            return city + ',' + state;


        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }


}