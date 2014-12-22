package com.relinkdevice.rrc.activities;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Xml;
import android.widget.EditText;

import com.relinkdevice.rrc.R;
import com.relinkdevice.rrc.RelinkApp;
import com.relinkdevice.rrc.helpers.RemoteControl;
import com.relinkdevice.rrc.helpers.RemoteIntent;
import com.relinkdevice.rrc.helpers.RemoteIntentData;
import com.relinkdevice.rrc.util.Constants;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.StringWriter;

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "GCM: MainActivity";

    private EditText mRegId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();

        RemoteControl.init(this);

        mRegId.setText(RelinkApp.getRegID());

        RemoteIntent i = new RemoteIntent(this, Constants.ACTION_REMOTE_INTENT);
        i.sendRemote();

    }

    /**
     * Initialize view components.
     */
    private void initViews() {
        mRegId = (EditText) findViewById(R.id.etRegId);
    }

}
