package com.example.boss.recordservice.service;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;


/**
 * Created by BOSS on 30.11.2016.
 */

public class ServiceDialog extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("To record calls you need to allow usage stat permission");
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                ServiceDialog.this.finish();
            }
        });
        alert.setPositiveButton(android.R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(ServiceDialog.this, "OK", Toast.LENGTH_SHORT).show();
                        startActivityForResult(intent, 10);
                        ServiceDialog.this.finish();
                    }
                });
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v("myLogs","Receive Something");
    }
}
