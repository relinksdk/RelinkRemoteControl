package com.relinkdevice.rrc.helpers;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.util.Xml;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.relinkdevice.rrc.util.Constants;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nakov on 12/16/14.
 */
public class RemoteIntent extends Intent {

    private static final String KEY_INTENT = "intent";
    private static final String KEY_ACTION = "action";
    private static final String KEY_DATA = "data";
    private static final String KEY_TYPE = "type";
    private static final String KEY_COMPONENT = "component";
    private static final String KEY_EXTRAS = "extras";
    private static final String KEY_FLAGS = "flags";
    private static final String KEY_CATEGORY = "category";
    private static final String KEY_CATEGORIES = "categories";

    private GoogleCloudMessaging mGoogleCloudMessaging;

    private AtomicInteger mId;

    public RemoteIntent(Context ctx) {
        super();
        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(ctx);
        mId = new AtomicInteger();
    }

    /**
     * Converts the original intent data into serialized xml string and sends a GCM message.
     */
    public void sendRemote() {
        if (mGoogleCloudMessaging == null) {
            Log.i(Constants.TAG, "Google cloud messaging is not initialized. Please use the RemoteIntent constructor.");
            return;
        }

        try {
            JSONObject obj = saveToJSON();

            Bundle data = new Bundle();
            data.putString(KEY_INTENT, obj.toString());
            send(data);

        } catch (JSONException e) {
            Log.d(Constants.TAG, e.getLocalizedMessage());
        }

    }

    /**
     * Upstream a GCM message up to the 3rd party server
     */
    private void send(Bundle data) {
        new AsyncTask<Bundle, Void, Void>() {
            @Override
            protected Void doInBackground(Bundle... params) {
                try {
                    Bundle data = params[0];
                    data.putString(KEY_ACTION, Constants.ACTION_REMOTE_INTENT);
                    String id = Integer.toString(mId.incrementAndGet());
                    mGoogleCloudMessaging.send(Constants.GCM_SENDER_HOST, id, Constants.GCM_TIME_TO_LIVE, data);
                } catch (IOException ex) {
                    Log.d(Constants.TAG, ex.getLocalizedMessage());
                }

                return null;
            }

        }.execute(data);
    }

    /**
     * Save intent object to JSON.
     *
     * @return
     * @throws JSONException
     */
    private JSONObject saveToJSON() throws JSONException {
        JSONObject obj = new JSONObject();
        if (getAction() != null) {
            obj.put(KEY_ACTION, getAction());
        }
        if (getData() != null) {
            obj.put(KEY_DATA, getData().toString());
        }
        if (getType() != null) {
            obj.put(KEY_TYPE, getType());
        }
        if (getComponent() != null) {
            obj.put(KEY_COMPONENT, getComponent().flattenToShortString());
        }

        Bundle data = getExtras();
        if (data != null) {
            JSONArray extras = new JSONArray();
            for (String key : data.keySet()) {
                extras.put(new JSONObject().put(key, data.get(key)));
            }
            obj.put(KEY_EXTRAS, extras);
        }
        obj.put(KEY_FLAGS, Integer.toHexString(getFlags()));

        if (getCategories() != null) {
            JSONArray categories = new JSONArray();
            for (int categoryNdx = getCategories().size() - 1; categoryNdx >= 0; --categoryNdx) {
                categories.put(new JSONObject().put(KEY_CATEGORY, getCategories().toArray()[categoryNdx].toString()));
            }
            obj.put(KEY_CATEGORIES, categories);
        }

        return obj;
    }

    /**
     * Create new Intent object from JSON.
     *
     * @param data
     * @return
     */
    public static Intent getFromJSON(String data) {

        if(data == null)
            return null;

        Intent intent = new Intent();

        try {
            JSONObject obj = new JSONObject(data);

            if(obj.has(KEY_ACTION)) {
                intent.setAction(obj.getString(KEY_ACTION));
            }

            if(obj.has(KEY_DATA)) {
                Uri uri = Uri.parse(obj.getString(KEY_DATA));
                intent.setData(uri);
            }

            if(obj.has(KEY_TYPE)) {
                intent.setType(obj.getString(KEY_TYPE));
            }

            if(obj.has(KEY_COMPONENT)) {
                intent.setComponent(ComponentName.unflattenFromString(obj.getString(KEY_COMPONENT)));
            }

            if(obj.has(KEY_EXTRAS)) {
                JSONArray extras = obj.getJSONArray(KEY_EXTRAS);
                for(int i = 0; i < extras.length(); i++) {
                    JSONObject o = extras.getJSONObject(i);
                    Iterator<String> it = o.keys();
                    while(it.hasNext()) {
                        String key = it.next();
                        intent.putExtra(key, o.get(key).toString());
                    }
                }
            }

            if(obj.has(KEY_FLAGS)) {
                intent.setFlags(Integer.valueOf(obj.getString(KEY_FLAGS), 16));
            }

            if(obj.has(KEY_CATEGORIES)) {
                JSONArray categories = obj.getJSONArray(KEY_CATEGORIES);
                for(int i = 0; i < categories.length(); i++) {
                    JSONObject o = categories.getJSONObject(i);
                    if(o.has(KEY_CATEGORY))
                        intent.addCategory(o.getString(KEY_CATEGORY));
                }
            }

        } catch (JSONException e) {
            Log.d(Constants.TAG, e.getLocalizedMessage());
        }

        return intent;
    }

}
