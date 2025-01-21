package com.example.salud_total_v2;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import androidx.core.app.NotificationCompat;

public class NotificationReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Información pasada desde el PendingIntent
        String mensaje = intent.getStringExtra("mensaje");

        // Crear una notificación
        Uri sonido = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "SaludTotalChannel")
                .setSmallIcon(R.drawable.ic_notification)  // Agrega un ícono adecuado aquí
                .setContentTitle("Recordatorio")
                .setContentText(mensaje)
                .setAutoCancel(true)
                .setSound(sonido)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        // Intent para abrir la aplicación al hacer clic en la notificación
        Intent resultIntent = new Intent(context, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}
