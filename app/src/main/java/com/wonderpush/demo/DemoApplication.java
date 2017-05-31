package com.wonderpush.demo;

import com.wonderpush.sdk.WonderPush;
import com.wonderpush.sdk.WonderPushChannelPreference;
import com.wonderpush.sdk.WonderPushUserPreferences;

import android.app.Application;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.PatternMatcher;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import java.util.Random;

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String oldPushToken = intent.getStringExtra(WonderPush.INTENT_PUSH_TOKEN_CHANGED_EXTRA_OLD_KNOWN_PUSH_TOKEN);
                String pushToken = intent.getStringExtra(WonderPush.INTENT_PUSH_TOKEN_CHANGED_EXTRA_PUSH_TOKEN);
                Toast.makeText(getApplicationContext(), "TOKEN CHANGED from " + oldPushToken + " to " + pushToken, Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter(WonderPush.INTENT_PUSH_TOKEN_CHANGED));
        // Inform of INTENT_NOTIFICATION_OPENED intents
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Toast.makeText(getApplicationContext(), "RECEIVED PUSH: " + String.valueOf(intent), Toast.LENGTH_LONG).show();
            }
        }, new IntentFilter(WonderPush.INTENT_NOTIFICATION_OPENED));

        // Example deeplink handles by application logic
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (!WonderPush.INTENT_NOTIFICATION_WILL_OPEN_EXTRA_NOTIFICATION_TYPE_DATA.equals(
                        intent.getStringExtra(WonderPush.INTENT_NOTIFICATION_WILL_OPEN_EXTRA_NOTIFICATION_TYPE))) {
                    Toast.makeText(getApplicationContext(), "Deeplink resolved programmatically", Toast.LENGTH_SHORT).show();
                    Intent openIntent = new Intent();
                    openIntent.setClass(context, NavigationActivity.class);
                    openIntent.fillIn(intent, 0);
                    openIntent.putExtra("resolvedProgrammatically", true);
                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntentWithParentStack(openIntent);
                    stackBuilder.startActivities();
                }
            }
        }, new IntentFilter(WonderPush.INTENT_NOTIFICATION_WILL_OPEN));

        // Example data notification handled by application logic
        LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (WonderPush.INTENT_NOTIFICATION_WILL_OPEN_EXTRA_NOTIFICATION_TYPE_DATA.equals(
                        intent.getStringExtra(WonderPush.INTENT_NOTIFICATION_WILL_OPEN_EXTRA_NOTIFICATION_TYPE))) {
                    Toast.makeText(getApplicationContext(), "Data notification received for " + getPackageManager().getApplicationLabel(getApplicationInfo()), Toast.LENGTH_SHORT).show();

                    // If you want to manually display a notification in response to a data notification, do the following
                    Intent pushNotif = intent.getParcelableExtra(WonderPush.INTENT_NOTIFICATION_WILL_OPEN_EXTRA_RECEIVED_PUSH_NOTIFICATION);
                    String message = pushNotif.getStringExtra("manual_display_message");
                    if (message != null) {
                        Toast.makeText(getApplicationContext(), "Building notification manually", Toast.LENGTH_SHORT).show();
                        Intent openIntent = new Intent(context, NavigationActivity.class);
                        // Copy the push notification payload
                        openIntent.putExtras(pushNotif);
                        // Copy the local intent extras so that the WonderPush SDK can track the click
                        openIntent.putExtras(intent);
                        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                                .setAutoCancel(true)
                                .setSmallIcon(R.drawable.notification_icon)
                                .setContentTitle(getPackageManager().getApplicationLabel(getApplicationInfo()) + " manual data notif")
                                .setContentText(message)
                                .setStyle(new NotificationCompat.BigTextStyle()
                                        .bigText(message))
                                .setContentIntent(pendingIntent);
                        android.app.NotificationManager mNotificationManager = (android.app.NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                        mNotificationManager.notify(null, new Random().nextInt(), builder.build());
                    }

                }
            }
        }, new IntentFilter(WonderPush.INTENT_NOTIFICATION_WILL_OPEN));

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
                Toast.makeText(context, "Method " + method + " called with arg: " + arg, Toast.LENGTH_LONG).show();
            }
        }, catchallMethodIntentFilter);

        WonderPush.initialize(this);
        WonderPush.putInstallationCustomProperties(null);

        WonderPushUserPreferences.setDefaultChannelId("default");
        if (WonderPushUserPreferences.getChannelPreference("default") == null) {
            // The wrapping if serves to not modify existing preferences (as we don't store them elsewhere)
            WonderPushUserPreferences.putChannelPreference(
                    new WonderPushChannelPreference("default", null)
                            .setName("Default")
                            .setDescription("Miscellaneous notifications.")
            );
        }
        // Here we declare a new channel
        // On Android O this would create it once and leave it unchanged (except for name and description)
        // On Android pre O, this would reset the user preferences stored in WonderPush
        WonderPushUserPreferences.putChannelPreference(
                new WonderPushChannelPreference("important", null)
                        .setName("Important")
                        .setDescription("Important notifications you should not overlook.")
                        .setImportance(NotificationManagerCompat.IMPORTANCE_MAX)
                        .setColor(Color.RED)
                        .setLights(true)
                        .setLightColor(Color.RED)
                        .setSound(true)
                        .setSoundUri(new Uri.Builder()
                                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                                .authority(getPackageName())
                                .path(String.valueOf(R.raw.sound))
                                .build())
                        .setVibrate(true)
                        .setVibrateInSilentMode(true)
                        .setVibrationPattern(new long[]{200, 50, 200, 50, 200})
        );
    }

}
