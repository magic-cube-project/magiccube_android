package com.magiccube.exchange;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.magiccube.exchange.web.WebActivity;

public class MainActivity extends Activity {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("url","https://exchange.magiccube.io");
        intent.putExtras(bundle);
        startActivity(intent);
        MainActivity.this.finish();

    }
}