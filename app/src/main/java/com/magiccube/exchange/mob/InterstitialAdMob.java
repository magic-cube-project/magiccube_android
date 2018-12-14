package com.magiccube.exchange.mob;

import android.content.Context;
import android.os.CountDownTimer;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;

public class InterstitialAdMob {
    private InterstitialAd interstitialAd;
    private CountDownTimer countDownTimer;
    private Context context;
    public InterstitialAdMob(Context context){
        this.context = context;
        MobileAds.initialize(context, "ca-app-pub-3940256099942544~3347511713");
        // Create the InterstitialAd and set the adUnitId.
        interstitialAd = new InterstitialAd(context);
        // Defined in res/values/strings.xml
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
    }
    public void show(){
        if (interstitialAd != null && interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    public void timeshow(final long milliseconds){
        loadAd();
        // Create the game timer, which counts down to the end of the level
        // and shows the "retry" button.
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }

        countDownTimer = new CountDownTimer(milliseconds, 50) {
            @Override
            public void onTick(long millisUnitFinished) {
            }

            @Override
            public void onFinish() {

                show();
            }
        };

        countDownTimer.start();
    }

    public void loadAd(){
        if (!interstitialAd.isLoading() && !interstitialAd.isLoaded()) {
            AdRequest adRequest = new AdRequest.Builder().build();
            interstitialAd.loadAd(adRequest);
        }
    }
}

