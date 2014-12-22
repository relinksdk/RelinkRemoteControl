package com.relinkdevice.rrc;

import android.app.Application;

import com.relinkdevice.rrc.helpers.RemoteControl;

/**
 * Created by nakov on 12/15/14.
 */
public class RelinkApp extends Application {

    private static String mRegID;

    @Override
    public void onCreate() {
        super.onCreate();

    }

    /**
     * Get GCM registration ID.
     *
     * @return
     */
    public static String getRegID() {
        return mRegID;
    }

    /**
     * Set GCM registration ID.
     *
     * @param regId
     */
    public static void setRegID(String regId) {
        mRegID = regId;
    }
}
