package com.wonderpush.demo;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class TreeA2Activity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_a2);
    }

    public void btnOpenB1_onClick(View button) {
        startActivity(new Intent().setComponent(new ComponentName(this, TreeA2B1Activity.class)));
    }

    public void btnOpenB2_onClick(View button) {
        startActivity(new Intent().setComponent(new ComponentName(this, TreeA2B2Activity.class)));
    }

}
