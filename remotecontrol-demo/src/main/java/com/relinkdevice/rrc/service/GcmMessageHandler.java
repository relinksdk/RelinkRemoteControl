package com.relinkdevice.rrc.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.relinkdevice.remotecontrol.RemoteIntent;
import com.relinkdevice.rrc.helpers.ChangeVolume;
import com.relinkdevice.rrc.util.Constants;

/**
 * Created by nakov on 12/14/14.
 */
public class GcmMessageHandler extends IntentService {

    public GcmMessageHandler() {
        super("GcmMessageHandler");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();

        if (!extras.isEmpty()) {
            Intent i = RemoteIntent.getFromJSON(extras.getString("intent"));
            if(i == null) {
                // used from the browser
                ChangeVolume cv = new ChangeVolume(getApplicationContext(), intent.getExtras());
                cv.changeVolume();
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
