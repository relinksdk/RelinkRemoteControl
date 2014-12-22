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
import com.relinkdevice.rrc.RelinkApp;
import com.relinkdevice.rrc.util.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by nakov on 12/16/14.
 */
public class RemoteIntent extends Intent {

    private String mRegistrationId;

    private InputStream is;

    private GoogleCloudMessaging mGoogleCloudMessaging;

    private AtomicInteger mId = new AtomicInteger();

    public RemoteIntent(Context ctx, String action) {
        super(action);
        mGoogleCloudMessaging = GoogleCloudMessaging.getInstance(ctx);
        mRegistrationId = RelinkApp.getRegID();
    }

    /**
     * Converts the original intent data into serialized xml string and sends a GCM message.
     */
    public void sendRemote() {
        try {
            JSONObject obj = saveToJSON();

            Bundle data = new Bundle();
            data.putString("intent", obj.toString());
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
                    data.putString("action", Constants.ACTION_REMOTE_INTENT);
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
            obj.put("action", getAction());
        }
        if (getData() != null) {
            obj.put("data", getData().toString());
        }
        if (getType() != null) {
            obj.put("type", getType());
        }
        if (getComponent() != null) {
            obj.put("component", getComponent().flattenToShortString());
        }
        obj.put("flags", Integer.toHexString(getFlags()));

        if (getCategories() != null) {
            JSONArray categories = new JSONArray();
            for (int categoryNdx = getCategories().size() - 1; categoryNdx >= 0; --categoryNdx) {
                categories.put(new JSONObject().put("category", getCategories().toArray()[categoryNdx].toString()));
            }
            obj.put("categories", categories);
        }

        return obj;
    }

    private void saveToXml() throws IOException {
        XmlSerializer serializer = Xml.newSerializer();
        serializer.startDocument(null, true);
        final StringWriter writer = new StringWriter();
        serializer.setOutput(writer);
        serializer.startDocument("UTF-8", true);

        serializer.startTag(null, "intent");
        if (getAction() != null) {
            serializer.attribute(null, "action", getAction());
        }
        if (getData() != null) {
            serializer.attribute(null, "data", getData().toString());
        }
        if (getType() != null) {
            serializer.attribute(null, "type", getType());
        }
        if (getComponent() != null) {
            serializer.attribute(null, "component", getComponent().flattenToShortString());
        }
        serializer.attribute(null, "flags", Integer.toHexString(getFlags()));

        if (getCategories() != null) {
            serializer.startTag(null, "categories");
            for (int categoryNdx = getCategories().size() - 1; categoryNdx >= 0; --categoryNdx) {
                serializer.attribute(null, "category", getCategories().toArray()[categoryNdx].toString());
            }
            serializer.endTag(null, "categories");
        }
        serializer.endTag(null, "intent");

        serializer.endDocument();

        Log.i("AAA", serializer.toString());
        is = new ByteArrayInputStream(serializer.toString().getBytes());

    }

    private Intent restoreFromXML() throws IOException, XmlPullParserException {
        XmlPullParser in = Xml.newPullParser();
        in.setInput(new BufferedReader(new InputStreamReader(is)));
        Intent intent = new Intent();
        final int outerDepth = in.getDepth();

        int attrCount = in.getAttributeCount();
        for (int attrNdx = attrCount - 1; attrNdx >= 0; --attrNdx) {
            final String attrName = in.getAttributeName(attrNdx);
            final String attrValue = in.getAttributeValue(attrNdx);
            if ("action".equals(attrName)) {
                intent.setAction(attrValue);
            } else if ("data".equals(attrName)) {
                intent.setData(Uri.parse(attrValue));
            } else if ("type".equals(attrName)) {
                intent.setType(attrValue);
            } else if ("component".equals(attrName)) {
                intent.setComponent(ComponentName.unflattenFromString(attrValue));
            } else if ("flags".equals(attrName)) {
                intent.setFlags(Integer.valueOf(attrValue, 16));
            } else {
                Log.e("Intent", "restoreFromXml: unknown attribute=" + attrName);
            }
        }

        int event;
        String name;
        while (((event = in.next()) != XmlPullParser.END_DOCUMENT) &&
                (event != XmlPullParser.END_TAG || in.getDepth() < outerDepth)) {
            if (event == XmlPullParser.START_TAG) {
                name = in.getName();
                if ("categories".equals(name)) {
                    attrCount = in.getAttributeCount();
                    for (int attrNdx = attrCount - 1; attrNdx >= 0; --attrNdx) {
                        intent.addCategory(in.getAttributeValue(attrNdx));
                    }
                } else {
                    Log.w("Intent", "restoreFromXml: unknown name=" + name);
//                    XmlUtils.skipCurrentTag(in);
                }
            }
        }

        return intent;
    }

}
