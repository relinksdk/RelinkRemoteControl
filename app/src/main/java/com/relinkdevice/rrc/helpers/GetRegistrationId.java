package com.relinkdevice.rrc.helpers;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.relinkdevice.rrc.util.Constants;
import com.relinkdevice.rrc.util.PreferenceUtil;

import java.io.IOException;

/**
 * Created by nakov on 12/15/14.
 */
public class GetRegistrationId {

    private static final String TAG = "RRC: GetRegistrationId";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private PreferenceUtil mPreferenceUtil;

    private String mRegistrationId;

    private GoogleCloudMessaging mGCM;

    public GetRegistrationId(Context ctx) {
        mPreferenceUtil = PreferenceUtil.getInstance(ctx);

        mRegistrationId = mPreferenceUtil.getString(Constants.KEY_DEVICE_REGISTRATION_ID, null);

        if (mRegistrationId == null) {
            retrieveRegistrationId(ctx);
        }
    }

    /**
     * Get registration ID from Google developer console.
     *
     * @param ctx
     */
    private void retrieveRegistrationId(Context ctx) {
//        if(checkPlayServices((Activity) ctx)) {
            new RetrieveRegistrationIdTask(ctx).execute();
//        }
    }

    /**
     * Get registration ID.
     *
     * @return
     */
    public String getRegistrationId() {
        return mRegistrationId;
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

                saveRegistrationId(mRegistrationId);
                Log.i(TAG, "RegID: " + mRegistrationId);
            } catch (IOException ex) {
                Log.i(TAG, ex.getMessage());
            }
            return null;
        }

        /**
         * Save registration id to preference
         * */
        private void saveRegistrationId(String registrationId) {
            mPreferenceUtil.setString(Constants.KEY_DEVICE_REGISTRATION_ID, registrationId);
        }

    }

}
