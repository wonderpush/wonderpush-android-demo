package com.wonderpush.demo;

import com.wonderpush.sdk.WonderPush;

import android.app.Activity;
import android.os.Bundle;

public class ChildActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_child);

        WonderPush.initialize(this);
    }

}
