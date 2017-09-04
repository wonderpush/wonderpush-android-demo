package com.wonderpush.demo;

import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wonderpush.sdk.WonderPushChannel;
import com.wonderpush.sdk.WonderPushUserPreferences;

public class PreferencesActivity extends AppCompatActivity {

    TriStateCheckBox triDefaultSound;
    TriStateCheckBox triDefaultVibrate;
    TriStateCheckBox triDefaultVibrateSilentMode;
    RadioGroup rdgDefaultColor;
    RadioButton radDefaultColorAsIs;
    RadioButton radDefaultColorNone;
    RadioButton radDefaultColorDefault;
    RadioButton radDefaultColorRed;
    RadioButton radDefaultColorBlue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        triDefaultSound = (TriStateCheckBox) findViewById(R.id.triDefaultSound);
        triDefaultVibrate = (TriStateCheckBox) findViewById(R.id.triDefaultVibrate);
        triDefaultVibrateSilentMode = (TriStateCheckBox) findViewById(R.id.triDefaultVibrateSilentMode);
        rdgDefaultColor = (RadioGroup) findViewById(R.id.rdgDefaultColor);
        radDefaultColorAsIs = (RadioButton) findViewById(R.id.radDefaultColorAsIs);
        radDefaultColorNone = (RadioButton) findViewById(R.id.radDefaultColorNone);
        radDefaultColorDefault = (RadioButton) findViewById(R.id.radDefaultColorDefault);
        radDefaultColorRed = (RadioButton) findViewById(R.id.radDefaultColorRed);
        radDefaultColorBlue = (RadioButton) findViewById(R.id.radDefaultColorBlue);

        // The channel existence is ensured in DemoApplication, no need to test against null
        WonderPushChannel defaultChannel = WonderPushUserPreferences.getChannel("default");
        triDefaultSound.setCheckedBoolean(defaultChannel.getSound());
        triDefaultVibrate.setCheckedBoolean(defaultChannel.getVibrate());
        triDefaultVibrateSilentMode.setCheckedBoolean(defaultChannel.getVibrateInSilentMode());
        if (defaultChannel.getColor() == null) {
            radDefaultColorAsIs.setChecked(true);
        } else if (defaultChannel.getColor() == Color.TRANSPARENT) {
            radDefaultColorDefault.setChecked(true);
        } else if (defaultChannel.getColor() == Color.RED) {
            radDefaultColorRed.setChecked(true);
        } else if (defaultChannel.getColor() == Color.BLUE) {
            radDefaultColorBlue.setChecked(true);
        } else {
            radDefaultColorNone.setChecked(true);
        }

        triDefaultSound.setOnCheckedTriStateChangeListener(new TriStateCheckBox.OnCheckedTriStateChangeListener() {
            @Override
            public void onCheckedTriStateChanged(TriStateCheckBox triStateCheckBox, TriStateCheckBox.State state) {
                WonderPushChannel defaultChannel = WonderPushUserPreferences.getChannel("default");
                defaultChannel.setSound(state.getBoolean());
                WonderPushUserPreferences.putChannel(defaultChannel);
            }
        });
        triDefaultVibrate.setOnCheckedTriStateChangeListener(new TriStateCheckBox.OnCheckedTriStateChangeListener() {
            @Override
            public void onCheckedTriStateChanged(TriStateCheckBox triStateCheckBox, TriStateCheckBox.State state) {
                WonderPushChannel defaultChannel = WonderPushUserPreferences.getChannel("default");
                defaultChannel.setVibrate(state.getBoolean());
                WonderPushUserPreferences.putChannel(defaultChannel);
            }
        });
        triDefaultVibrateSilentMode.setOnCheckedTriStateChangeListener(new TriStateCheckBox.OnCheckedTriStateChangeListener() {
            @Override
            public void onCheckedTriStateChanged(TriStateCheckBox triStateCheckBox, TriStateCheckBox.State state) {
                WonderPushChannel defaultChannel = WonderPushUserPreferences.getChannel("default");
                defaultChannel.setVibrateInSilentMode(state.getBoolean());
                WonderPushUserPreferences.putChannel(defaultChannel);
            }
        });
        rdgDefaultColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                WonderPushChannel defaultChannel = WonderPushUserPreferences.getChannel("default");
                Object tag = findViewById(checkedId).getTag();
                if (tag instanceof String) {
                    String color = (String) tag;
                    if ("null".equals(color)) {
                        defaultChannel.setColor(null);
                    } else if ("default".equals(color)) {
                        defaultChannel.setColor(Color.TRANSPARENT);
                    } else {
                        defaultChannel.setColor(Color.parseColor(color));
                    }
                }
                WonderPushUserPreferences.putChannel(defaultChannel);
            }
        });
    }
}