package com.wonderpush.demo;

import java.lang.reflect.Method;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wonderpush.sdk.WonderPush;

public class MainActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_main);

        if (WonderPush.isReady()) {
            TextView label = (TextView) findViewById(R.id.lblDescription);
            label.setText(R.string.description);
        } else {
            LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    TextView label = (TextView) findViewById(R.id.lblDescription);
                    label.setText(R.string.description);
                }
            }, new IntentFilter(WonderPush.INTENT_INTIALIZED));
        }

        mockLocationAvailable = checkCallingOrSelfPermission("android.Manifest.permission.ACCESS_MOCK_LOCATION") == PackageManager.PERMISSION_GRANTED;
        if (checkCallingOrSelfPermission("android.Manifest.permission.ACCESS_COARSE_LOCATION") != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ActivityCompat.requestPermissions(this, new String[]{
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        android.Manifest.permission.ACCESS_FINE_LOCATION
                }, 0);
            }
        }
    }

    private boolean checkMockLocationAndAlert() {
        if (!mockLocationAvailable) {
            new AlertDialog.Builder(this)
                    .setIcon(R.mipmap.ic_launcher)
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem toggle = menu.findItem(R.id.mnuMainToggleNotificationEnabled);
        if (toggle != null) {
            toggle.setChecked(WonderPush.isSubscribedToNotifications());
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.mnuMainReadInstallationCustom) {
            Toast.makeText(this, String.valueOf(WonderPush.getProperties()), Toast.LENGTH_LONG).show();
        } else if (id == R.id.mnuMainOpenChildActivity) {
            startActivity(new Intent().setClass(this, ChildActivity.class));
        } else if (id == R.id.mnuMainToggleNotificationEnabled) {
            if (WonderPush.isSubscribedToNotifications()) {
                WonderPush.unsubscribeFromNotifications();
            } else {
                WonderPush.subscribeToNotifications();
            }
            item.setChecked(WonderPush.isSubscribedToNotifications());
        } else if (id == R.id.mnuMainSetUserId) {
            final EditText txtUserId = new EditText(this);
            txtUserId.setText(WonderPush.getUserId());
            txtUserId.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            txtUserId.setSingleLine();
            txtUserId.setSelectAllOnFocus(true);
            new android.support.v7.app.AlertDialog.Builder(this)
                    .setTitle(R.string.titleMainSetUserIdDialog)
                    .setView(txtUserId)
                    .setPositiveButton(R.string.btnMainSetUserIdDialogOK, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            WonderPush.setUserId(TextUtils.isEmpty(txtUserId.getText().toString()) ? null : txtUserId.getText().toString());
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.btnMainSetUserIdDialogCancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else if (id == R.id.mnuMainOpenTreeA1Activity) {
            startActivity(new Intent().setClass(this, TreeA1Activity.class));
        } else if (id == R.id.mnuMainOpenTreeA2Activity) {
            startActivity(new Intent().setClass(this, TreeA2Activity.class));
        } else if (id == R.id.mnuMainOpenOrphanActivity) {
            startActivity(new Intent().setClass(this, OrphanActivity.class));
        } else if (id == R.id.mnuMainPreferences) {
            startActivity(new Intent().setClass(this, PreferencesActivity.class));
        }
        return super.onOptionsItemSelected(item);
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

    public void btnGenderMale_onClick(View button) {
        try {
            WonderPush.putProperties(new JSONObject("{\"string_gender\":\"male\"}}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void btnGenderFemale_onClick(View button) {
        try {
            WonderPush.putProperties(new JSONObject("{\"string_gender\":\"female\"}}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void btnGenderBoth_onClick(View button) {
        try {
            WonderPush.putProperties(new JSONObject("{\"string_gender\":[\"male\",\"female\"]}}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void btnGenderRemove_onClick(View button) {
        try {
            WonderPush.putProperties(new JSONObject("{\"string_gender\":null}"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void btnTagGet_onClick(View button) {
        Set<String> tags = WonderPush.getTags();
        Toast.makeText(this, tags.toString(), Toast.LENGTH_LONG).show();
    }

    public void btnTagClear_onClick(View button) {
        WonderPush.removeAllTags();
    }

    public void btnTagAddFooBar_onClick(View button) {
        WonderPush.addTag("foo", "bar");
    }

    public void btnTagRemoveFooBar_onClick(View button) {
        WonderPush.removeTag("foo", "bar");
    }

    public void btnTagAddFoo_onClick(View button) {
        WonderPush.addTag("foo");
    }

    public void btnTagRemoveFoo_onClick(View button) {
        WonderPush.removeTag("foo");
    }

    public void btnTagHasFoo_onClick(View button) {
        Toast.makeText(this, WonderPush.hasTag("foo") ? "YES" : "NO", Toast.LENGTH_LONG).show();
    }

    public void btnTagAddBar_onClick(View button) {
        WonderPush.addTag("bar");
    }

    public void btnTagRemoveBar_onClick(View button) {
        WonderPush.removeTag("bar");
    }

    public void btnTagHasBar_onClick(View button) {
        Toast.makeText(this, WonderPush.hasTag("bar") ? "YES" : "NO", Toast.LENGTH_LONG).show();
    }

}
