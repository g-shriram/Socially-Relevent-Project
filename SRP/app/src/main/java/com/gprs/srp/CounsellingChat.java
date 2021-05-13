package com.gprs.srp;

import android.Manifest;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CounsellingChat extends AppCompatActivity {

    EditText input;
    ListView chatlist;
    ArrayList<String> message;
    ArrayList<Integer> toggle;
    CustomCounsellingChatAdapter c;
    private String counsellornumber = "";
    Boolean counsellor = false;
    String mynumber = "";
    TextView name, role;
    UserRegistrationHelper u;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_counselling_chat);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        input = findViewById(R.id.inputmessage);
        chatlist = findViewById(R.id.chatlist);
        toggle = new ArrayList<>();
        message = new ArrayList<>();
        name = findViewById(R.id.name);
        role = findViewById(R.id.role);
        counsellor = getIntent().getBooleanExtra("couns", false);
        mynumber = getIntent().getStringExtra("my");
        counsellornumber = getIntent().getStringExtra("them");
        FirebaseDatabase.getInstance().getReference().child("counselling").child("active").child(counsellornumber + mynumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot == null || dataSnapshot.getValue() == null) {
                    findViewById(R.id.chatwindow).setVisibility(View.INVISIBLE);
                    findViewById(R.id.offline).setVisibility(View.VISIBLE);
                } else {
                    findViewById(R.id.chatwindow).setVisibility(View.VISIBLE);
                    findViewById(R.id.offline).setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (counsellor) {
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    final String currentDateTime = dateFormat.format(new Date()); // Find todays date
                    final CharSequence[] options = {"End Counselling", "End today's session", "Cancel"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(CounsellingChat.this);
                    builder.setTitle("Add Photo!");
                    builder.setItems(options, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int item) {
                            if (options[item].equals("End Counselling")) {
                                FirebaseDatabase.getInstance().getReference().child("Notification").child(counsellornumber).child("counselling").child(currentDateTime).setValue("You counselling is completed !");
                                FirebaseDatabase.getInstance().getReference().child("counselling").child("enable").child(counsellornumber).removeValue();
                                FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(mynumber).child(counsellornumber).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if (dataSnapshot != null) {
                                            CounsellorHelper c = dataSnapshot.getValue(CounsellorHelper.class);
                                            c.setTime(currentDateTime);
                                            FirebaseDatabase.getInstance().getReference().child("counselling").child("done").child(mynumber).child(currentDateTime).setValue(c);
                                            FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(mynumber).child(counsellornumber).removeValue();
                                            finish();
                                        }
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            } else if (options[item].equals("End today's session")) {
                                FirebaseDatabase.getInstance().getReference().child("Notification").child(counsellornumber).child("counselling").child(currentDateTime).setValue("You counselling session for today is ended !");
                                finish();
                            } else if (options[item].equals("Cancel")) {
                                dialog.dismiss();
                            }
                        }
                    });
                    builder.show();
                } else
                    finish();
            }
        });

        StorageReference mStorageRef = FirebaseStorage.getInstance().getReference();
        if (counsellornumber != null) {
            if (mStorageRef.child(counsellornumber) != null) {
                StorageReference sr = mStorageRef.child("proImg").child(counsellornumber + ".jpg");
                sr.getBytes(Long.MAX_VALUE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        ImageView proimg = findViewById(R.id.proimg);
                        proimg.setImageBitmap(bitmap);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle any errors
                    }
                });
            }
        }


        FirebaseDatabase.getInstance().getReference().child("Users").child(counsellornumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    u = dataSnapshot.getValue(UserRegistrationHelper.class);
                    name.setText(u.getFname());
                    role.setText(u.getRole());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        c = new CustomCounsellingChatAdapter(this, toggle, message);
        chatlist.setAdapter(c);

        input.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    // Perform action on key press
                    if (!input.getText().toString().isEmpty()) {
                        toggle.add(1);
                        message.add(input.getText().toString());
                        FirebaseDatabase.getInstance().getReference().child("counselling").child("message").child(mynumber + counsellornumber).setValue(input.getText().toString());
                        c.notifyDataSetChanged();
                        input.setText("");
                    }
                    return true;
                }
                return false;
            }
        });

        FirebaseDatabase.getInstance().getReference().child("counselling").child("message").child(counsellornumber + mynumber).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    toggle.add(0);
                    message.add(dataSnapshot.getValue(String.class));
                    c.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    if (checkSelfPermission(Manifest.permission.WRITE_CONTACTS)
                            != PackageManager.PERMISSION_GRANTED) {
                        requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS},
                                2);

                        // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                        // app-defined int constant

                        return;
                    }

                } else {
                    Toast.makeText(this, "Permission required to make vedio call", Toast.LENGTH_SHORT).show();
                }

            }
            break;
            case 2:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    try {
                        addContact("COVID19RELIEF", counsellornumber);
                    } catch (Exception e) {
                        Toast.makeText(this, "Whatsapp required to make vedio call", Toast.LENGTH_SHORT).show();
                    }
                }

                // other 'switch' lines to check for other
                // permissions this app might request
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.counsellingchatmenu, menu);
        RelativeLayout relativeLayout = findViewById(R.id.chatwindow);
        URL url = null;


        return true;
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getTitle().equals(getString(R.string.voice_call))) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + counsellornumber));
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }
        }
        if (item.getTitle().equals(getString(R.string.vedio_call))) {
            if (checkSelfPermission(Manifest.permission.READ_CONTACTS)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS},
                        1);
            } else
                try {
                    addContact("COVID19RELIEF", counsellornumber);
                } catch (Exception e) {
                    Toast.makeText(this, "Whatsapp required to make vedio call", Toast.LENGTH_SHORT).show();
                }

        }
        if (item.getTitle().equals("Info")) {
            if (u != null) {
                Bundle bundle = new Bundle();
                bundle.putString("name", u.getFname());
                bundle.putString("phone", u.getPhone());
                bundle.putString("email", u.getEmail());
                bundle.putString("role", u.getRole());
                bundle.putDouble("lat", u.getLat());
                bundle.putDouble("lon", u.getLon());

                BottomSheetDialogFragment f = new Bottomsheetcounsellingchatinfofragment();
                f.setArguments(bundle);
                f.show(getSupportFragmentManager(), "Dialog");
            }
        }
        return super.onOptionsItemSelected(item);
    }

    void contact() throws Exception {
        String contactNumber = counsellornumber; // to change with real value

        Cursor cursor = getContentResolver()
                .query(
                        ContactsContract.Data.CONTENT_URI,
                        new String[]{ContactsContract.Data._ID},
                        ContactsContract.RawContacts.ACCOUNT_TYPE + " = 'com.whatsapp' " +
                                "AND " + ContactsContract.Data.MIMETYPE + " = 'vnd.android.cursor.item/vnd.com.whatsapp.video.call' " +
                                "AND " + ContactsContract.CommonDataKinds.Phone.NUMBER + " LIKE '%" + contactNumber + "%'",
                        null,
                        ContactsContract.Contacts.DISPLAY_NAME
                );

        if (cursor == null) {
            // throw an exception
        }

        long id = -1;
        while (cursor.moveToNext()) {
            id = cursor.getLong(cursor.getColumnIndex(ContactsContract.Data._ID));
        }

        if (!cursor.isClosed()) {
            cursor.close();
        }

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);

        intent.setDataAndType(Uri.parse("content://com.android.contacts/data/" + id), "vnd.android.cursor.item/vnd.com.whatsapp.voip.call");
        intent.setPackage("com.whatsapp");

        startActivity(intent);
    }

    private void addContact(String name, String number) {
        Uri addContactsUri = ContactsContract.Data.CONTENT_URI;
        long rowContactId = getRawContactId();
        String displayName = name;
        insertContactDisplayName(addContactsUri, rowContactId, displayName);
        String phoneNumber = number;
        String phoneTypeStr = "Mobile";//work,home etc
        insertContactPhoneNumber(addContactsUri, rowContactId, phoneNumber, phoneTypeStr);
        try {
            contact();
        } catch (Exception e) {
            Toast.makeText(this, "Whatsapp required to make vedio call", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertContactDisplayName(Uri addContactsUri, long rawContactId, String displayName) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);

        // Put contact display name value.
        contentValues.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, displayName);

        getContentResolver().insert(addContactsUri, contentValues);

    }


    private long getRawContactId() {
        // Inser an empty contact.
        ContentValues contentValues = new ContentValues();
        Uri rawContactUri = getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, contentValues);
        // Get the newly created contact raw id.
        long ret = ContentUris.parseId(rawContactUri);
        return ret;
    }

    private void insertContactPhoneNumber(Uri addContactsUri, long rawContactId, String phoneNumber, String phoneTypeStr) {
        // Create a ContentValues object.
        ContentValues contentValues = new ContentValues();

        // Each contact must has an id to avoid java.lang.IllegalArgumentException: raw_contact_id is required error.
        contentValues.put(ContactsContract.Data.RAW_CONTACT_ID, rawContactId);

        // Each contact must has an mime type to avoid java.lang.IllegalArgumentException: mimetype is required error.
        contentValues.put(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);

        // Put phone number value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.NUMBER, phoneNumber);

        // Calculate phone type by user selection.
        int phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;

        if ("home".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
        } else if ("mobile".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
        } else if ("work".equalsIgnoreCase(phoneTypeStr)) {
            phoneContactType = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
        }
        // Put phone type value.
        contentValues.put(ContactsContract.CommonDataKinds.Phone.TYPE, phoneContactType);

        // Insert new contact data into phone contact list.
        getContentResolver().insert(addContactsUri, contentValues);

    }

    public static boolean deleteContact(Context ctx, String phone, String name) {
        Uri contactUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phone));
        Cursor cur = ctx.getContentResolver().query(contactUri, null, null, null, null);
        try {
            if (cur.moveToFirst()) {
                do {
                    if (cur.getString(cur.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME)).equalsIgnoreCase(name)) {
                        String lookupKey = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.LOOKUP_KEY));
                        Uri uri = Uri.withAppendedPath(ContactsContract.Contacts.CONTENT_LOOKUP_URI, lookupKey);
                        ctx.getContentResolver().delete(uri, null, null);
                        return true;
                    }

                } while (cur.moveToNext());
            }

        } catch (Exception e) {
            System.out.println(e.getStackTrace());
        } finally {
            cur.close();
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        if (counsellor) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            final String currentDateTime = dateFormat.format(new Date()); // Find todays date
            final CharSequence[] options = {"End Counselling", "End today's session", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(CounsellingChat.this);
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("End Counselling")) {
                        FirebaseDatabase.getInstance().getReference().child("Notification").child(counsellornumber).child("counselling").child(currentDateTime).setValue("You counselling is completed !");
                        FirebaseDatabase.getInstance().getReference().child("counselling").child("enable").child(counsellornumber).removeValue();
                        FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(mynumber).child(counsellornumber).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if (dataSnapshot != null) {
                                    CounsellorHelper c = dataSnapshot.getValue(CounsellorHelper.class);
                                    c.setTime(currentDateTime);
                                    FirebaseDatabase.getInstance().getReference().child("counselling").child("done").child(mynumber).child(currentDateTime).setValue(c);
                                    FirebaseDatabase.getInstance().getReference().child("counselling").child("schedule").child(mynumber).child(counsellornumber).removeValue();
                                    finish();
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });

                    } else if (options[item].equals("End today's session")) {
                        FirebaseDatabase.getInstance().getReference().child("Notification").child(counsellornumber).child("counselling").child(currentDateTime).setValue("You counselling session for today is ended !");
                        finish();
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } else
            finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            deleteContact(this, counsellornumber, "COVID19RELIEF");
        } catch (Exception e) {

        }
        FirebaseDatabase.getInstance().getReference().child("counselling").child("message").child(mynumber + counsellornumber).removeValue();

        FirebaseDatabase.getInstance().getReference().child("counselling").child("active").child(mynumber + counsellornumber).removeValue();
    }

    @Override
    protected void onStop() {
        super.onStop();
        FirebaseDatabase.getInstance().getReference().child("counselling").child("active").child(mynumber + counsellornumber).removeValue();
    }

    @Override
    protected void onPause() {
        super.onPause();
        FirebaseDatabase.getInstance().getReference().child("counselling").child("active").child(mynumber + counsellornumber).removeValue();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseDatabase.getInstance().getReference().child("counselling").child("active").child(mynumber + counsellornumber).setValue(true);

    }
}