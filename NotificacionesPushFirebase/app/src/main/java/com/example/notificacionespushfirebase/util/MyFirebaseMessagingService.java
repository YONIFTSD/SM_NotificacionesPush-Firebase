package com.example.notificacionespushfirebase.util;

import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.notificacionespushfirebase.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();

    private NotificationUtils notificationUtils;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        Log.d("NEW TOKEN", s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, " DESDE: " + remoteMessage.getFrom());
        if(remoteMessage == null) return;
        if(remoteMessage.getNotification() != null){


            manejarNotificacion(remoteMessage.getNotification().getTitle());
        }

        if(remoteMessage.getData().size() > 0){

            interpretarMensaje(remoteMessage);
        }
    }

    private void manejarNotificacion(String mensaje){
        if(!NotificationUtils.isAppIsInBackground(getApplicationContext())){
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("mensaje", mensaje);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationAlarm();
        }
    }

    private void interpretarMensaje(RemoteMessage remoteMessage){


        String titulo = remoteMessage.getNotification().getTitle();
        String mensaje = remoteMessage.getNotification().getBody();
        String urlImagen = remoteMessage.getNotification().getImageUrl().toString();


        if(!NotificationUtils.isAppIsInBackground(getApplicationContext())){
            Intent pushNotification = new Intent(Config.PUSH_NOTIFICATION);
            pushNotification.putExtra("mensaje", titulo);
            LocalBroadcastManager.getInstance(this).sendBroadcast(pushNotification);

            NotificationUtils notificationUtils = new NotificationUtils(getApplicationContext());
            notificationUtils.playNotificationAlarm();
            notificationUtils.senNotificationForeground(titulo, mensaje, urlImagen);
        }else{
            Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
            resultIntent.putExtra("mensaje", titulo);
            if(TextUtils.isEmpty(urlImagen)) {
                notificationUtils.showNotificationMessage(titulo, mensaje, resultIntent);
            }else{
                resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                notificationUtils.showNotificationMessage(titulo, mensaje, resultIntent, urlImagen);
            }
        }

    }
}
