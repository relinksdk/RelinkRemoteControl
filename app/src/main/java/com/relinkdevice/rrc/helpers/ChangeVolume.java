package com.relinkdevice.rrc.helpers;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import com.relinkdevice.rrc.util.Constants;

/**
 * Used to change the volume using remote control.
 *
 * Created by nakov on 12/23/14.
 */
public class ChangeVolume {

    private int[] mTypes = {AudioManager.STREAM_SYSTEM, AudioManager.STREAM_MUSIC, AudioManager.STREAM_ALARM, AudioManager.STREAM_RING};

    private Bundle mData;
    private AudioManager mAudioManager;

    public ChangeVolume(Context ctx, Bundle data) {
        mData = data;
        mAudioManager = (AudioManager) ctx.getSystemService(Context.AUDIO_SERVICE);
    }

    /**
     * Change the volume by given type and level passed in the bundle.z
     */
    public void changeVolume() {
        if(mData == null) {
            Log.i(Constants.TAG, "No data passed in the intent. The volume will remain the same");
        }

        int type = Integer.parseInt(mData.getString("type"));
        int level = Integer.parseInt(mData.getString("level"));

        int curLevel = mAudioManager.getStreamVolume(mTypes[type]);

        // set to normal by default
        mAudioManager.setRingerMode(AudioManager.RINGER_MODE_NORMAL);

        if(level == 0) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        } else if(level == 1) {
            mAudioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
        } else if(level == 9) { // max
            mAudioManager.setStreamVolume(mTypes[type], mAudioManager.getStreamMaxVolume(mTypes[type]), 0);
        } else {
            if(level > curLevel) {
                level = level - curLevel;
                for(int i = 0; i < level; i++) {
                    mAudioManager.adjustStreamVolume(mTypes[type], AudioManager.ADJUST_RAISE, 0);
                }
            } else if(level < curLevel) {
                level = curLevel - level;
                for(int i = 0; i < level; i++) {
                    mAudioManager.adjustStreamVolume(mTypes[type], AudioManager.ADJUST_LOWER, 0);
                }
            }
        }

    }

}
