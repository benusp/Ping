package com.example.peto.ping;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Ping extends AppCompatActivity {

    private static final String TAG = "ping";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ping);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        Button btn = (Button) findViewById(R.id.submit_button);
        Button stopBtn = (Button) findViewById(R.id.stop_button);
        Button clearBtn = (Button) findViewById(R.id.clear_button);

        ArrayList<EditText> ipAdresses = new ArrayList<EditText>();
        ipAdresses.add((EditText) findViewById(R.id.ipAdress1));
        ipAdresses.add((EditText) findViewById(R.id.ipAdress2));
        ipAdresses.add((EditText) findViewById(R.id.ipAdress3));

        EditText okInterval = (EditText) findViewById(R.id.okInterval);
        EditText badInterval = (EditText) findViewById(R.id.badInterval);

        Context context = getApplicationContext();

        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiManager.WifiLock lock = wifiManager.createWifiLock(WifiManager.WIFI_MODE_FULL, TAG);
        lock.setReferenceCounted(false);
        lock.acquire();

        final TextView console = (TextView) findViewById(R.id.console);
        btn.setOnClickListener(new MyOnClickListener(ipAdresses, console, this, okInterval, badInterval));
        stopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                 MyOnClickListener.setRun(false);
            }
        });
        clearBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                console.setText("");
            }
        });
    }
}
