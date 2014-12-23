package com.relinkdevice.rrc.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.relinkdevice.rrc.R;
import com.relinkdevice.rrc.helpers.RemoteControl;
import com.relinkdevice.rrc.helpers.RemoteIntent;
import com.relinkdevice.rrc.util.Constants;

public class MainActivity extends ActionBarActivity {

    private Spinner mVolumeTypes, mVolumeLevels;

    private String[] mTypes, mLevels;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        RemoteControl.init(this);
    }

    /**
     * Initialize view components.
     */
    private void initViews() {
        mVolumeTypes = (Spinner) findViewById(R.id.volumeTypes);
        mVolumeLevels = (Spinner) findViewById(R.id.volumeLevels);

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

}
