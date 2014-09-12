package com.wonderpush.demo;

import java.util.Properties;

import com.wonderpush.sdk.WonderPush;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

}
