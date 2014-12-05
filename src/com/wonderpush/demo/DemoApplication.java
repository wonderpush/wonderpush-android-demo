package com.wonderpush.demo;

import com.wonderpush.sdk.WonderPush;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PatternMatcher;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Example notification button action `method` receiver
        IntentFilter exampleMethodIntentFilter = new IntentFilter();
        exampleMethodIntentFilter.addAction(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_ACTION);
        exampleMethodIntentFilter.addDataScheme(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_SCHEME);
        exampleMethodIntentFilter.addDataAuthority(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_AUTHORITY, null);
        exampleMethodIntentFilter.addDataPath("/example", PatternMatcher.PATTERN_LITERAL); // Note: prepend a / to the actual method name
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String arg = intent.getStringExtra(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_EXTRA_ARG);
                Toast.makeText(context, "Example method successfully called with arg: " + arg,  Toast.LENGTH_LONG).show();
            }
        }, exampleMethodIntentFilter);

        // Catch-all notification button action `method` receiver, to show the user that a method was called
        IntentFilter catchallMethodIntentFilter = new IntentFilter();
        catchallMethodIntentFilter.addAction(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_ACTION);
        catchallMethodIntentFilter.addDataScheme(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_SCHEME);
        catchallMethodIntentFilter.addDataAuthority(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_AUTHORITY, null);
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String method = intent.getStringExtra(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_EXTRA_METHOD);
                if ("example".equals(method)) {
                    // The above receiver already took care of the "example" method
                    return;
                }
                String arg = intent.getStringExtra(WonderPush.INTENT_NOTIFICATION_BUTTON_ACTION_METHOD_EXTRA_ARG);
                Toast.makeText(context, "Method " + method + " called with arg: " + arg,  Toast.LENGTH_LONG).show();
            }
        }, catchallMethodIntentFilter);

        WonderPush.initialize(this);
    }

}
