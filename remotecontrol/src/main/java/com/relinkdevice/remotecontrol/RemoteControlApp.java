package com.relinkdevice.remotecontrol;

import android.app.Application;

/**
 * @author Trajche Nakov on 1/9/15.
 * @email trajce.nakov@gmail.com
 */
public class RemoteControlApp extends Application {

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
