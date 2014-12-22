package com.relinkdevice.rrc.helpers;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by nakov on 12/15/14.
 */
public class RemoteIntentData implements Parcelable {

    public static final String TAG = "RemoteIntentData";

    private String mAction;

    private Bundle mBundle;

    public RemoteIntentData() {
    }

    public String getAction() {
        return mAction;
    }

    public void setAction(String action) {
        mAction = action;
    }

    public Bundle getBundle() {
        return mBundle;
    }

    public void setBundle(Bundle bundle) {
        mBundle = bundle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(getAction());
        out.writeBundle(getBundle());
    }

    /**
     * Parcelable object constructor.
     */
    public static final Parcelable.Creator<RemoteIntentData> CREATOR
            = new Parcelable.Creator<RemoteIntentData>() {
        public RemoteIntentData createFromParcel(Parcel in) {
            return new RemoteIntentData(in);
        }

        public RemoteIntentData[] newArray(int size) {
            return new RemoteIntentData[size];
        }
    };

    /**
     * Read parcelable in the order that was entered.
     *
     * @param in
     */
    public RemoteIntentData(Parcel in) {
        setAction(in.readString());
        setBundle(in.readBundle());
    }

}
