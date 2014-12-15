package com.relinkdevice.rrc;

import android.app.Application;

import com.relinkdevice.rrc.helpers.GetRegistrationId;

/**
 * Created by nakov on 12/15/14.
 */
public class RelinkApp extends Application {

    private String mRegID;

    @Override
    public void onCreate() {
        super.onCreate();

        GetRegistrationId regId = new GetRegistrationId(this);
        mRegID = regId.getRegistrationId();
    }

    /**
     * Get GCM registration ID.
     *
     * @return
     */
    public String getRegID() {
        return mRegID;
    }
}
