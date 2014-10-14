package com.wonderpush.demo;

import java.util.Properties;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.wonderpush.sdk.WonderPush;
import com.wonderpush.sdk.WonderPushInitializer;

public class WonderPushInitializerImpl implements WonderPushInitializer {

    public void initialize(Context context) {
        try {
            Properties props = new Properties();
            props.load(context.getResources().openRawResource(R.raw.credentials));
            WonderPush.initialize(context, props.getProperty("clientId"), props.getProperty("clientSecret"));
        } catch (Exception ex) {
            Log.e(this.getClass().getSimpleName(), "Failed to load WonderPush credentials for initialization", ex);
            Toast.makeText(context, "Failed to load WonderPush credentials: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
