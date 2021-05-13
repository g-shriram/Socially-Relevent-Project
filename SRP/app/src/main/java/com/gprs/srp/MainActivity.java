package com.gprs.srp;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.data.BarEntry;
import com.ibm.cloud.sdk.core.http.Response;
import com.ibm.cloud.sdk.core.http.ServiceCall;
import com.ibm.cloud.sdk.core.security.IamAuthenticator;
import com.ibm.watson.assistant.v2.Assistant;
import com.ibm.watson.assistant.v2.model.CreateSessionOptions;
import com.ibm.watson.assistant.v2.model.DialogNodeOutputOptionsElement;
import com.ibm.watson.assistant.v2.model.MessageInput;
import com.ibm.watson.assistant.v2.model.MessageOptions;
import com.ibm.watson.assistant.v2.model.MessageResponse;
import com.ibm.watson.assistant.v2.model.RuntimeResponseGeneric;
import com.ibm.watson.assistant.v2.model.SessionResponse;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

import pl.droidsonroids.gif.GifImageView;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {

    private static final int RECORD_REQUEST_CODE = 101;
    GifImageView gifImageView;
    ImageView talked, listen;
    private TextToSpeech tts;
    Intent speechRecognizerIntent;
    String lastSpoken = "Hi hello,Welcome";

    SharedPreferences pref;
    SharedPreferences.Editor editor;


    SQLiteHelper helper;
    SQLiteDatabase db;

    public static final Integer RecordAudioRequestCode = 1;
    private SpeechRecognizer speechRecognizer;
    private TextView editText;
    private Button micButton;
    private Interpreter interpreter;
    private Handler handler;
    private TextClassificationClient client;
    CountDownTimer y;
    private Assistant watsonAssistant;
    private Response<SessionResponse> watsonAssistantSession;
    private String lastSpoken1="";
    private ArrayList<String> ques;
    int lis=30;

    private boolean checkInternetConnection() {
        // get Connectivity Manager object to check connection
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        // Check for network connections
        if (isConnected) {
            return true;
        } else {
            Toast.makeText(this, " No Internet Connection available ", Toast.LENGTH_LONG).show();
            return false;
        }

    }

    private void createServices() {
        if (checkInternetConnection()) {
            watsonAssistant = new Assistant("2019-02-28", new IamAuthenticator("S6C-4uOqeJyJzNaRBGP2PEp7PSJuNZ9C_OciE5JO3KoS"));
            watsonAssistant.setServiceUrl("https://api.eu-gb.assistant.watson.cloud.ibm.com/instances/fff0d44c-bc02-4bdc-965e-b83674194106");
        }
    }

    // Sending a message to Watson Assistant Service
    private void sendMessage(String mes, boolean init) {

        if (init)
            new MainActivity.send().execute(mes.trim(), "initial");
        else {
            new MainActivity.send().execute(mes.trim(), "notinitial");
        }

    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences("user", 0); // 0 - for private mode
        editor=pref.edit();
        createServices();
           sendMessage("", true);

        ques=new ArrayList<>();
        helper=new SQLiteHelper(this);
        db=helper.getReadableDatabase();

        ques.add("Have you ever experienced a terrible occurrence that has impacted you significantly? Examples may include, but aren’t necessarily limited to: being the victim of armed assault; witnessing a tragedy happen to someone else; surviving a sexual assault, or living through a natural disaster?");
        ques.add("Do you ever feel that you’ve been affected by feelings of edginess, anxiety, or nerves?");
        ques.add("Have you experienced a week or longer of lower-than-usual interest in activities that you usually enjoy? Examples might include work, exercise, or hobbies.");
        ques.add("Have you ever experienced an ‘attack’ of fear, anxiety, or panic?");
        ques.add("Do feelings of anxiety or discomfort around others bother you?\",\"How would you describe your appetite over the past X weeks? Have your eating habits altered in any way?");
        ques.add("Could you tell me about any times over the past few months that you’ve been bothered by low feelings, stress, or sadness?");
        ques.add("How frequently have you had little pleasure or interest in the activities you usually enjoy? Would you tell me more?");


        Cursor c=helper.speech_reportChart2(db);

        while (c.moveToNext()) {
        editor.putFloat("pos",c.getFloat(0));
            editor.putFloat("neg",c.getFloat(1));


            if(c.getFloat(0)<c.getFloat(1)){
                if(Math.abs(c.getFloat(0)-c.getFloat(1))<=0.1)
                    editor.putInt("status",1);
                else if(Math.abs(c.getFloat(0)-c.getFloat(1))<=0.2)
                    editor.putInt("status",2);
                else if(Math.abs(c.getFloat(0)-c.getFloat(1))<=0.3)
                    editor.putInt("status",3);
                else
                    editor.putInt("status",4);


            }
            else
                editor.putInt("status",0);
                editor.apply();
        }

        findViewById(R.id.repeat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                speakOut(lastSpoken);
            }
        });


        client = new TextClassificationClient(getApplicationContext());
        handler = new Handler();


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }


        tts = new TextToSpeech(this, this);

        tts.setSpeechRate(1f);


        editText = findViewById(R.id.text);
        listen = findViewById(R.id.listen);
        micButton = findViewById(R.id.button);
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);

      y = new CountDownTimer(10000, 1000) {
            public void onTick(long millisUntilFinished) {
                if(lis>=1) {
                    lis--;
                }
                else {
                        Random rand = new Random();
                        speakOut(ques.get(rand.nextInt(ques.size())));
                        lis=30;
                    }
                }
            public void onFinish() {
              y.start();
            }
        };
        y.start();

        speechRecognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());

        speechRecognizer.setRecognitionListener(new RecognitionListener() {

            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {
                talked.setImageDrawable(getDrawable(R.drawable.talking));
                listen.setVisibility(View.VISIBLE);
                lis=30;
            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {
                speechRecognizer.stopListening();
                talked.setImageDrawable(getDrawable(R.drawable.talked));
                listen.setVisibility(View.INVISIBLE);

                lis=30;

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                micButton.setEnabled(true);
                ArrayList<String> data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                classify(data.get(0));
                lastSpoken1=data.get(0);
                sendMessage(data.get(0), false);
            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });


        micButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP) {

                }
                if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {

                    speechRecognizer.startListening(speechRecognizerIntent);
                }
                return false;
            }
        });

        gifImageView = findViewById(R.id.talk);
        talked = findViewById(R.id.talked);
        gifImageView.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onInit(int status) {

        if (status == TextToSpeech.SUCCESS) {
            int result = tts.setLanguage(Locale.US);
            new speakThread().start();
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                Toast.makeText(getApplicationContext(), "Language not supported", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(getApplicationContext(), "Init failed", Toast.LENGTH_SHORT).show();
        }
    }


    private class send extends AsyncTask<String, Void, String> {

        private boolean initialRequest = false;


        @Override
        protected String doInBackground(String... params) {

            final String inputmessage = params[0];

            Thread thread = new Thread(new Runnable() {
                public void run() {
                    try {
                        if (watsonAssistantSession == null) {
                            ServiceCall<SessionResponse> call = watsonAssistant.createSession(new CreateSessionOptions.Builder().assistantId("6e39a81c-6541-4227-9091-c2da2ac81f54").build());
                            watsonAssistantSession = call.execute();
                        }

                        MessageInput input = new MessageInput.Builder()
                                .text(inputmessage)
                                .build();
                        MessageOptions options = new MessageOptions.Builder()
                                .assistantId("6e39a81c-6541-4227-9091-c2da2ac81f54")
                                .input(input)
                                .sessionId(watsonAssistantSession.getResult().getSessionId())
                                .build();
                        Response<MessageResponse> response = watsonAssistant.message(options).execute();
                        Log.i("response", "run: " + response.getResult());
                        if (response != null &&
                                response.getResult().getOutput() != null &&
                                !response.getResult().getOutput().getGeneric().isEmpty()) {

                            List<RuntimeResponseGeneric> responses = response.getResult().getOutput().getGeneric();

                            for (RuntimeResponseGeneric r : responses) {
                                switch (r.responseType()) {
                                    case "text":

                                        if(r.text().equals("activity")){
                                            lastSpoken="Getting some activities for you.";
                                            speakOut("Getting some activities for you.");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            startActivity(new Intent(MainActivity.this,Recommend.class));                                                }
                                                    }, 3000);
                                                }
                                            });

                                        }
                                        else if(r.text().equals("counselling")){
                                            lastSpoken="Opening Counselling window.";
                                            speakOut("Opening Counselling window.");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            startActivity(new Intent(MainActivity.this,Counselling.class));                                                }
                                                    }, 3000);
                                                }
                                            });

                                        }
                                        else if(r.text().equals("report")){
                                            lastSpoken="Generating your report.";
                                            speakOut("Generating your report.");
                                            runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    new Handler().postDelayed(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            startActivity(new Intent(MainActivity.this,report.class));                                                }
                                                    }, 3000);
                                                }
                                            });

                                        }
                                        else{
                                            lastSpoken=r.text();
                                            speakOut(r.text());
                                        }

                                        break;

                                    case "option":
                                        String title = r.title();
                                        String OptionsOutput = "";
                                        for (int i = 0; i < r.options().size(); i++) {
                                            DialogNodeOutputOptionsElement option = r.options().get(i);
                                            OptionsOutput = OptionsOutput + option.getLabel() + "\n";

                                        }
                                        break;

                                    case "image":

                                        break;
                                    default:
                                        Log.e("Error", "Unhandled message type");
                                }
                            }

                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            thread.start();


            return "Did translate";
        }
    }

    private void speakOut(String speech) {



        tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {


            @Override
            public void onStart(String s) {

            }

            @Override
            public void onDone(String s) {
            }

            @Override
            public void onError(String s) {


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Error ", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Bundle params = new Bundle();
        params.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");

        String text = speech;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, params, "Dummy String");
            lis=60;
        }

    }


    private void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.RECORD_AUDIO},RecordAudioRequestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RecordAudioRequestCode && grantResults.length > 0 ){
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permission Granted",Toast.LENGTH_SHORT).show();
        }
    }

    CountDownTimer x;
    class speakThread extends Thread{
        long minPrime;

        speakThread() {

        }

        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    x = new CountDownTimer(20000, 500) {
                        public void onTick(long millisUntilFinished) {
                            if (tts.isSpeaking()) {
                                gifImageView.setVisibility(View.VISIBLE);
                                talked.setVisibility(View.INVISIBLE);
                            } else {
                                gifImageView.setVisibility(View.INVISIBLE);
                                talked.setVisibility(View.VISIBLE);
                            }
                        }

                        public void onFinish() {
                            x.start();
                        }
                    };
                    x.start();


                }
            });

        }

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        speechRecognizer.destroy();
    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.v("textclassify", "onStart");
        handler.post(
                () -> {
                    client.load();
                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("textclassify", "onStop");
        handler.post(
                () -> {
                    client.unload();
                });
    }



    private void classify(final String text) {
        handler.post(
                () -> {
                    // Run text classification with TF Lite.
                    try{
                        List<TextClassificationClient.Result> results = client.classify(text);
                        showResult(text, results);
                    }
                    catch (Exception e){

                    }

                    // Show classification result on screen

                });
    }

    /** Show classification result on the screen. */
    private void showResult(final String inputText, final List<TextClassificationClient.Result> results) {
        // Run on UI thread as we'll updating our app UI
        runOnUiThread(
                () -> {
                    String textToShow = "";
                    for (int i = 0; i < results.size(); i++) {
                        TextClassificationClient.Result result = results.get(i);
                        textToShow +=
                                String.format("%s: %s ; ", result.getTitle(), result.getConfidence());
                    }



                    // Append the result to the UI.
                    editText.setText(textToShow);

                    if(results.get(0).getTitle().equals("happy"))
                helper.addText(db,lastSpoken1,results.get(0).getConfidence(),results.get(1).getConfidence());
                  else
                        helper.addText(db,lastSpoken1,results.get(1).getConfidence(),results.get(0).getConfidence());

                    Cursor c=helper.speech_reportChart2(db);

                    while (c.moveToNext()) {
                        editor.putFloat("pos",c.getFloat(0));
                        editor.putFloat("neg",c.getFloat(1));
                        if(c.getFloat(0)<c.getFloat(1)){
                            if(Math.abs(c.getFloat(0)-c.getFloat(1))<=0.1)
                                editor.putInt("status",1);
                            else if(Math.abs(c.getFloat(0)-c.getFloat(1))<=0.2)
                                editor.putInt("status",2);
                            else if(Math.abs(c.getFloat(0)-c.getFloat(1))<=0.3)
                                editor.putInt("status",3);
                            else
                                editor.putInt("status",4);


                        }
                        else
                            editor.putInt("status",0);
                        editor.apply();
                    }
                });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case RECORD_REQUEST_CODE: {
                if (resultCode == RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (!result.get(0).isEmpty())
                        sendMessage(result.get(0), false);
                }

                break;

            }
            default:
                start();
        }
    }

    private void start() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak the word");
        try {
            startActivityForResult(intent, RECORD_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(MainActivity.this,
                    "Sorry your device not supported",
                    Toast.LENGTH_SHORT).show();
        }
    }


}