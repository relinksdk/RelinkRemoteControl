package com.relinkdevice.rrc.activities;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.*;
import com.relinkdevice.remotecontrol.RemoteControl;
import com.relinkdevice.remotecontrol.RemoteIntent;
import com.relinkdevice.rrc.R;
import com.relinkdevice.rrc.bluetooth.BluetoothService;
import com.relinkdevice.rrc.helpers.ChangeVolume;
import com.relinkdevice.rrc.util.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class MainActivity extends ActionBarActivity {

    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;
    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";
    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    private Spinner mVolumeTypes, mVolumeLevels;

    private String[] mTypes, mLevels;

    private EditText mEmail;

    private RemoteControl mRemoteCtrl;

    private RelativeLayout mRegistrationContainer, mVolumeContainer, mBTContainer;

    private ToggleButton mOnOffBT;

    private BluetoothAdapter mBluetoothAdapter;

    private BluetoothService mChatService = null;

    private ArrayAdapter<String> mPairedDevicesArrayAdapter;

    private ListView mPairedDevices;

    private List<String> mDeviceAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRemoteCtrl = RemoteControl.init(this);
        initViews();
        initBluetooth();
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
        mBTContainer = (RelativeLayout) findViewById(R.id.btContainer);
        mPairedDevices = (ListView) findViewById(R.id.pairedDevicesList);

        if (mRemoteCtrl.isRegistered()) {
            mRegistrationContainer.setVisibility(View.GONE);
            mVolumeContainer.setVisibility(View.VISIBLE);
            mBTContainer.setVisibility(View.VISIBLE);
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

        if (Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            mRemoteCtrl.register(email);
            Toast.makeText(this, "Registration sent.", Toast.LENGTH_SHORT).show();
        } else {
            mEmail.setError(getString(R.string.error_email_address));
        }

        mRegistrationContainer.setVisibility(View.GONE);
        mVolumeContainer.setVisibility(View.VISIBLE);
        mBTContainer.setVisibility(View.VISIBLE);
    }

    // BLUETOOTH

    /**
     * Initialize bluetooth adapter.
     */
    private void initBluetooth() {
        mOnOffBT = (ToggleButton) findViewById(R.id.onOff);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        mChatService = new BluetoothService(this, mHandler);

        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            mOnOffBT.setChecked(true);
            setPairedDevices();
        }

        onBluetoothChangeListener();
    }

    /**
     * On/Off change listener for the bluetooth.
     */
    private void onBluetoothChangeListener() {
        mOnOffBT.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getApplicationContext(), "Bluetooth not supported on this device.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!isChecked) {
                    mBluetoothAdapter.disable();
                    mOnOffBT.setChecked(false);
                    mPairedDevices.setVisibility(View.GONE);
                } else {
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    mPairedDevices.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    mOnOffBT.setChecked(true);
                    setPairedDevices();
                } else {
                    // User did not enable Bluetooth or an error occurred
                    mOnOffBT.setChecked(false);
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                }
        }
    }

    // The Handler that gets information back from the BluetoothService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_WRITE:
                    Toast.makeText(getApplicationContext(), "Sent volume status to remote device.", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_READ:
                    byte[] readBuf = (byte[]) msg.obj;
                    // construct a string from the valid bytes in the buffer
                    String readMessage = new String(readBuf, 0, msg.arg1);

                    Intent i = RemoteIntent.getFromJSON(readMessage);
                    if(i == null) {
                        Log.i(Constants.TAG, "No intent data passed.");
                    } else {
                        if(Constants.ACTION_CHANGE_VOLUME.equals(i.getAction())) {
                            ChangeVolume cv = new ChangeVolume(getApplicationContext(), i.getExtras());
                            cv.changeVolume();

                            Toast.makeText(getApplicationContext(), "Volume changed.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    String connectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + connectedDeviceName, Toast.LENGTH_SHORT).show();
                    Toast.makeText(getApplicationContext(), "Press the device one more to send the intent.", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    /**
     * Send intent via BT.
     *
     * @param address - Selected bluetooth device address
     */
    public void sendViaBT(String address) {
        RemoteIntent i = new RemoteIntent(this);
        i.setAction(Constants.ACTION_CHANGE_VOLUME);
        i.putExtra("type", mVolumeTypes.getSelectedItemPosition());
        i.putExtra("level", mVolumeLevels.getSelectedItemPosition());
        i.setPackage("com.android.bluetooth");
        i.sendRemote();

        BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
        // Attempt to connect to the device
        if(mChatService.getState() != BluetoothService.STATE_CONNECTED) {
            mChatService.connect(device);
        } else {
            byte[] send = i.getJSON().getBytes();
            mChatService.write(send);
        }
    }

    /**
     * Set the list of paired devices.
     */
    private void setPairedDevices() {
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
        mPairedDevicesArrayAdapter = new ArrayAdapter<>(this, R.layout.device_name);
        mDeviceAddress = new ArrayList<>();

        // If there are paired devices, add each one to the ArrayAdapter
        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                mPairedDevicesArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mDeviceAddress.add(device.getAddress());
            }
        } else {
            String noDevices = getResources().getText(R.string.none_paired).toString();
            mPairedDevicesArrayAdapter.add(noDevices);
        }
        mPairedDevices.setAdapter(mPairedDevicesArrayAdapter);

        mPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i(Constants.TAG, "Clicked " + mDeviceAddress.get(position));

                sendViaBT(mDeviceAddress.get(position));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if(mBluetoothAdapter != null && mBluetoothAdapter.isEnabled()) {
            setPairedDevices();
        }
    }
}
