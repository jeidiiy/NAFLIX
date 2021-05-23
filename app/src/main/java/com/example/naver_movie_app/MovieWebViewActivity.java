package com.example.naver_movie_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MovieWebViewActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_web_view);

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        WebView webView = findViewById(R.id.movieWebView);
        webView.setWebViewClient(new MovieWebView());

        WebSettings webSet = webView.getSettings();
        webSet.setBuiltInZoomControls(true);
        webSet.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }

    static class MovieWebView extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            return super.shouldOverrideUrlLoading(view, url);
        }
    }
}
