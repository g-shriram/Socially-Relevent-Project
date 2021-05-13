package com.gprs.srp;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Environment;
import android.view.WindowManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class pdfViewer extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private static final String TAG = "PDF";
    Integer pageNumber = 0;
    String text;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_pdf_viewer);

        progressDialog = new ProgressDialog(this);

        progressDialog.setMessage("Loading..."); // Setting Message
        progressDialog.setTitle("Please Wait !"); // Setting Title
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner

        progressDialog.setCancelable(true);
        progressDialog.show();
        String extStorageDirectory = Environment.getExternalStorageDirectory()
                .toString();

        WebView webView = findViewById(R.id.webv1);

        Intent intent = getIntent();
        text = intent.getStringExtra("text");

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowContentAccess(false);
        webView.setClickable(false);
        webView.setLongClickable(false);

        webView.loadUrl(text);

        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                progressDialog.show(); // Display Progress Dialog

            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                super.onPageFinished(view, url);
                progressDialog.hide();
            }

            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                progressDialog.hide();
                handler.proceed(); // Ignore SSL certificate errors
            }

            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                progressDialog.hide();
                try {
                    webView.stopLoading();
                } catch (Exception e) {
                }

                if (webView.canGoBack()) {
                    webView.goBack();
                }

                webView.loadUrl("about:blank");
                AlertDialog alertDialog = new AlertDialog.Builder(pdfViewer.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Server not Available ! \nCheck your internet connection and try again.");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alertDialog.show();
                super.onReceivedError(webView, errorCode, description, failingUrl);
            }


        });

    }


}