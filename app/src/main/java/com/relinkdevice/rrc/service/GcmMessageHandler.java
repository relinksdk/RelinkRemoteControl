package com.relinkdevice.rrc.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.relinkdevice.rrc.helpers.ChangeVolume;
import com.relinkdevice.rrc.helpers.RemoteIntent;
import com.relinkdevice.rrc.util.Constants;

/**
 * Created by nakov on 12/14/14.
 */
public class GcmMessageHandler extends IntentService {

    private String mMsg;
    private Handler mHandler;

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty()) {
            Intent i = RemoteIntent.getFromJSON(extras.getString("intent"));
            if(i == null) {
                Log.i(Constants.TAG, "No intent data passed.");
            } else {
                if(Constants.ACTION_CHANGE_VOLUME.equals(i.getAction())) {
                    ChangeVolume cv = new ChangeVolume(getApplicationContext(), i.getExtras());
                    cv.changeVolume();
                }

                if(Intent.ACTION_VIEW.equals(i.getAction())) {
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(i);
                }
            }

        }

        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

}
