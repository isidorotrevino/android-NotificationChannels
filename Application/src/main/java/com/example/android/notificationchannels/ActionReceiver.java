package com.example.android.notificationchannels;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class ActionReceiver extends Activity {

    public static final String NOTIFICATION_ID = "NOTIFICATION_ID";


    //@Override
    public void onReceive(Context context, Intent intent) {
        Log.w("Check", "Inside On Receiver");

        Toast.makeText(context.getApplicationContext(),"received",Toast.LENGTH_SHORT).show();

        String action=intent.getStringExtra("action");
        if(action.equals("descartar")){
            performAction1(context);
        }
        else if(action.equals("aplicar")){
            performAction2(context);

        }
        //This is used to close the notification tray
        Intent it = new Intent(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        context.sendBroadcast(it);
    }

    public void performAction1(Context context){
        Toast.makeText(context.getApplicationContext(),"received 1",Toast.LENGTH_LONG).show();
    }

    public void performAction2(Context context){
        Toast.makeText(context.getApplicationContext(),"received 2",Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.cancel(getIntent().getIntExtra(NOTIFICATION_ID, -1));
        Log.w("Check", "Inside On create");

        Toast.makeText(this.getApplicationContext(),"Descartando notificación",Toast.LENGTH_SHORT).show();

        finish(); // since finish() is called in onCreate(), onDestroy() will be called immediately


    }

    public static PendingIntent getDismissIntent(int notificationId, Context context) {
        Intent intent = new Intent(context, ActionReceiver.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(NOTIFICATION_ID, notificationId);
        PendingIntent dismissIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        return dismissIntent;
    }
}
