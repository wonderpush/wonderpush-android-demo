package com.wonderpush.demo;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class TreeA1Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tree_a1);
    }

    public void btnOpenB1_onClick(View button) {
        startActivity(new Intent().setClass(this, TreeA1B1Activity.class));
    }

    public void btnOpenB2_onClick(View button) {
        startActivity(new Intent().setClass(this, TreeA1B2Activity.class));
    }

}
