package com.javiersantos.mlmanager.utils;

import android.view.View;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.NativeExpressAdView;

/**
 * Created by hi on 1/5/17.
 */

public class AdsUtils {
  public static void loadNativeAds(final NativeExpressAdView adView){
    //Load ads
    AdRequest request = new AdRequest.Builder().addTestDevice("7F02914F9C4D5DEB8EF81FD66BFA0A0A")
        .build();
    adView.loadAd(request);
    adView.setAdListener(new AdListener() {
      @Override
      public void onAdLoaded() {
        super.onAdLoaded();
        adView.setVisibility(View.VISIBLE);
      }

      @Override
      public void onAdFailedToLoad(int i) {
        super.onAdFailedToLoad(i);
        adView.setVisibility(View.GONE);
      }
    });
  }
}