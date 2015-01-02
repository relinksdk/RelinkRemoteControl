package com.relinkdevice.rrc.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import com.relinkdevice.rrc.R;
import com.relinkdevice.rrc.helpers.RemoteControl;
import com.relinkdevice.rrc.helpers.RemoteIntent;
import com.relinkdevice.rrc.util.Constants;

public class MainActivity extends ActionBarActivity {

    private Spinner mVolumeTypes, mVolumeLevels;

    private String[] mTypes, mLevels;

    private EditText mEmail;

    private RemoteControl mRemoteCtrl;

    private RelativeLayout mRegistrationContainer, mVolumeContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRemoteCtrl = RemoteControl.init(this);
        initViews();
    }

    /**
     * Initialize view components.
     */
    private void initViews() {
        mVolumeTypes = (Spinner) findViewById(R.id.volumeTypes);
        mVolumeLevels = (Spinner) findViewById(R.id.volumeLevels);
        mEmail = (EditText) findViewById(R.id.email);
        mRegistrationContainer = (RelativeLayout) findViewById(R.id.registrationContainer);
        mVolumeContainer = (RelativeLayout) findViewById(R.id.volumeContainer);

        if(mRemoteCtrl.isRegistered()) {
            mRegistrationContainer.setVisibility(View.GONE);
            mVolumeContainer.setVisibility(View.VISIBLE);
        }

        setComponentsData();
    }

    /**
     * Fills the components with data.
     */
    private void setComponentsData() {
        mTypes = getResources().getStringArray(R.array.volume_types);
        ArrayAdapter<String> volumeTypes = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mTypes);
        mVolumeTypes.setAdapter(volumeTypes);

        mLevels = getResources().getStringArray(R.array.volume_levels);
        ArrayAdapter<String> volumeLevels = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_dropdown_item, mLevels);
        mVolumeLevels.setAdapter(volumeLevels);
    }

    /**
     * On change volume button click.
     *
     * @param v
     */
    public void changeVolume(View v) {
        RemoteIntent i = new RemoteIntent(this);
        i.setAction(Constants.ACTION_CHANGE_VOLUME);
        i.putExtra("type", mVolumeTypes.getSelectedItemPosition());
        i.putExtra("level", mVolumeLevels.getSelectedItemPosition());
        i.sendRemote();

        Toast.makeText(this, "Remote control intent was sent.", Toast.LENGTH_SHORT).show();
    }

    public void openInBrowser(View v) {
        String url = "http://www.google.com";
        RemoteIntent i = new RemoteIntent(this);
        i.setAction(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        i.sendRemote();
    }

    /**
     * On register button click.
     */
    public void register(View v) {
        String email = mEmail.getText().toString();

        if(Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mRemoteCtrl.register(email);
            Toast.makeText(this, "Registration sent.", Toast.LENGTH_SHORT).show();
        } else {
            mEmail.setError(getString(R.string.error_email_address));
        }

        mRegistrationContainer.setVisibility(View.GONE);
        mVolumeContainer.setVisibility(View.VISIBLE);
    }

}
