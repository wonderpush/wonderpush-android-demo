package com.wonderpush.demo;

import java.lang.reflect.Method;
import java.util.Properties;

import com.wonderpush.sdk.WonderPush;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    private final static String MOCK_LOCATION_PROVIDER = "wonderpush-demo";
    private final static float MOCK_LOCATION_EIFFEL_TOWER_LAT = 48.858222f;
    private final static float MOCK_LOCATION_EIFFEL_TOWER_LON = 2.2945f;
    private final static float MOCK_LOCATION_LOUVRE_LAT = 48.860339f;
    private final static float MOCK_LOCATION_LOUVRE_LON = 2.337599f;
    private final static float MOCK_LOCATION_ACCURACY = 3.f;

    private boolean mockLocationAvailable = false;

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

        mockLocationAvailable = checkCallingOrSelfPermission("android.Manifest.permission.ACCESS_MOCK_LOCATION") == PackageManager.PERMISSION_GRANTED;

        setContentView(R.layout.activity_main);
    }

    private boolean checkMockLocationAndAlert() {
        if (!mockLocationAvailable) {
            new AlertDialog.Builder(this)
                    .setIcon(R.drawable.ic_launcher)
                    .setTitle(R.string.noMockLocationAlertTitle)
                    .setMessage(R.string.noMockLocationAlertMessage)
                    .setPositiveButton(R.string.noMockLocationAlertOkButton, null)
                    .show();
        }
        return mockLocationAvailable;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mockLocationAvailable) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            try {
                if (locationManager.getProvider(MOCK_LOCATION_PROVIDER) != null) {
                    locationManager.removeTestProvider(MOCK_LOCATION_PROVIDER);
                }
            } catch (SecurityException ex) {
                locationManager.removeTestProvider(MOCK_LOCATION_PROVIDER);
            }
            locationManager.addTestProvider(MOCK_LOCATION_PROVIDER, false, false, false, false, false, false, false, Criteria.POWER_LOW, Criteria.ACCURACY_FINE);
        }
    }

    @Override
    protected void onPause() {
        if (mockLocationAvailable) {
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            setMockLocation(0, 0, -1); // poison last location, remembered by the passive location provider
            locationManager.removeTestProvider(MOCK_LOCATION_PROVIDER);
        }
        super.onPause();
    }

    // Helpers

    private void setMockLocation(float lat, float lon, float accuracy) {
        Location location = new Location(MOCK_LOCATION_PROVIDER);
        location.setLatitude(lat);
        location.setLongitude(lon);
        location.setAccuracy(accuracy);
        location.setTime(System.currentTimeMillis());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            try {
                Method locationJellyBeanFixMethod = Location.class.getMethod("makeComplete");
                if (locationJellyBeanFixMethod != null) {
                   locationJellyBeanFixMethod.invoke(location);
                }
            } catch (Exception ex) {
                Log.w(this.getClass().getSimpleName(), "Failed to complete mock location", ex);
            }
        }

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationManager.setTestProviderLocation(MOCK_LOCATION_PROVIDER, location);
    }

    // Button callbacks

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
        if (!checkMockLocationAndAlert()) {
            return;
        }
        setMockLocation(MOCK_LOCATION_EIFFEL_TOWER_LAT, MOCK_LOCATION_EIFFEL_TOWER_LON, MOCK_LOCATION_ACCURACY);
        WonderPush.trackEvent("geofencing", null);
    }

    public void btnNearLouvre_onClick(View button) {
        if (!checkMockLocationAndAlert()) {
            return;
        }
        setMockLocation(MOCK_LOCATION_LOUVRE_LAT, MOCK_LOCATION_LOUVRE_LON, MOCK_LOCATION_ACCURACY);
        WonderPush.trackEvent("geofencing", null);
    }

    public void btnInactiveUser_onClick(View button) {
        WonderPush.trackEvent("inactivity", null);
    }

}
