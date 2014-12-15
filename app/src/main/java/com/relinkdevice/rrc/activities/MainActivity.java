package com.relinkdevice.rrc.activities;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.relinkdevice.rrc.R;
import com.relinkdevice.rrc.util.Constants;
import com.relinkdevice.rrc.util.PreferenceUtil;

import java.io.IOException;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "GCM: MainActivity";
    private static final String PROJECT_NUMBER = "810659540725";
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private EditText mRegId;

    private GoogleCloudMessaging mGCM;

    private String mRegistrationID;

    private PreferenceUtil mPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        mPreference = PreferenceUtil.getInstance(this);
    }

    /**
     * Initialize view components.
     */
    private void initViews() {
        mRegId = (EditText) findViewById(R.id.etRegId);
    }

    /**
     * On get registration ID button click handler.
     *
     * @param v
     */
    public void getRegistrationId(View v) {
        new RetrieveRegistrationIdTask().execute();
    }

    /**
     * Getting registration ID
     */
    private class RetrieveRegistrationIdTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... params) {
            String msg = "";
            try {
                if (mGCM == null) {
                    mGCM = GoogleCloudMessaging.getInstance(getApplicationContext());
                }
                mRegistrationID = mGCM.register(PROJECT_NUMBER);

                saveRegistrationId(mRegistrationID);
                msg = "Device registered, registration ID=" + mRegistrationID;
                Log.i("GCM", msg);
            } catch (IOException ex) {
                msg = "Error :" + ex.getMessage();

            }
            return msg;
        }

        /**
         * Save registration id to preference
         * */
        private void saveRegistrationId(String registrationId) {
            mPreference.setString(Constants.KEY_DEVICE_REGISTRATION_ID, registrationId);
        }

        @Override
        protected void onPostExecute(String msg) {
            mRegId.setText(msg + "\n");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Check device for Play Services APK.
        checkPlayServices();
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, this,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }
}
