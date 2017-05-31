package com.wonderpush.demo;

import android.graphics.Color;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wonderpush.sdk.WonderPushChannelPreference;
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

        WonderPushChannelPreference defaultChannelPreference = WonderPushUserPreferences.getChannelPreference("default");
        triDefaultSound.setCheckedBoolean(defaultChannelPreference.getSound());
        triDefaultVibrate.setCheckedBoolean(defaultChannelPreference.getVibrate());
        triDefaultVibrateSilentMode.setCheckedBoolean(defaultChannelPreference.getVibrateInSilentMode());
        if (defaultChannelPreference.getColor() == null) {
            radDefaultColorAsIs.setChecked(true);
        } else if (defaultChannelPreference.getColor() == Color.TRANSPARENT) {
            radDefaultColorDefault.setChecked(true);
        } else if (defaultChannelPreference.getColor() == Color.RED) {
            radDefaultColorRed.setChecked(true);
        } else if (defaultChannelPreference.getColor() == Color.BLUE) {
            radDefaultColorBlue.setChecked(true);
        } else {
            radDefaultColorNone.setChecked(true);
        }

        triDefaultSound.setOnCheckedTriStateChangeListener(new TriStateCheckBox.OnCheckedTriStateChangeListener() {
            @Override
            public void onCheckedTriStateChanged(TriStateCheckBox triStateCheckBox, TriStateCheckBox.State state) {
                WonderPushChannelPreference defaultChannelPreference = WonderPushUserPreferences.getChannelPreference("default");
                defaultChannelPreference.setSound(state.getBoolean());
                WonderPushUserPreferences.putChannelPreference(defaultChannelPreference);
            }
        });
        triDefaultVibrate.setOnCheckedTriStateChangeListener(new TriStateCheckBox.OnCheckedTriStateChangeListener() {
            @Override
            public void onCheckedTriStateChanged(TriStateCheckBox triStateCheckBox, TriStateCheckBox.State state) {
                WonderPushChannelPreference defaultChannelPreference = WonderPushUserPreferences.getChannelPreference("default");
                defaultChannelPreference.setVibrate(state.getBoolean());
                WonderPushUserPreferences.putChannelPreference(defaultChannelPreference);
            }
        });
        triDefaultVibrateSilentMode.setOnCheckedTriStateChangeListener(new TriStateCheckBox.OnCheckedTriStateChangeListener() {
            @Override
            public void onCheckedTriStateChanged(TriStateCheckBox triStateCheckBox, TriStateCheckBox.State state) {
                WonderPushChannelPreference defaultChannelPreference = WonderPushUserPreferences.getChannelPreference("default");
                defaultChannelPreference.setVibrateInSilentMode(state.getBoolean());
                WonderPushUserPreferences.putChannelPreference(defaultChannelPreference);
            }
        });
        rdgDefaultColor.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                WonderPushChannelPreference defaultChannelPreference = WonderPushUserPreferences.getChannelPreference("default");
                Object tag = findViewById(checkedId).getTag();
                if (tag instanceof String) {
                    String color = (String) tag;
                    if ("null".equals(color)) {
                        defaultChannelPreference.setColor(null);
                    } else if ("default".equals(color)) {
                        defaultChannelPreference.setColor(Color.TRANSPARENT);
                    } else {
                        defaultChannelPreference.setColor(Color.parseColor(color));
                    }
                }
                WonderPushUserPreferences.putChannelPreference(defaultChannelPreference);
            }
        });
    }
}
