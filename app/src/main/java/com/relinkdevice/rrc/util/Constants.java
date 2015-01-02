package com.relinkdevice.rrc.util;

/**
 * Created by Sharif on 12/15/2014.
 */
public class Constants {

    public static final String TAG = "RelinkRemoteControl";

    public static final String KEY_DEVICE_REGISTRATION_ID = "key.device.registration.id";

    public static final String KEY_REGISTERED = "key.registered.server";

    public static final String PROJECT_NUMBER = "810659540725";

    public static final String GCM_SENDER_HOST = PROJECT_NUMBER + "@gcm.googleapis.com";

    public static final long GCM_TIME_TO_LIVE = 60L * 60L * 24L * 7L * 4L; // 4 Weeks

    public static final String ACTION_REGISTER = "com.relinkdevice.gcm.REGISTER";

    public static final String ACTION_UNREGISTER = "com.relinkdevice.gcm.UNREGISTER";

    public static final String ACTION_REMOTE_INTENT = "com.relinkdevice.gcm.REMOTE_INTENT";

    public static final String ACTION_CHANGE_VOLUME = "com.relinkdevice.gcm.CHANGE_VOLUME";

}
