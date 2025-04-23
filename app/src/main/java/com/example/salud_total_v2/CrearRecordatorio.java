package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;

import androidx.appcompat.app.AppCompatActivity;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class CrearRecordatorio extends AppCompatActivity {

    private EditText etNombreMedicamento, etDuracion, etHoraInicio, etFrecuencia, etNota;
    private Spinner spinnerDoctor, spinnerEspecialidad;
    private Button btnCrearRecordatorio, btnRegresar;
    private int idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private OkHttpClient client;
    private HashMap<String, Integer> doctoresMap = new HashMap<>();
    private HashMap<String, Integer> especialidadesMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crear_recordatorio);

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        nombreUsuario = getIntent().getStringExtra("nombre");
        apellidoUsuario = getIntent().getStringExtra("apellido");

        // Verificar si el usuario está autenticado
        if (idUsuario == -1) {
            // Redirigir a la actividad de inicio de sesión si no está autenticado
            Intent loginIntent = new Intent(CrearRecordatorio.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        // Configurar los elementos de la vista
        TextView tvBienvenido = findViewById(R.id.tvBienvenido);
        tvBienvenido.setText("Bienvenido " + nombreUsuario + " " + apellidoUsuario);

        etNombreMedicamento = findViewById(R.id.etNombreMedicamento);
        etDuracion = findViewById(R.id.etDuracion);
        etHoraInicio = findViewById(R.id.etHoraInicio);
        etFrecuencia = findViewById(R.id.etFrecuencia);
        etNota = findViewById(R.id.etNota);
        btnCrearRecordatorio = findViewById(R.id.btnCrearRecordatorio);
        btnRegresar = findViewById(R.id.btnRegresar);

        client = new OkHttpClient();

        btnCrearRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                crearRecordatorio();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Redirigir a la actividad principal
                Intent mainIntent = new Intent(CrearRecordatorio.this, MainActivity.class);
                mainIntent.putExtra("id_usuario", idUsuario);
                mainIntent.putExtra("nombre", nombreUsuario);
                mainIntent.putExtra("apellido", apellidoUsuario);
                startActivity(mainIntent);
                finish();
            }
        });
    }
    private void crearRecordatorio() {
        String nombre = etNombreMedicamento.getText().toString().trim();
        int duracion = Integer.parseInt(etDuracion.getText().toString().trim());
        String horaInicio = etHoraInicio.getText().toString().trim();
        int frecuencia = Integer.parseInt(etFrecuencia.getText().toString().trim());
        String nota = etNota.getText().toString().trim();

        // Calcular fecha de inicio y fecha de fin
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String fechaInicio = sdf.format(new Date());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR, duracion);
        String fechaFin = sdf.format(calendar.getTime());

        JSONObject recordatorioData = new JSONObject();
        try {
            recordatorioData.put("id_usuario", idUsuario);
            recordatorioData.put("nombre", nombre);
            recordatorioData.put("duracion", duracion);
            recordatorioData.put("hora_inicio", horaInicio);
            recordatorioData.put("frecuencia", frecuencia);
            recordatorioData.put("nota", nota);
            recordatorioData.put("fecha_inicio", fechaInicio);
            recordatorioData.put("fecha_fin", fechaFin);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(CrearRecordatorio.this, "Error al crear los datos del recordatorio", Toast.LENGTH_SHORT).show();
            return;
        }

        // Definir las dos direcciones IP
        String ip1 = "http://192.168.100.144:3000";
        String ip2 = "http://192.168.59.26:3000"; // Nueva IP

        // Seleccionar la IP activa
        String selectedIp = ip1; // Cambia a ip2 si deseas usar la nueva IP

        // Construir el cuerpo de la solicitud
        RequestBody body = RequestBody.create(recordatorioData.toString(), MediaType.parse("application/json; charset=utf-8"));

        // Construir la solicitud
        Request request = new Request.Builder()
                .url(selectedIp + "/createRecordatorio")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CrearRecordatorio.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(CrearRecordatorio.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show();
                        }
                    });
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CrearRecordatorio.this, "Recordatorio creado exitosamente", Toast.LENGTH_SHORT).show();
                        // Redirigir a la actividad principal
                        Intent mainIntent = new Intent(CrearRecordatorio.this, MainActivity.class);
                        mainIntent.putExtra("id_usuario", idUsuario);
                        mainIntent.putExtra("nombre", nombreUsuario);
                        mainIntent.putExtra("apellido", apellidoUsuario);
                        startActivity(mainIntent);
                        finish();
                    }
                });
            }
        });
    }
}
