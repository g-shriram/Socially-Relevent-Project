package com.gprs.srp;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.concurrent.TimeUnit;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import pl.droidsonroids.gif.GifImageView;


public class Bottomsheetregisterfragment extends BottomSheetDialogFragment {

    private String verificationCodeBySystem;
    DatabaseReference reference;
    FirebaseDatabase database;
    View view;

    int count = 60;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.bottom_sheet_register_layout, container, false);
        TextView mobile = view.findViewById(R.id.mobile);
        mobile.setText("+91 " + this.getArguments().getString("phone"));
        view.findViewById(R.id.mobile).setFocusable(false);
        validatemobile();


        new CountDownTimer(60000, 1000) {
            public void onTick(long l) {
                TextInputLayout editText = view.findViewById(R.id.otplayout);

                editText.setHint("Enter OTP ( " + count + " )");
                count = count - 1;
            }

            public void onFinish() {
                dismiss();
            }
        }.start();
        view.findViewById(R.id.dismiss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        return view;
    }

    private void validatemobile() {

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91 " + this.getArguments().getString("phone"),        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                TaskExecutors.MAIN_THREAD,               // Activity (for callback binding)
                mCallbacks);


        verifymobile();
    }

    private void verifymobile() {

        view.findViewById(R.id.register).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText input = view.findViewById(R.id.otp);
                view.findViewById(R.id.register).setEnabled(false);
                if (input.getText().toString().equals("")) {
                    view.findViewById(R.id.register).setEnabled(true);
                    Toast.makeText(getActivity(), "Enter valid OTP", Toast.LENGTH_LONG).show();

                } else {
                    verifycode(input.getText().toString());
                }
            }
        });


    }

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            verificationCodeBySystem = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {

            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                verifycode(code);
            }

        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(getActivity(), "Authentication failed" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    };

    private void verifycode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCodeBySystem, code);
        signinwithcredentials(credential);
    }

    private void signinwithcredentials(final PhoneAuthCredential credential) {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                view.findViewById(R.id.register).setEnabled(true);
                if (task.isSuccessful()) {
                    try {

                        setcount(getArguments().getString("role"));
                        UserRegistrationHelper helper = new UserRegistrationHelper(getArguments().getString("name"), getArguments().getString("email"), getArguments().getDouble("lat"), getArguments().getDouble("lon"), getArguments().getString("phone"), getArguments().getString("role"), encrypt(getArguments().getString("password")));
                        database = FirebaseDatabase.getInstance();


                        reference = database.getReference("Users");

                        reference.child(getArguments().getString("phone")).setValue(helper);
                        if (getArguments().getString("role").equals("Victim"))
                            reference.child(getArguments().getString("phone")).child("status").setValue(1);
                        UserLocationHelper userLocationHelper = new UserLocationHelper(getArguments().getString("name"), getArguments().getDouble("lat"), getArguments().getDouble("lon"), getArguments().getString("role"), getArguments().getString("email"), getArguments().getString("phone"));
                        database.getReference("Location").child(getArguments().getString("phone")).setValue(userLocationHelper);


                        SharedPreferences pref;
                        SharedPreferences.Editor editor;

                        pref = getActivity().getSharedPreferences("user", 0); // 0 - for private mode
                        editor = pref.edit();

                        editor.putString("user", getArguments().getString("phone"));
                        editor.putString("role", getArguments().getString("role"));
                        editor.apply();

                        EditText editText = view.findViewById(R.id.otp);
                        editText.setText("******");
                        GifImageView gifImageView = view.findViewById(R.id.success);
                        gifImageView.setVisibility(View.VISIBLE);
                        ImageView imageView = view.findViewById(R.id.register);
                        imageView.setVisibility(View.INVISIBLE);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                startActivity(new Intent(getActivity(), test.class), ActivityOptions.makeSceneTransitionAnimation(getActivity()).toBundle());
                                getActivity().finish();
                            }
                        }, 2000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else
                    Toast.makeText(getActivity(), "Incorrect OTP", Toast.LENGTH_LONG).show();
            }
        });
    }

    void setcount(final String role) {
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference[] reference1 = {database.getReference("Count").child(role)};
        final Integer[] count = new Integer[1];

        reference1[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                count[0] = dataSnapshot.getValue(Integer.class);
                if (count[0] != null) {
                    reference1[0] = database.getReference("Count");

                    reference1[0].child(role).setValue(count[0] + 1);
                } else {
                    reference1[0] = database.getReference("Count");

                    reference1[0].child(role).setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        final DatabaseReference[] reference = {database.getReference("Count").child("total")};

        reference[0].addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                count[0] = dataSnapshot.getValue(Integer.class);
                if (count[0] != null) {
                    reference[0] = database.getReference("Count");

                    reference[0].child("total").setValue(count[0] + 1);
                } else {
                    reference[0] = database.getReference("Count");

                    reference[0].child("total").setValue(1);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private static final String ALGORITHM = "AES";
    private static final String KEY = "1Hbfh667adfDEJ78";

    public static String encrypt(String value) throws Exception {
        Key key = generateKey();
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] encryptedByteValue = cipher.doFinal(value.getBytes(StandardCharsets.UTF_8));
        String encryptedValue64 = Base64.encodeToString(encryptedByteValue, Base64.DEFAULT);
        return encryptedValue64;

    }

    private static Key generateKey() throws Exception {
        Key key = new SecretKeySpec(KEY.getBytes(), ALGORITHM);
        return key;
    }
}