package com.relinkdevice.rrc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.EditText;

import com.relinkdevice.rrc.R;
import com.relinkdevice.rrc.RelinkApp;
import com.relinkdevice.rrc.util.Constants;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "GCM: MainActivity";

    private EditText mRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        RelinkApp mApp = (RelinkApp) getApplication();
        mRegId.setText(mApp.getRegID());

        Intent intent = new Intent(Constants.REMOTE_INTENT);
        sendBroadcast(intent);
    }

    /**
     * Initialize view components.
     */
    private void initViews() {
        mRegId = (EditText) findViewById(R.id.etRegId);
    }

}
