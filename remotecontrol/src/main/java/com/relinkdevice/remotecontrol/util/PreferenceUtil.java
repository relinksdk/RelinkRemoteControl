package com.relinkdevice.remotecontrol.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by nakov on 09/01/2015.
 */
public class PreferenceUtil {

    private static PreferenceUtil mPreferenceInstance;

    private static SharedPreferences mSharedPreferences;

    private SharedPreferences.Editor mEditor;

    private static Context mContext;

    /**
     * Private constructor is used to initiate SingleTon instance
     * @param  context
     * */
    private PreferenceUtil(Context context) {
        this.mContext = context;
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this.mContext.getApplicationContext());
    }

    /**
     * Singleton instance
     * @param  context
     * */
    public static synchronized PreferenceUtil getInstance(Context context) {
        if (mPreferenceInstance == null) {
            mPreferenceInstance = new PreferenceUtil(context);
        }
        return mPreferenceInstance;
    }

    /**
     * Get preference value using key
     * @param  key
     * @param defaultValue
     * */
    public String getString(String key, String defaultValue) {
        return mSharedPreferences.getString(key, defaultValue);
    }

    /**
     * Set preference string
     * @param  key
     * @param value
     * */
    public void setString(String key, String value) {
        getmEditor().putString(key, value).commit();
    }

    /**
     * Get boolean preference if the value is available otherwise it will return false
     * @param  key
     * */
    public boolean getBoolean(String key) {
        return mSharedPreferences.getBoolean(key, false);
    }

    /**
     * Get boolean preference
     * @param  key
     * @param defaultValue
     * */
    public boolean getBoolean(String key, boolean defaultValue) {
        return mSharedPreferences.getBoolean(key, defaultValue);
    }

    /**
     * Set boolean preference
     * @param  key
     * @param value
     * */
    public void setBoolean(String key, boolean value) {
        getmEditor().putBoolean(key, value).commit();
    }

    /**
     * Set int preference
     * @param  key
     * @param value
     * */
    public void setInt(String key, int value) {
        getmEditor().putInt(key, value).commit();
    }

    /**
     * Get int preference
     * @param  key
     * @param defaultValue
     * */
    public int getInt(String key, int defaultValue) {
        return mSharedPreferences.getInt(key, defaultValue);
    }

    /**
     * Get int preference where default value is zero
     * @param  key
     * */
    public int getInt(String key) {
        return mSharedPreferences.getInt(key, 0);
    }

    /**
     * Editor to write into SharedPreference file
     * */
    private SharedPreferences.Editor getmEditor(){
        return mSharedPreferences.edit();
    }
}
