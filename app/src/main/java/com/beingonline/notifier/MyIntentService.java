package com.beingonline.notifier;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

/**
 * Created by conor.mclaverty on 19/01/2016.
 */
public class MyIntentService extends IntentService {

    public static final String ACTION_MyIntentService = "com.example.androidintentservice.RESPONSE";
    public static final String ACTION_MyUpdate = "com.example.androidintentservice.UPDATE";
    public static final String EXTRA_KEY_IN = "EXTRA_IN";
    public static final String EXTRA_KEY_OUT = "EXTRA_OUT";
    public static final String EXTRA_KEY_UPDATE = "EXTRA_UPDATE";
    String msgFromActivity;
    String extraOut;

    public MyIntentService() {
        super("com");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        //get input
        msgFromActivity = intent.getStringExtra(EXTRA_KEY_IN);
        extraOut = "Hello: " + msgFromActivity;

        for (int x = 0; x <= 10; x++) {

            for (int i = 0; i <= 10; i++) {
                try {
                    Thread.sleep(6000*10); /* Execute every 10 mins */
                } catch (InterruptedException e) {

                    Log.v("loop", "");

                    e.printStackTrace();
                    Intent intentUpdate = new Intent();
                    intentUpdate.setAction(ACTION_MyUpdate);
                    intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                    intentUpdate.putExtra(EXTRA_KEY_UPDATE, i);
                    sendBroadcast(intentUpdate);
                }

                //send update
                Intent intentUpdate = new Intent();
                intentUpdate.setAction(ACTION_MyUpdate);
                intentUpdate.addCategory(Intent.CATEGORY_DEFAULT);
                intentUpdate.putExtra(EXTRA_KEY_UPDATE, i);
                sendBroadcast(intentUpdate);
            }

            //return result
            Intent intentResponse = new Intent();
            intentResponse.setAction(ACTION_MyIntentService);
            intentResponse.addCategory(Intent.CATEGORY_DEFAULT);
            intentResponse.putExtra(EXTRA_KEY_OUT, extraOut);
            sendBroadcast(intentResponse);
        }

    }

}


