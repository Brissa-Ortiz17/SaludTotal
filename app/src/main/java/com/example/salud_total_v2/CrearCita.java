package com.example.salud_total_v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log; // Asegúrate de importar Log
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class CrearCita extends AppCompatActivity {

    private EditText etFecha, etHora, etDireccion, etNota;
    private Spinner spinnerUsuario, spinnerDoctor, spinnerEspecialidad;
    private Button btnHecho, btnRegresar;
    private OkHttpClient client = new OkHttpClient();
    private Calendar citaCalendar;
    private int idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Obtener los datos del usuario logueado desde el Intent
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("id_usuario", -1);
        nombreUsuario = intent.getStringExtra("nombre");
        apellidoUsuario = intent.getStringExtra("apellido");

        setContentView(R.layout.activity_crear_cita);

        etFecha = findViewById(R.id.etFecha);
        etHora = findViewById(R.id.etHora);
        etDireccion = findViewById(R.id.etDireccion);
        etNota = findViewById(R.id.etNota);
        spinnerUsuario = findViewById(R.id.spinnerUsuario);
        spinnerDoctor = findViewById(R.id.spinnerDoctor);
        spinnerEspecialidad = findViewById(R.id.spinnerEspecialidad);
        btnHecho = findViewById(R.id.btnHecho);
        btnRegresar = findViewById(R.id.btnRegresar);

        // Definir las dos direcciones IP
        String ip1 = "http://192.168.100.144:3000";
        String ip2 = "http://192.168.59.26:3000"; // Nueva IP

        // Seleccionar la IP activa
                String selectedIp = ip1; // Cambia a ip2 si deseas usar la nueva IP

        // Cargar datos para los spinners
        loadSpinnerData(selectedIp + "/usuarios", spinnerUsuario, "id_usuario", "nombre_completo");
        loadSpinnerData(selectedIp + "/doctores", spinnerDoctor, "id_doctor", "nombre");
        loadSpinnerData(selectedIp + "/especialidades", spinnerEspecialidad, "id_especialidad", "nombre_especialidad");


        btnHecho.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Obtener datos de la cita
                int idUsuario = getSelectedId(spinnerUsuario);
                int idDoctor = getSelectedId(spinnerDoctor);
                int idEspecialidad = getSelectedId(spinnerEspecialidad);
                String fechaCita = etFecha.getText().toString();
                String horaCita = etHora.getText().toString();
                String direccion = etDireccion.getText().toString();
                String nota = etNota.getText().toString();

                // Crear cita en la base de datos
                crearCita(idUsuario, idDoctor, fechaCita, horaCita, idEspecialidad, direccion, nota);
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirigir a la actividad de citas médicas y pasar los datos del usuario logueado
                Intent intent = new Intent(CrearCita.this, CitasMedicas.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
                finish();
            }
        });

        // Set the default user as the logged in user
        spinnerUsuario.post(new Runnable() {
            @Override
            public void run() {
                setDefaultUser(idUsuario);
            }
        });
    }

    private void loadSpinnerData(String url, final Spinner spinner, final String idKey, final String nameKey) {
        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CrearCita.this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CrearCita.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                final List<String> items = new ArrayList<>();
                final List<Integer> ids = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Verifica si el campo 'nameKey' existe
                        if (!jsonObject.has(nameKey)) {
                            Log.e("CrearCita", "No value for " + nameKey);
                            continue;
                        }

                        int id = jsonObject.getInt(idKey);
                        String name = jsonObject.getString(nameKey);  // Cambiado a 'nombre_completo'
                        ids.add(id);
                        items.add(name);
                    }
                } catch (JSONException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CrearCita.this, "Error al parsear los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(CrearCita.this, android.R.layout.simple_spinner_item, items);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setTag(ids);
                        setDefaultUser(idUsuario);
                    }
                });
            }
        });
    }

    private int getSelectedId(Spinner spinner) {
        int position = spinner.getSelectedItemPosition();
        if (position != Spinner.INVALID_POSITION) {
            List<Integer> ids = (List<Integer>) spinner.getTag();
            return ids.get(position);
        }
        return -1;
    }

    private void setDefaultUser(int idUsuario) {
        List<Integer> ids = (List<Integer>) spinnerUsuario.getTag();
        if (ids != null) {
            int position = ids.indexOf(idUsuario);
            if (position != -1) {
                spinnerUsuario.setSelection(position);
            }
        }
    }

    private void crearCita(int idUsuario, int idDoctor, String fechaCita, String horaCita, int idEspecialidad, String direccion, String nota) {
        OkHttpClient client = new OkHttpClient();
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject citaData = new JSONObject();
        try {
            citaData.put("id_usuario", idUsuario);
            citaData.put("id_doctor", idDoctor);
            citaData.put("fecha_cita", fechaCita);
            citaData.put("hora_cita", horaCita);
            citaData.put("id_especialidad", idEspecialidad);
            citaData.put("ubicacion", direccion);
            citaData.put("nota", nota);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Definir las dos direcciones IP
        String ip1 = "http://192.168.100.144:3000";
        String ip2 = "http://192.168.59.26:3000"; // Nueva IP

        // Seleccionar la IP activa
        String selectedIp = ip1; // Cambia a ip2 si deseas usar la nueva IP

        // Construir el cuerpo de la solicitud
        RequestBody body = RequestBody.create(citaData.toString(), JSON);

        // Construir la solicitud
        Request request = new Request.Builder()
                .url(selectedIp + "/createAppointment")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CrearCita.this, "Error al crear la cita: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CrearCita.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CrearCita.this, "Cita creada exitosamente", Toast.LENGTH_SHORT).show();
                        // Programar notificaciones
                        programarNotificaciones();
                    }
                });
            }
        });
    }

    private void programarNotificaciones() {
        // Obtener fecha y hora de la cita
        String fechaCita = etFecha.getText().toString();
        String horaCita = etHora.getText().toString();

        // Convertir fecha y hora a Calendar
        String[] fechaParts = fechaCita.split("-");
        String[] horaParts = horaCita.split(":");

        citaCalendar = Calendar.getInstance();
        citaCalendar.set(Calendar.YEAR, Integer.parseInt(fechaParts[0]));
        citaCalendar.set(Calendar.MONTH, Integer.parseInt(fechaParts[1]) - 1); // Meses en Calendar empiezan desde 0
        citaCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(fechaParts[2]));
        citaCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(horaParts[0]));
        citaCalendar.set(Calendar.MINUTE, Integer.parseInt(horaParts[1]));
        citaCalendar.set(Calendar.SECOND, 0);

        // Notificación 24 horas antes
        Calendar reminderCalendar24h = (Calendar) citaCalendar.clone();
        reminderCalendar24h.add(Calendar.DAY_OF_YEAR, -1);
        programarNotificacion(reminderCalendar24h, "Tienes una cita mañana a esta hora.");

        // Notificación 1 hora antes
        Calendar reminderCalendar1h = (Calendar) citaCalendar.clone();
        reminderCalendar1h.add(Calendar.HOUR_OF_DAY, -1);
        programarNotificacion(reminderCalendar1h, "Tienes una cita en 1 hora.");
    }

    private void programarNotificacion(Calendar reminderCalendar, String mensaje) {
        Intent intent = new Intent(CrearCita.this, ReminderReceiver.class);
        intent.putExtra("mensaje", mensaje);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(CrearCita.this, (int) reminderCalendar.getTimeInMillis(), intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, reminderCalendar.getTimeInMillis(), pendingIntent);

        Toast.makeText(CrearCita.this, "Notificación programada para " + reminderCalendar.getTime(), Toast.LENGTH_SHORT).show();
    }
}
