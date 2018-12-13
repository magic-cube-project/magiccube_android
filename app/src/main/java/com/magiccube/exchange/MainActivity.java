package com.magiccube.exchange;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.webkit.WebView;

import com.magiccube.exchange.web.WebActivity;

import static android.content.ContentValues.TAG;

public class MainActivity extends Activity {

    public static final int CODE_FOR_WRITE_PERMISSION = 1;
    public static WebView webview;


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE}, MainActivity.CODE_FOR_WRITE_PERMISSION);

        } else {
            load();
        }



    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        //通过requestCode来识别是否同一个请求
        if (requestCode == CODE_FOR_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                load();
            } else {
                //用户不同意，向用户展示该权限作用
                                                                                  load();

            }
        }
    }

    public void load() {
        Intent intent = new Intent(MainActivity.this, WebActivity.class);
        Bundle bundle = new Bundle();
        Uri uri = getIntent().getData();
        if (uri != null) {
            String url = uri.getQueryParameter("url");
            Log.e(TAG, "url: " + url);
            bundle.putString("url", url);
        } else {
            bundle.putString("url", "https://exchange.magiccube.io");
        }
        intent.putExtras(bundle);
        startActivity(intent);
        MainActivity.this.finish();
    }
}