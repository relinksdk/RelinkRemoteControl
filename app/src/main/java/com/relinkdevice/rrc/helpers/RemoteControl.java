package com.relinkdevice.rrc.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.relinkdevice.rrc.RelinkApp;
import com.relinkdevice.rrc.util.Constants;
import com.relinkdevice.rrc.util.PreferenceUtil;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nakov on 12/15/14.
 */
public class RemoteControl {

    private static final String TAG = "RRC: GetRegistrationId";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private PreferenceUtil mPreferenceUtil;

    private String mRegistrationId;

    private GoogleCloudMessaging mGCM;

    private static RemoteControl mRemoteControl;

    private AtomicInteger mId = new AtomicInteger();

    public static void init(Activity activity) {
        if (mRemoteControl == null)
            mRemoteControl = new RemoteControl(activity);
    }

    public RemoteControl(Activity activity) {
        mPreferenceUtil = PreferenceUtil.getInstance(activity);

        mRegistrationId = mPreferenceUtil.getString(Constants.KEY_DEVICE_REGISTRATION_ID, null);

        if (mRegistrationId == null) {
            retrieveRegistrationId(activity);
        } else {
            RelinkApp.setRegID(mRegistrationId);
        }

    }

    /**
     * Get registration ID from Google developer console.
     *
     * @param activity
     */
    private void retrieveRegistrationId(Activity activity) {
        if (checkPlayServices(activity)) {
            new RetrieveRegistrationIdTask(activity).execute();
        }
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(activity);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
            }
            return false;
        }
        return true;
    }

    /**
     * Getting registration ID
     */
    private class RetrieveRegistrationIdTask extends AsyncTask<Void, Void, Void> {

        private Context mContext;

        public RetrieveRegistrationIdTask(Context ctx) {
            mContext = ctx;
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                if (mGCM == null) {
                    mGCM = GoogleCloudMessaging.getInstance(mContext);
                }
                mRegistrationId = mGCM.register(Constants.PROJECT_NUMBER);

                sendRegistrationIdToBackend();

                saveRegistrationId(mRegistrationId);
                Log.i(TAG, "RegID: " + mRegistrationId);
            } catch (IOException ex) {
                Log.i(TAG, ex.getMessage());
            }
            return null;
        }

        /**
         * Save registration id to preference
         */
        private void saveRegistrationId(String registrationId) {
            mPreferenceUtil.setString(Constants.KEY_DEVICE_REGISTRATION_ID, registrationId);
            RelinkApp.setRegID(registrationId);
        }

    }

    /**
     * Sends the registration ID to the 3rd party server via an upstream
     * GCM message. Ideally this would be done via HTTP to guarantee success or failure
     * immediately, but it would require an HTTP endpoint.
     */
    private void sendRegistrationIdToBackend() {
        Log.d(Constants.TAG, "REGISTER USER ID: " + mRegistrationId);
        String name = "trajche.nakov@gmail.com";
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... params) {
                try {
                    Bundle data = new Bundle();
                    data.putString("name", params[0]);
                    data.putString("action", Constants.ACTION_REGISTER);
                    String id = Integer.toString(mId.incrementAndGet());
                    mGCM.send(Constants.GCM_SENDER_HOST, id, Constants.GCM_TIME_TO_LIVE, data);

                    Log.i(Constants.TAG, "Registration ID sent to backend");
                } catch (IOException ex) {
                    Log.d(Constants.TAG, ex.getLocalizedMessage());
                }
                return null;
            }

        }.execute(name);
    }

    /**
     * Send an upstream GCM message to the 3rd party server to remove this
     * device's registration ID, and contact the GCM server to do the same.
     */
    private void unregister() {
        Log.d(Constants.TAG, "UNREGISTER USER ID: " + mRegistrationId);
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    Bundle data = new Bundle();
                    data.putString("action", Constants.ACTION_UNREGISTER);
                    String id = Integer.toString(mId.incrementAndGet());
                    mGCM.send(Constants.GCM_SENDER_HOST, id, Constants.GCM_TIME_TO_LIVE, data);
                    msg = "Sent un-registration";
                    mGCM.unregister();
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                }
                return msg;
            }

        }.execute();
    }

}
