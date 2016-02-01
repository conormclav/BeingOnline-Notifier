package com.beingonline.notifier;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by conor.mclaverty on 15/01/2016.
 */
public class myActivity extends ListActivity {

    // URL to get contacts JSON
    public static String url = "http://www.conormclaverty.co.uk/api.html";

    // JSON Node names
    public static final String TAG_monitorINFO = "monitors";
    public static final String TAG_ID = "id";
    public static final String TAG_NAME = "name";
    public static final String TAG_EMAIL = "email";
    public static final String TAG_STATUS = "status";

    public final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Calling async task to get json
        new Getmonitors().execute();



    }



    /**
     * Async task class to get json by making HTTP call
     */
    public class Getmonitors extends AsyncTask<Void, Void, Void> {

        // Hashmap for ListView
        ArrayList<HashMap<String, String>> monitorList;
        ProgressDialog pDialog;

        @Override
        public void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(myActivity.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        public Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            NewWebRequest webreq = new NewWebRequest();

            // Making a request to url and getting response
            String jsonStr = webreq.makeWebServiceCall(url, NewWebRequest.GET);

            monitorList = ParseJSON(jsonStr);

            return null;
        }

        @Override
        public void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */

//            Intent intent = new Intent(MainActivity.this, myService.class);
//            startService(intent);


            ListAdapter adapter = new SimpleAdapter(

                    myActivity.this, monitorList,
                    R.layout.list_item, new String[]{TAG_NAME, TAG_EMAIL,
                    TAG_STATUS}, new int[]{R.id.name,
                    R.id.email, R.id.statuslist});

            setListAdapter(adapter);





        }
    }



    public ArrayList<HashMap<String, String>> ParseJSON(String json) {
        if (json != null) {
            try {
                // Hashmap for ListView
                ArrayList<HashMap<String, String>> monitorList = new ArrayList<HashMap<String, String>>();

                JSONObject jsonObj = new JSONObject(json);

                // Getting JSON Array node
                JSONArray monitors = jsonObj.getJSONArray(TAG_monitorINFO);

                // looping through All monitors

                for (int i = 0; i < monitors.length(); i++) {
                    JSONObject c = monitors.getJSONObject(i);

                    String id = c.getString(TAG_ID);
                    String name = c.getString(TAG_NAME);
                    String email = c.getString(TAG_EMAIL);
                    String statusid = c.getString(TAG_STATUS);

                    // Phone node is JSON Object

                    // tmp hashmap for single monitor
                    HashMap<String, String> monitor = new HashMap<String, String>();

                    // adding each child node to HashMap key => value
                    monitor.put(TAG_ID, id);
                    monitor.put(TAG_NAME, name);
                    monitor.put(TAG_EMAIL, email);

                    if (statusid.equals("100")) {
                        monitor.put(TAG_STATUS, statusid + " - OK");

                    } else if (statusid.equals("200")) {
                        monitor.put(TAG_STATUS, statusid + " - NEEDS ATTENTION");
                    } else if (statusid.equals("300")) {
                        monitor.put(TAG_STATUS, statusid + " - FAILED");

                    } else if (statusid.equals("400")) {
                        monitor.put(TAG_STATUS, statusid + " - DOWN");
                        showNotification();

                    }

                    // adding monitor to monitors list
                    monitorList.add(monitor);
                }
                return monitorList;
            } catch (JSONException e) {
//                e.printStackTrace();
                return null;
            }
        } else {
            return null;
        }
    }

    private void showNotification() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {500,1000};

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("hello")
                .setSmallIcon(android.R.drawable.ic_menu_help)
                .setContentTitle("BeingOnline")
                .setContentText("Attention Required!")
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(v)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }

}
