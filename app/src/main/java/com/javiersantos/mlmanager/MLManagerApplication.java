package com.javiersantos.mlmanager;

import android.app.Application;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.javiersantos.mlmanager.utils.AppPreferences;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.iconics.Iconics;

public class MLManagerApplication extends Application {
    private static AppPreferences sAppPreferences;
    private static boolean isPro;
    static MLManagerApplication mInstance;
    InterstitialAd mInterstitialAd;

    @Override
    public void onCreate() {
        super.onCreate();

        // Load Shared Preference
        sAppPreferences = new AppPreferences(this);

        // Check if there is the Pro version
        isPro = this.getPackageName().equals(getProPackage());

        // Register custom fonts like this (or also provide a font definition file)
        Iconics.registerFont(new GoogleMaterial());

        //Init full ads
        initFullAds();

        mInstance = this;
    }

    public static AppPreferences getAppPreferences() {
        return sAppPreferences;
    }

    /**
     * Retrieve APK Manager Pro Pro
     * @return true for APK Manager Pro Pro, false otherwise
     */
    public static Boolean isPro() {
        return isPro;
    }

    public static void setPro(Boolean res) {
        isPro = res;
    }

    public static String getProPackage() {
        return "com.ikame.apk.pro";
    }

    public static MLManagerApplication getInstance(){
        return mInstance;
    }

    void initFullAds(){
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId(getString(R.string.ads_intersitital_id));

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                requestNewInterstitial();
            }
        });

        requestNewInterstitial();
    }

    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
            .build();

        mInterstitialAd.loadAd(adRequest);
    }
    public void showFullAds(){
        if(mInterstitialAd.isLoaded()){
            mInterstitialAd.show();
        }
    }
}
