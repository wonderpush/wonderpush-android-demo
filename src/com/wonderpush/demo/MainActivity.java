package com.wonderpush.demo;

import java.util.Properties;

import com.wonderpush.sdk.WonderPush;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                TextView label = (TextView) findViewById(R.id.lblDescription);
                label.setText(R.string.description);
            }
        }, new IntentFilter(WonderPush.INTENT_INTIALIZED));

        try {
            Properties props = new Properties();
            props.load(getResources().openRawResource(R.raw.credentials));
            WonderPush.initialize(this, props.getProperty("clientId"), props.getProperty("clientSecret"), null);
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Failed to load WonderPush credentials for initialization", ex);
            Toast.makeText(this, "Failed to load WonderPush credentials: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }

        setContentView(R.layout.activity_main);
    }

    public void btnFirstVisit_onClick(View button) {
        WonderPush.trackEvent("firstVisit", null);
    }

    public void btnNewsRead_onClick(View button) {
        WonderPush.trackEvent("newsRead", null);
    }

    public void btnGameOver_onClick(View button) {
        WonderPush.trackEvent("gameOver", null);
    }

    public void btnLike_onClick(View button) {
        WonderPush.trackEvent("like", null);
    }

    public void btnAddToCart_onClick(View button) {
        WonderPush.trackEvent("addToCart", null);
    }

    public void btnPurchase_onClick(View button) {
        WonderPush.trackEvent("purchase", null);
    }

    public void btnNearEiffelTower_onClick(View button) {
        // TODO Fake location
        WonderPush.trackEvent("geofencing", null);
    }

    public void btnNearLouvre_onClick(View button) {
        // TODO Fake location
        WonderPush.trackEvent("geofencing", null);
    }

    public void btnInactiveUser_onClick(View button) {
        WonderPush.trackEvent("inactivity", null);
    }

}
