package com.wonderpush.demo;

import android.app.Activity;
import android.graphics.Color;
import androidx.annotation.IdRes;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.wonderpush.sdk.WonderPush;
import com.wonderpush.sdk.WonderPushChannel;
import com.wonderpush.sdk.WonderPushUserPreferences;

public class InitializeActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initialize);

        EditText clientId = (EditText) findViewById(R.id.clientIdEditText);
        EditText secret = (EditText) findViewById(R.id.secretEditText);
        Button initializeButton = (Button) findViewById(R.id.btnInitialize);
        initializeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String clientIdValue = clientId.getText().toString();
                String secretValue = secret.getText().toString();
                if (clientIdValue.isEmpty() || secretValue.isEmpty()) {
                    return;
                }
                DemoApplication.getInstance().initializeWonderPush(clientIdValue, secretValue);
                WonderPush.subscribeToNotifications();
                finish();
            }
        });
    }
}
