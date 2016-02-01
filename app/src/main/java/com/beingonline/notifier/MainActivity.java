package com.beingonline.notifier;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.Menu;
import android.widget.ProgressBar;

public class MainActivity extends myActivity {

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
   }

    @Override
    protected void onResume() {
        super.onResume();
    }

    //    TextView textResult;
    ProgressBar progressBar;

    private MyBroadcastReceiver myBroadcastReceiver;
    private MyBroadcastReceiver_Update myBroadcastReceiver_Update;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        progressBar = (ProgressBar)findViewById(R.id.progressbar);

        //prepare MyParcelable passing to intentMyIntentService
        String msgToIntentService = "Android-er";

        //Start MyIntentService
        final Intent intentMyIntentService = new Intent(this, MyIntentService.class);
        intentMyIntentService.putExtra(MyIntentService.EXTRA_KEY_IN, msgToIntentService);
        startService(intentMyIntentService);

        myBroadcastReceiver = new MyBroadcastReceiver();
        myBroadcastReceiver_Update = new MyBroadcastReceiver_Update();

        //register BroadcastReceiver
        IntentFilter intentFilter = new IntentFilter(MyIntentService.ACTION_MyIntentService);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver, intentFilter);

        IntentFilter intentFilter_update = new IntentFilter(MyIntentService.ACTION_MyUpdate);
        intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(myBroadcastReceiver_Update, intentFilter_update);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        showFinished();
        //un-register BroadcastReceiver
        unregisterReceiver(myBroadcastReceiver);
        unregisterReceiver(myBroadcastReceiver_Update);
    }




    public class MyBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            new Getmonitors().execute();

            int update = intent.getIntExtra(MyIntentService.EXTRA_KEY_UPDATE, 0);
            progressBar.setProgress(update);

            //un-register BroadcastReceiver
            unregisterReceiver(myBroadcastReceiver);
            unregisterReceiver(myBroadcastReceiver_Update);

            myBroadcastReceiver = new MyBroadcastReceiver();
            myBroadcastReceiver_Update = new MyBroadcastReceiver_Update();

            //register BroadcastReceiver
            IntentFilter intentFilter = new IntentFilter(MyIntentService.ACTION_MyIntentService);
            intentFilter.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(myBroadcastReceiver, intentFilter);

            IntentFilter intentFilter_update = new IntentFilter(MyIntentService.ACTION_MyUpdate);
            intentFilter_update.addCategory(Intent.CATEGORY_DEFAULT);
            registerReceiver(myBroadcastReceiver_Update, intentFilter_update);

        }
    }

    public class MyBroadcastReceiver_Update extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int update = intent.getIntExtra(MyIntentService.EXTRA_KEY_UPDATE, 0);
            progressBar.setProgress(update);
        }
    }

    public void showFinished() {
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        long[] v = {500,1000};

        // both of these approaches now work: FLAG_CANCEL, FLAG_UPDATE; the uniqueInt may be the real solution.
        //PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, showFullQuoteIntent, PendingIntent.FLAG_CANCEL_CURRENT);

        Notification notification = new NotificationCompat.Builder(this)
                .setTicker("hello")
                .setSmallIcon(android.R.drawable.ic_dialog_alert)
                .setContentTitle("BeingOnline")
                .setContentText("Syncing has timed out!")
                .setAutoCancel(true)
                .setSound(uri)
                .setVibrate(v)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(0, notification);
    }






}
