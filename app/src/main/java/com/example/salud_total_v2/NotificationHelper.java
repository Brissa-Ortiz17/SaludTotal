package com.example.salud_total_v2;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;


import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NotificationHelper {

    public static void crearCanalDeNotificacion(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Recordatorio";
            String description = "Canal para notificaciones de recordatorios";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("recordatorio_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }


    public static void programarNotificaciones(Context context, int idUsuario) {
        crearCanalDeNotificacion(context);
        OkHttpClient client = new OkHttpClient();

        String url = "http://10.0.2.2:3000/getNotificacionesByUsuario?id_usuario=" + idUsuario;

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    return;
                }

                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String fecha = jsonObject.getString("fecha"); // Formato: "yyyy-MM-dd"
                        String hora = jsonObject.getString("hora");   // Formato: "HH:mm:ss"
                        String mensaje = jsonObject.getString("nota");

                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        Date fechaHora = sdf.parse(fecha + " " + hora);
                        long tiempoAlarma = fechaHora.getTime() - (15 * 60 * 1000); // 15 minutos antes

                        // Configurar AlarmManager
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        Intent intent = new Intent(context, NotificationReceiver.class);
                        intent.putExtra("mensaje", mensaje);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                context,
                                i,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        if (alarmManager != null) {
                            alarmManager.setExact(AlarmManager.RTC_WAKEUP, tiempoAlarma, pendingIntent);
                        }
                    }
                } catch (JSONException | ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

}
