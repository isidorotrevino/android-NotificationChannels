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

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.MonitorNotifier;
import org.altbeacon.beacon.RangeNotifier;
import org.altbeacon.beacon.Region;

import java.util.Collection;

/**
 * Display main screen for sample. Displays controls for sending test notifications.
 */
public class MainActivity extends Activity implements BeaconConsumer {
    private static final String TAG = MainActivity.class.getSimpleName();

    protected BeaconManager beaconManager;

    private static final int NOTI_PRIMARY1 = 1100;
    private static final int NOTI_PRIMARY2 = 1101;
    private static final int NOTI_SECONDARY1 = 1200;
    private static final int NOTI_SECONDARY2 = 1201;

    /*
     * A view model for interacting with the UI elements.
     */
    private MainUi ui;

    /*
     * A
     */
    private NotificationHelper noti;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        noti = new NotificationHelper(this);
        ui = new MainUi(findViewById(R.id.activity_main));

        beaconManager = BeaconManager.getInstanceForApplication(this);

        // To detect proprietary beacons, you must add a line like below corresponding to your beacon
        // type.  Do a web search for "setBeaconLayout" to get the proper expression.
        // beaconManager.getBeaconParsers().add(new BeaconParser().
        //        setBeaconLayout("m:2-3=beac,i:4-19,i:20-21,i:22-23,p:24-24,d:25-25"));
        beaconManager.bind(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        beaconManager.unbind(this);
    }


    /**
     * Send activity notifications.
     *
     * @param id    The ID of the notification to create
     * @param title The title of the notification
     */
    public void sendNotification(int id, String title) {
        Notification.Builder nb = null;
        switch (id) {
            case NOTI_PRIMARY1:
                nb = noti.getNotification1(title, getString(R.string.primary1_body));
                break;

            case NOTI_PRIMARY2:
                nb = noti.getNotification1(title, getString(R.string.primary2_body));
                break;

            case NOTI_SECONDARY1:
                nb = noti.getNotification2(title, getString(R.string.secondary1_body));
                break;

            case NOTI_SECONDARY2:
                nb = noti.getNotification2(title, getString(R.string.secondary2_body));
                break;
        }
        if (nb != null) {
            noti.notify(id, nb);
        }
    }

    /**
     * Send Intent to load system Notification Settings for this app.
     */
    public void goToNotificationSettings() {
        Intent i = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        startActivity(i);
    }

    /**
     * Send intent to load system Notification Settings UI for a particular channel.
     *
     * @param channel Name of channel to configure
     */
    public void goToNotificationSettings(String channel) {
        Intent i = new Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS);
        i.putExtra(Settings.EXTRA_APP_PACKAGE, getPackageName());
        i.putExtra(Settings.EXTRA_CHANNEL_ID, channel);
        startActivity(i);
    }

    @Override
    public void onBeaconServiceConnect() {
        //Monitoreo
        beaconManager.removeAllMonitorNotifiers();
        final MainActivity act = this;
        beaconManager.addMonitorNotifier(new MonitorNotifier() {
            @Override
            public void didEnterRegion(Region region) {
                Log.i(TAG, "I just saw an beacon for the first time!");

                sendNotification(NOTI_SECONDARY2,"");

            }

            @Override
            public void didExitRegion(Region region) {
                Log.i(TAG, "I no longer see an beacon");
            }

            @Override
            public void didDetermineStateForRegion(int state, Region region) {
                Log.i(TAG, "I have just switched from seeing/not seeing beacons: " + state);
                if(state == MonitorNotifier.INSIDE){
                    sendNotification(NOTI_SECONDARY2,"");
                }
            }
        });

        try {
            beaconManager.startMonitoringBeaconsInRegion(new Region("myMonitoringUniqueId", null, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, "Error al monitorear", e);
        }

        beaconManager.removeAllRangeNotifiers();
        beaconManager.addRangeNotifier(new RangeNotifier() {
            @Override
            public void didRangeBeaconsInRegion(Collection<Beacon> beacons, Region region) {
                if (beacons.size() > 0) {
                    Log.i(TAG, "The first beacon I see is about " + beacons.iterator().next().getDistance() + " meters away.");
                }
            }
        });

        try {
            beaconManager.startRangingBeaconsInRegion(new Region("myRangingUniqueId", null, null, null));
        } catch (RemoteException e) {
            Log.e(TAG, "Error al monitorear", e);
        }

    }

    /**
     * View model for interacting with Activity UI elements. (Keeps core logic for sample
     * seperate.)
     */
    class MainUi implements View.OnClickListener {
        final TextView titlePrimary;
        final TextView titleSecondary;

        private MainUi(View root) {
            titlePrimary = (TextView) root.findViewById(R.id.main_primary_title);
            ((Button) root.findViewById(R.id.main_primary_send1)).setOnClickListener(this);
            ((Button) root.findViewById(R.id.main_primary_send2)).setOnClickListener(this);
            ((ImageButton) root.findViewById(R.id.main_primary_config)).setOnClickListener(this);

            titleSecondary = (TextView) root.findViewById(R.id.main_secondary_title);
            ((Button) root.findViewById(R.id.main_secondary_send1)).setOnClickListener(this);
            ((Button) root.findViewById(R.id.main_secondary_send2)).setOnClickListener(this);
            ((ImageButton) root.findViewById(R.id.main_secondary_config)).setOnClickListener(this);

            ((Button) root.findViewById(R.id.btnA)).setOnClickListener(this);
        }

        private String getTitlePrimaryText() {
            if (titlePrimary != null) {
                return titlePrimary.getText().toString();
            }
            return "";
        }

        public String getTitleSecondaryText() {
            if (titlePrimary != null) {
                return titleSecondary.getText().toString();
            }
            return "";
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.main_primary_send1:
                    sendNotification(NOTI_PRIMARY1, getTitlePrimaryText());
                    break;
                case R.id.main_primary_send2:
                    sendNotification(NOTI_PRIMARY2, getTitlePrimaryText());
                    break;
                case R.id.main_primary_config:
                    goToNotificationSettings(NotificationHelper.PRIMARY_CHANNEL);
                    break;

                case R.id.main_secondary_send1:
                    sendNotification(NOTI_SECONDARY1, getTitleSecondaryText());
                    break;
                case R.id.main_secondary_send2:
                    sendNotification(NOTI_SECONDARY2, getTitleSecondaryText());
                    break;
                case R.id.main_secondary_config:
                    goToNotificationSettings(NotificationHelper.SECONDARY_CHANNEL);
                    break;
                case R.id.btnA:
                    goToNotificationSettings();
                    break;
                default:
                    Log.e(TAG, "Unknown click event.");
                    break;
            }
        }
    }
}
