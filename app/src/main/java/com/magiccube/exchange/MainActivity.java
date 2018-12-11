package com.magiccube.exchange;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import com.magiccube.exchange.web.WebActivity;

import static android.content.ContentValues.TAG;
public class MainActivity extends Activity {

    public static WebView webview;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        Bundle bundle = new Bundle();

        Uri uri = getIntent().getData();
        if (uri != null) {
            String url = uri.getQueryParameter("url");
            Log.e(TAG, "url: " + url);
            bundle.putString("url",url);
        } else {
            bundle.putString("url","https://exchange.magiccube.io");
        }
        intent.putExtras(bundle);
        startActivity(intent);
        MainActivity.this.finish();

    }
}