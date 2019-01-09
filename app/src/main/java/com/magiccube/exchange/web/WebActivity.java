package com.magiccube.exchange.web;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;

import com.magiccube.exchange.MainActivity;
import com.magiccube.exchange.mob.InterstitialAdMob;
import com.magiccube.exchange.utils.APKVersionCodeUtils;

public class WebActivity extends Activity {
    private ValueCallback<Uri> uploadMessage;
    private ValueCallback<Uri[]> uploadMessageAboveL;
    private final static int FILE_CHOOSER_RESULT_CODE = 10000;
    private WebSettings webSettings = null;
    private WebView webview = null;
//    String[] urlArr = new String[100];

    private InterstitialAdMob interstitialAdMob;

    private static final int REQUEST_CODE = 0x11;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  //设置为强制竖屏

        webview = new WebView(this);
        MainActivity.webview = webview;
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        setContentView(layout);
        layout.addView(webview);

        Point point = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(point);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(point.x, point.y);
        params.leftMargin = 0;
        params.topMargin = 0;
        webview.setLayoutParams(params);
        String ua = webview.getSettings().getUserAgentString();
        webview.getSettings().setUserAgentString(ua + "; APP/JHELLO app_version: " + APKVersionCodeUtils.getVersionCode(this)+" "+APKVersionCodeUtils.getDeviceInfo(this));

        fullscreen();

        webview.setWebChromeClient(new WebChromeClient() {
            // For Android 5.0+
            @Override
            public boolean onShowFileChooser(WebView webView,
                                             ValueCallback<Uri[]> filePathCallback,
                                             FileChooserParams fileChooserParams) {
                uploadMessageAboveL = filePathCallback;
                openImageChooserActivity();

                return true;
            }

        });

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
        webview.getSettings().setBlockNetworkImage(false);//解决图片不显示
        settings.setPluginState(WebSettings.PluginState.ON);

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
                int windowHeight = wm.getDefaultDisplay().getHeight() - height;

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
                int windowHeight = wm.getDefaultDisplay().getHeight();

                ViewGroup.LayoutParams lp = webview.getLayoutParams();
                lp.width = windowWidth;
                lp.height = windowHeight;
                webview.setLayoutParams(lp);

            }
        });

//        interstitialAdMob = new InterstitialAdMob(this);
//
//        interstitialAdMob.timeshow(5000);


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


        //一些适配操作
        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();

        Configuration mConfiguration = this.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        ViewGroup.LayoutParams lp = webview.getLayoutParams();
        lp.width = windowWidth;
        lp.height = windowHeight;
        webview.setLayoutParams(lp);
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
        View rootView = this.getWindow().getDecorView();

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        int visibleHeight = r.height();

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        int windowWidth = wm.getDefaultDisplay().getWidth();
        int windowHeight = wm.getDefaultDisplay().getHeight();

        ViewGroup.LayoutParams lp = webview.getLayoutParams();
        lp.width = windowWidth;
        lp.height = visibleHeight;
        webview.setLayoutParams(lp);
    }

    /**
     * 监听Back键按下事件,方法2:
     * 在此处返回false,所以会继续传播该事件. 继续执行super.onKeyDown(keyCode, event);
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            if (webview.canGoBack()) {
                webview.goBack();
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }

        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    public void setUrl(String url) {
        webview.loadUrl(url);
    }

    private void openImageChooserActivity() {
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Image Chooser"), FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (null == uploadMessage && null == uploadMessageAboveL) return;
            Uri result = data == null || resultCode != RESULT_OK ? null : data.getData();
            if (uploadMessageAboveL != null) {
                onActivityResultAboveL(requestCode, resultCode, data);
            } else if (uploadMessage != null) {
                uploadMessage.onReceiveValue(result);
                uploadMessage = null;
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void onActivityResultAboveL(int requestCode, int resultCode, Intent intent) {
        if (requestCode != FILE_CHOOSER_RESULT_CODE || uploadMessageAboveL == null)
            return;
        Uri[] results = null;
        if (resultCode == Activity.RESULT_OK) {
            if (intent != null) {
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    results = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        results[i] = item.getUri();
                    }
                }
                if (dataString != null)
                    results = new Uri[]{Uri.parse(dataString)};
            }
        }
        uploadMessageAboveL.onReceiveValue(results);
        uploadMessageAboveL = null;
    }

}
