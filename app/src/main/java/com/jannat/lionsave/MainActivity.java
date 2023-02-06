package com.jannat.lionsave;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.startapp.sdk.adsbase.StartAppAd;
import com.startapp.sdk.adsbase.StartAppSDK;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

      StartAppSDK.setTestAdsEnabled(true);
        StartAppAd.disableSplash();
    }

    public void startGame(View view) {

        GameView gameView = new GameView(this);
        setContentView(gameView);
        StartAppAd.showAd(MainActivity.this);
    }
}