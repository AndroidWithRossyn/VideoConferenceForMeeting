package com.videomeetings.conference.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Vibrator;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.videomeetings.conference.AdHelper.AdsJsonPOJO;
import com.videomeetings.conference.AdHelper.AppInterfaces;
import com.videomeetings.conference.AdHelper.AppPreferencesManger;
import com.videomeetings.conference.AdHelper.FirebaseUtils;
import com.videomeetings.conference.BuildConfig;
import com.videomeetings.conference.R;
import com.videomeetings.conference.activity.HomeActivity;
import com.videomeetings.conference.base.PrefManager;
import com.videomeetings.conference.utils.Global;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.io.IOException;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    MediaPlayer mediaPlayer;
    public static final String FCM_PARAM = "picture";
    private static final String CHANNEL_NAME = BuildConfig.APPLICATION_ID + "FCM";
    private static final String CHANNEL_NAME_ID = BuildConfig.APPLICATION_ID + "FCM_ID";
    private static final String CHANNEL_DESC = BuildConfig.APPLICATION_ID + "Firebase Cloud Messaging";
    PrefManager prefManager;

    @Override
    public void onMessageReceived(@androidx.annotation.NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        prefManager = new PrefManager(this);
        if (!remoteMessage.getData().isEmpty()) {
            if (remoteMessage.getData().containsValue("10")) {
                FirebaseUtils.initiateAndStoreFirebaseRemoteConfig(getApplicationContext(), new AppInterfaces.AdDataInterface() {
                    @Override
                    public void getAdData(AdsJsonPOJO adsJsonPOJO) {
                        AppPreferencesManger appPreferencesManger = new AppPreferencesManger(getApplicationContext());
                        appPreferencesManger.setAdsModel(adsJsonPOJO);
                    }
                });
            }
        }
        if (prefManager.getBoolean(Global.ISNOTIFY)) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();
            java.util.Map<String, String> data = remoteMessage.getData();
            sendNotification(notification, data);
        }
    }

    private void sendNotification(RemoteMessage.Notification notification, java.util.Map<String, String> data) {
//        audioPlayer();

        Intent intent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Uri soundUri = Uri.parse("android.resource://" + getApplicationContext().getPackageName() + "/" + R.raw.sound_file);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, CHANNEL_NAME_ID)
                .setContentTitle(notification.getTitle() + "===" + prefManager.getBoolean(Global.ISNOTIFY))
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setSmallIcon(R.mipmap.ic_launcher_round);

        try {
            String picture = data.get(FCM_PARAM);
            if (picture != null && !"".equals(picture)) {
                java.net.URL url = new java.net.URL(picture);
                Bitmap bigPicture = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                notificationBuilder.setStyle(
                        new NotificationCompat.BigPictureStyle().bigPicture(bigPicture).setSummaryText(notification.getBody())
                );
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AudioAttributes audioAttributes = new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .build();
            NotificationChannel channel = new NotificationChannel(CHANNEL_NAME_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription(CHANNEL_DESC);
            channel.setShowBadge(true);
            channel.canShowBadge();
            channel.enableLights(true);
            if (prefManager.getBoolean(Global.ISVIBRATE)) {
                channel.enableVibration(true);
                channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
            }
            if (prefManager.getBoolean(Global.ISOUND)) {
                channel.setSound(soundUri, audioAttributes);
            }

            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0, notificationBuilder.build());
        audioPlayer();
    }

    public void audioPlayer() {
        //set up MediaPlayer
        try {
            /*if (!prefManager.getBoolean(Global.ISOUND)) {
                mediaPlayer = MediaPlayer.create(this, R.raw.mute_sound);
            } else {*/
                mediaPlayer = MediaPlayer.create(this, R.raw.sound_file);
//            }

            /*if (prefManager.getBoolean(Global.ISVIBRATE)) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(2000);
            }*/
            mediaPlayer.setVolume(1,1);
            mediaPlayer.prepare();
            if (prefManager.getBoolean(Global.ISOUND)) {
                mediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void handleIntent(@NonNull Intent intent) {
        prefManager = new PrefManager(this);
        if (prefManager.getBoolean(Global.ISNOTIFY)) {
            if (prefManager.getBoolean(Global.ISVIBRATE)) {
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(2000);
            }
            super.handleIntent(intent);
        }
    }
}
