package com.magiccube.exchange.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

public class WebActivity extends Activity {
    private WebSettings webSettings = null;
    private WebView webview = null;
//    String[] urlArr = new String[100];

    private static final int REQUEST_CODE = 0x11;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // hideBottomUIMenu();
        // fullscreen();

        webview = new WebView(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        layout.addView(webview);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(point.x, point.y);
        params.leftMargin = 0;
        params.topMargin = 0;
        params.height = getHeight();

        webview.setLayoutParams(params);


        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", url);
                view.loadUrl(url);
                return true;
            }
        });

        if (Build.VERSION.SDK_INT >= 19) {
            webview.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webview.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }

        WebSettings settings = webview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);
        settings.setAllowFileAccess(true);
        settings.setAppCacheEnabled(true);
        settings.setUseWideViewPort(true);
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        settings.setLoadWithOverviewMode(true);
        settings.setDomStorageEnabled(true);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        settings.setUseWideViewPort(true);
        webview.setWebContentsDebuggingEnabled(true);
        settings.setPluginState(WebSettings.PluginState.ON);
        webview.setWebChromeClient(new WebChromeClient());


        String ua = webview.getSettings().getUserAgentString();
        webview.getSettings().setUserAgentString(ua+"; APP/JHLLO app_version:"+getLocalVersion());

//        webview.addJavascriptInterface(new WebTesk(this), "Android");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }

        webview.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.e("URL", url);
                if (url == null || url == "") {
                    return false;
                }
                if (!url.startsWith("http") && !url.startsWith("https")) {
                    {
                        //加载手机内置支付
                        toLoadInnerApp(url);
                        return true;
                    }
                } else {
                    return false;
                }
            }

        });


        Bundle bundleExtra = this.getIntent().getExtras();
        String url = bundleExtra.getString("url");
        webview.loadUrl(url);


        SoftKeyBoardListener.setListener(WebActivity.this, new SoftKeyBoardListener.OnSoftKeyBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
//                Toast.makeText(WebActivity.this, "键盘显示 高度" + height, Toast.LENGTH_SHORT).show();

                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int windowWidth = wm.getDefaultDisplay().getWidth();
                int windowHeight = getHeight() - height;

                ViewGroup.LayoutParams lp = webview.getLayoutParams();
                lp.width = windowWidth;
                lp.height = windowHeight;
                webview.setLayoutParams(lp);


            }

            @Override
            public void keyBoardHide(int height) {
//                Toast.makeText(WebActivity.this, "键盘隐藏 高度" + height, Toast.LENGTH_SHORT).show();
                WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
                int windowWidth = wm.getDefaultDisplay().getWidth();
                int windowHeight = getHeight();

                ViewGroup.LayoutParams lp = webview.getLayoutParams();
                lp.width = windowWidth;
                lp.height = windowHeight;
                webview.setLayoutParams(lp);

            }
        });

    }

    /**
     * 加载手机内置支付app
     *
     * @param url
     */
    private void toLoadInnerApp(String url) {
        try {
            Intent it = new Intent(Intent.ACTION_VIEW);
            it.setData(Uri.parse(url));
            startActivity(it);
        } catch (Exception e) {
            //这里需要处理 发生异常的情况
            //可能情况： 手机没有安装支付宝或者微信。或者安装支付宝或者微信但是版本过低
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        hideBottomUIMenu();

        //一些适配操作
//        WindowManager wm = getWindowManager();//Activity可以直接获取WindowManager
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = getHeight();

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
//        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
//            //横屏
//            windowWidth+=getNavigationBarHeight(this);
//            fullscreen();
//
//        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
//            //竖屏
//            windowHeight+=getNavigationBarHeight(this);
//            hideBottomUIMenu();
//        }

        ViewGroup.LayoutParams lp = webview.getLayoutParams();
        lp.width = windowWidth;
        lp.height = windowHeight;
        webview.setLayoutParams(lp);
    }

    public int getHeight() {
        Context context = this.getApplicationContext();
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        display.getMetrics(dm);
        int height = dm.heightPixels;
        return height;
    }


    public boolean isNavigationBarShow() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            Display display = getWindowManager().getDefaultDisplay();
            Point size = new Point();
            Point realSize = new Point();
            display.getSize(size);
            display.getRealSize(realSize);
            return realSize.y != size.y;
        } else {
            boolean menu = ViewConfiguration.get(this).hasPermanentMenuKey();
            boolean back = KeyCharacterMap.deviceHasKey(KeyEvent.KEYCODE_BACK);
            if (menu || back) {
                return false;
            } else {
                return true;
            }
        }
    }

    public  String getLocalVersion() {
        Context ctx = this.getApplicationContext();
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;
            Log.d("TAG", "本软件的版本号。。" + localVersion);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    protected void fullscreen() {
        //隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    /**
     * 监听Back键按下事件,方法2:
     * 在此处返回false,所以会继续传播该事件. 继续执行super.onKeyDown(keyCode, event);
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if(webview.canGoBack()) {
                webview.goBack();
                return false;
            }
            else {
                return  super.onKeyDown(keyCode, event);
            }

        }else {
            return  super.onKeyDown(keyCode, event);
        }
    }


}
