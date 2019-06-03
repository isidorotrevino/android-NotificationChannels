/*
* Copyright 2017 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

package com.example.android.notificationchannels;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;

import java.util.Random;

/**
 * Helper class to manage notification channels, and create notifications.
 */
class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    public NotificationHelper(Context ctx) {
        super(ctx);

        NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                getString(R.string.noti_channel_default), NotificationManager.IMPORTANCE_DEFAULT);
        chan1.setLightColor(Color.GREEN);
        chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager().createNotificationChannel(chan1);

        NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                getString(R.string.noti_channel_second), NotificationManager.IMPORTANCE_HIGH);
        chan2.setLightColor(Color.BLUE);
        chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(chan2);

            /*
        ActionReceiver receiver = new ActionReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("descartar");
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        this.registerReceiver(receiver,filter);*/
    }

    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
    */
    public Notification.Builder getNotification1(String title, String body) {
        return new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                 .setContentTitle(title)
                 .setContentText(body)
                 .setSmallIcon(getSmallIcon())
                 .setAutoCancel(true);
    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    public Notification.Builder getNotification2(String title, String body) {


        Notification.Builder notification =
                new Notification.Builder(getApplicationContext(), SECONDARY_CHANNEL)
                 .setContentTitle(title)
                 //.setContentText(body)
                 .setSmallIcon(getSmallIcon())
                .setStyle(new Notification.BigPictureStyle()
                    .bigPicture(BitmapFactory.decodeResource(getResources(),getBigImage())))
                 .setAutoCancel(true);


/*
        Intent intent =new Intent(this,ActionReceiver.class);
        intent.setAction("descartar");
        intent.putExtra("action","descartar");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        PendingIntent intentDescartar=PendingIntent.getBroadcast(this,1002,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
                //PendingIntent.getActivity(this,1001,
                //intent,PendingIntentFlags.CancelCurrent);
*/

        int notificationId = new Random().nextInt(); // just use a counter in some util class...
        PendingIntent dismissIntent = ActionReceiver.getDismissIntent(notificationId, this);



        Intent intent2 = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.salondelaplasticamexicana.bellasartes.gob.mx/"));
        intent2.putExtra("action","aplicar");
        intent2.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intentAplicar= PendingIntent.getActivity(this,0,
                intent2,PendingIntent.FLAG_CANCEL_CURRENT);

        notification = notification.setContentIntent(intentAplicar);
        //notification = notification.addAction(android.R.drawable.ic_input_delete,"Descartar",intentDescartar);
        notification = notification.addAction(android.R.drawable.ic_input_delete,"Descartar",dismissIntent);
        notification = notification.addAction(android.R.drawable.btn_star,"Ver más",intentAplicar);



        return notification;
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon() {

        return android.R.drawable.stat_notify_chat;
    }

    private int getBigImage(){
        return R.drawable.salon_imagen_2;
    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return manager;
    }
}
