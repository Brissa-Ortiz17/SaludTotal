package com.example.salud_total_v2;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CalendarioActivity extends AppCompatActivity {

    private MaterialCalendarView calendarView;
    private TextView tvCitaInfo;
    private Button btnRegresar;
    private int idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private OkHttpClient client = new OkHttpClient();
    private HashMap<Integer, String> doctorNames = new HashMap<>();
    private HashMap<Integer, String> specialtyNames = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        nombreUsuario = getIntent().getStringExtra("nombre");
        apellidoUsuario = getIntent().getStringExtra("apellido");

        calendarView = findViewById(R.id.calendarView);
        tvCitaInfo = findViewById(R.id.tvCitaInfo);
        btnRegresar = findViewById(R.id.btnRegresar);

        // Set welcome message
        TextView tvBienvenido = findViewById(R.id.tvBienvenido);
        String mensajeBienvenida = "Bienvenido " + nombreUsuario + " " + apellidoUsuario;
        tvBienvenido.setText(mensajeBienvenida);

        // Load data for doctor names and specialty names
        loadDoctorNames();
        loadSpecialtyNames();

        // Load events for the calendar
        loadCitas();

        // Set date selection listener
        calendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                tvCitaInfo.setText(""); // Clear previous information
                loadCitaInfo(date);
            }
        });

        // Set regresar button listener
        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CalendarioActivity.this, MainActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadDoctorNames() {
        String url1 = "http://192.168.100.144:3000/doctores";
        String url2 = "http://192.168.59.26:3000/doctores"; // Segunda IP

        // Seleccionar la IP activa (modifica según la que necesites usar)
        String selectedUrl = url1; // Cambia a url2 si quieres usar la otra IP

        Request request = new Request.Builder()
                .url(selectedUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CalendarioActivity.this, "Error al cargar nombres de doctores: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CalendarioActivity.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id_doctor");
                        String name = jsonObject.getString("nombre");
                        doctorNames.put(id, name);
                    }
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CalendarioActivity.this, "Error al parsear nombres de doctores: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void loadSpecialtyNames() {
        String url1 = "http://192.168.100.144:3000/especialidades";
        String url2 = "http://192.168.59.26:3000/especialidades"; // Segunda IP

        // Seleccionar la IP activa (modifica según la que necesites usar)
        String selectedUrl = url1; // Cambia a url2 si quieres usar la otra IP

        Request request = new Request.Builder()
                .url(selectedUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CalendarioActivity.this, "Error al cargar nombres de especialidades: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CalendarioActivity.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt("id_especialidad");
                        String name = jsonObject.getString("nombre_especialidad");
                        specialtyNames.put(id, name);
                    }
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CalendarioActivity.this, "Error al parsear nombres de especialidades: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private void loadCitas() {
        String url1 = "http://192.168.100.144:3000/getCitasByUsuario?id_usuario=" + idUsuario;
        String url2 = "http://192.168.59.26:3000/getCitasByUsuario?id_usuario=" + idUsuario;

        String selectedUrl = url1; // Cambia a url2 si quieres usar la otra IP
        Request request = new Request.Builder()
                .url(selectedUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CalendarioActivity.this, "Error al cargar citas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(CalendarioActivity.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    final List<CalendarDay> dates = new ArrayList<>();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String fecha = jsonObject.getString("fecha_cita").split("T")[0];
                        CalendarDay day = CalendarDay.from(
                                Integer.parseInt(fecha.substring(0, 4)),
                                Integer.parseInt(fecha.substring(5, 7)) - 1, // Mes en CalendarDay es 0-based
                                Integer.parseInt(fecha.substring(8, 10))
                        );
                        dates.add(day);
                    }

                    runOnUiThread(() -> {
                        calendarView.addDecorator(new EventDecorator(Color.RED, dates));
                    });
                } catch (JSONException e) {
                    runOnUiThread(() -> Toast.makeText(CalendarioActivity.this, "Error al parsear citas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }


    private void loadCitaInfo(CalendarDay date) {
        String formattedDate = String.format("%04d-%02d-%02d", date.getYear(), date.getMonth() + 1, date.getDay());
        String url1 = "http://192.168.100.144:3000/getCitaByDate?id_usuario=" + idUsuario + "&fecha_cita=" + formattedDate;
        String url2 = "http://192.168.59.26:3000/getCitaByDate?id_usuario=" + idUsuario + "&fecha_cita=" + formattedDate; // Nueva IP

    // Seleccionar la IP activa
        String selectedUrl = url1; // Cambia a url2 si deseas usar la nueva IP

        Request request = new Request.Builder()
                .url(selectedUrl)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(CalendarioActivity.this, "Error al cargar información de la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> tvCitaInfo.setText("No existen citas para este día"));
                    return;
                }

                try {
                    String responseData = response.body().string();
                    if (responseData.isEmpty()) {
                        runOnUiThread(() -> tvCitaInfo.setText("No existen citas para este día"));
                    } else {
                        JSONObject jsonObject = new JSONObject(responseData);
                        String fecha = jsonObject.getString("fecha_cita");
                        String hora = jsonObject.getString("hora_cita").substring(0, 5);
                        int idDoctor = jsonObject.getInt("id_doctor");
                        int idEspecialidad = jsonObject.getInt("id_especialidad");

                        final String info = "Fecha: " + fecha +
                                "\nHora: " + hora +
                                "\nDoctor: " + doctorNames.get(idDoctor) +
                                "\nEspecialidad: " + specialtyNames.get(idEspecialidad) +
                                "\nUbicación: " + jsonObject.getString("ubicacion") +
                                "\nNota: " + jsonObject.getString("nota");

                        runOnUiThread(() -> tvCitaInfo.setText(info));
                    }
                } catch (JSONException e) {
                    runOnUiThread(() -> Toast.makeText(CalendarioActivity.this, "Error al parsear información de la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

}
