package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConsultarRecordatorio extends AppCompatActivity {

    private Spinner spinnerUsuario;
    private TextView tvBienvenido, tvResultados;
    private Button btnBuscar, btnRegresar, btnVerAnteriores;
    private int idUsuario;
    private String nombreUsuario, apellidoUsuario;
    private OkHttpClient client;
    private ArrayList<String> usuariosList = new ArrayList<>();
    private HashMap<String, Integer> usuariosMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_recordatorio);

        client = new OkHttpClient();

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        nombreUsuario = getIntent().getStringExtra("nombre");
        apellidoUsuario = getIntent().getStringExtra("apellido");

        tvBienvenido = findViewById(R.id.tvBienvenido);
        spinnerUsuario = findViewById(R.id.spinnerUsuario);
        btnBuscar = findViewById(R.id.btnBuscar);
        btnVerAnteriores = findViewById(R.id.btnVerAnteriores);
        tvResultados = findViewById(R.id.tvResultados);
        btnRegresar = findViewById(R.id.btnRegresar);

        tvBienvenido.setText("Bienvenido " + nombreUsuario + " " + apellidoUsuario);

        cargarUsuarios();  // Cargar la lista de usuarios al inicio

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarRecordatorios();
            }
        });
        btnVerAnteriores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buscarRecordatoriosAnteriores();
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad principal
                Intent mainIntent = new Intent(ConsultarRecordatorio.this, MainActivity.class);
                mainIntent.putExtra("id_usuario", idUsuario);
                mainIntent.putExtra("nombre", nombreUsuario);
                mainIntent.putExtra("apellido", apellidoUsuario);
                startActivity(mainIntent);
                finish();
            }
        });
    }

    private void cargarUsuarios() {
        // Cargar los usuarios desde el servidor
        String url = "http://10.0.2.2:3000/usuarios"; // Endpoint correcto
        Request request = new Request.Builder().url(url).build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error al cargar usuarios", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    Log.d("ConsultarRecordatorio", "Usuarios Response: " + responseBody);
                    JSONArray jsonArray = new JSONArray(responseBody);
                    usuariosList.clear();
                    usuariosMap.clear();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject usuario = jsonArray.getJSONObject(i);
                        String nombreCompleto = usuario.getString("nombre_completo"); // Asegúrate de que el JSON contiene 'nombre_completo'
                        int id = usuario.getInt("id_usuario");
                        usuariosList.add(nombreCompleto);
                        usuariosMap.put(nombreCompleto, id);
                    }

                    runOnUiThread(() -> {
                        if (usuariosList.isEmpty()) {
                            Toast.makeText(ConsultarRecordatorio.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                        } else {
                            ArrayAdapter<String> adapter = new ArrayAdapter<>(ConsultarRecordatorio.this, android.R.layout.simple_spinner_item, usuariosList);
                            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spinnerUsuario.setAdapter(adapter);

                            // Seleccionar el usuario logueado por defecto
                            String usuarioLogueado = nombreUsuario + " " + apellidoUsuario;
                            int position = adapter.getPosition(usuarioLogueado);
                            if (position >= 0) {
                                spinnerUsuario.setSelection(position);
                            }
                        }
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error al procesar los datos del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

    private void buscarRecordatorios() {
        int idUsuarioSeleccionado = usuariosMap.containsKey(spinnerUsuario.getSelectedItem().toString()) ? usuariosMap.get(spinnerUsuario.getSelectedItem().toString()) : idUsuario;

        JSONObject postData = new JSONObject();
        try {
            postData.put("id_usuario", idUsuarioSeleccionado);
            postData.put("filtro", ""); // No se utiliza filtro
            postData.put("id_filtro", -1); // No se utiliza id_filtro
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(postData.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/consultarRecordatorios")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    Log.d("ConsultarRecordatorio", "Recordatorios Response: " + responseBody);
                    JSONArray jsonArray = new JSONArray(responseBody);
                    StringBuilder resultados = new StringBuilder();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recordatorio = jsonArray.getJSONObject(i);
                        String fechaInicio = formatDate(recordatorio.getString("fecha_inicio")); // Formatea la fecha
                        String fechaFin = formatDate(recordatorio.getString("fecha_fin")); // Formatea la fecha

                        // Solo mostrar los recordatorios cuya fecha de fin sea hoy o en el futuro
                        if (fechaFin.compareTo(getTodayDate()) >= 0) {
                            resultados.append("Nombre: ").append(recordatorio.getString("nombre")).append("\n");
                            resultados.append("Duración: ").append(recordatorio.getString("duracion")).append("\n");
                            resultados.append("Hora de Inicio: ").append(recordatorio.getString("hora_inicio")).append("\n");
                            resultados.append("Frecuencia: ").append(recordatorio.getString("frecuencia")).append("\n");
                            resultados.append("Fecha de Inicio: ").append(fechaInicio).append("\n");
                            resultados.append("Fecha de Fin: ").append(fechaFin).append("\n");
                            resultados.append("Notas: ").append(recordatorio.getString("nota")).append("\n\n");
                        }
                    }

                    final String resultadoTexto = resultados.toString();
                    runOnUiThread(() -> {
                        tvResultados.setVisibility(View.VISIBLE);
                        tvResultados.setText(resultadoTexto);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error al procesar los datos del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    private void buscarRecordatoriosAnteriores() {
        int idUsuarioSeleccionado = usuariosMap.containsKey(spinnerUsuario.getSelectedItem().toString()) ? usuariosMap.get(spinnerUsuario.getSelectedItem().toString()) : idUsuario;

        JSONObject postData = new JSONObject();
        try {
            postData.put("id_usuario", idUsuarioSeleccionado);
            postData.put("filtro", ""); // No se utiliza filtro
            postData.put("id_filtro", -1); // No se utiliza id_filtro
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        RequestBody body = RequestBody.create(postData.toString(), okhttp3.MediaType.parse("application/json; charset=utf-8"));
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/consultarRecordatoriosAnteriores")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error al conectar con el servidor", Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    Log.d("ConsultarRecordatorio", "Recordatorios Anteriores Response: " + responseBody);
                    JSONArray jsonArray = new JSONArray(responseBody);
                    StringBuilder resultados = new StringBuilder();

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject recordatorio = jsonArray.getJSONObject(i);
                        String fechaInicio = formatDate(recordatorio.getString("fecha_inicio")); // Formatea la fecha
                        String fechaFin = formatDate(recordatorio.getString("fecha_fin")); // Formatea la fecha

                        // Mostrar todos los recordatorios cuya fecha de fin sea antes de hoy
                        if (fechaFin.compareTo(getTodayDate()) < 0) {
                            resultados.append("Nombre: ").append(recordatorio.getString("nombre")).append("\n");
                            resultados.append("Duración: ").append(recordatorio.getString("duracion")).append("\n");
                            resultados.append("Hora de Inicio: ").append(recordatorio.getString("hora_inicio")).append("\n");
                            resultados.append("Frecuencia: ").append(recordatorio.getString("frecuencia")).append("\n");
                            resultados.append("Fecha de Inicio: ").append(fechaInicio).append("\n");
                            resultados.append("Fecha de Fin: ").append(fechaFin).append("\n");
                            resultados.append("Notas: ").append(recordatorio.getString("nota")).append("\n\n");
                        }
                    }

                    final String resultadoTexto = resultados.toString();
                    runOnUiThread(() -> {
                        tvResultados.setVisibility(View.VISIBLE);
                        tvResultados.setText(resultadoTexto);
                    });

                } catch (JSONException e) {
                    e.printStackTrace();
                    runOnUiThread(() -> Toast.makeText(ConsultarRecordatorio.this, "Error al procesar los datos del servidor", Toast.LENGTH_SHORT).show());
                }
            }
        });
    }
    private String formatDate(String dateStr) {
        SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            return targetFormat.format(originalFormat.parse(dateStr));
        } catch (ParseException e) {
            e.printStackTrace();
            return dateStr; // Retorna la fecha sin formato si ocurre un error
        }
    }
    private String getTodayDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = new java.util.Date();
        return sdf.format(date);
    }
}
