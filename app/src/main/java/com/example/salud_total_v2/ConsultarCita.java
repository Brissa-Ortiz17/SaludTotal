package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class ConsultarCita extends AppCompatActivity {

    private Spinner spinnerUsuario;
    private Spinner spinnerFiltroTipo;
    private Spinner spinnerFiltroDoctorEspecialidad;
    private Button btnConsultar, btnRegresar;
    private RecyclerView recyclerViewCitas;
    private CitasAdapter citasAdapter;
    private OkHttpClient client = new OkHttpClient();
    private String selectedFilter = "";
    private int idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private TextView tvBienvenido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consultar_cita);

        idUsuario = getIntent().getIntExtra("id_usuario", -1);
        nombreUsuario = getIntent().getStringExtra("nombre");
        apellidoUsuario = getIntent().getStringExtra("apellido");

        tvBienvenido = findViewById(R.id.tvBienvenido);
        spinnerUsuario = findViewById(R.id.spinnerUsuario);
        spinnerFiltroTipo = findViewById(R.id.spinnerFiltroTipo);
        spinnerFiltroDoctorEspecialidad = findViewById(R.id.spinnerFiltroDoctorEspecialidad);
        btnConsultar = findViewById(R.id.btnConsultar);
        btnRegresar = findViewById(R.id.btnRegresar);
        recyclerViewCitas = findViewById(R.id.recyclerViewCitas);

        // Mostrar mensaje de bienvenida
        String mensajeBienvenida = "Bienvenido " + nombreUsuario + " " + apellidoUsuario;
        tvBienvenido.setText(mensajeBienvenida);

        recyclerViewCitas.setLayoutManager(new LinearLayoutManager(this));
        citasAdapter = new CitasAdapter(new ArrayList<>());
        recyclerViewCitas.setAdapter(citasAdapter);

        // Definir las dos direcciones IP
        String url1 = "http://192.168.100.144:3000/usuarios";
        String url2 = "http://192.168.59.26:3000/usuarios"; // Nueva IP

        // Seleccionar la IP activa
        String selectedUrl = url1; // Cambia a url2 si deseas usar la nueva IP

        // Cargar los datos en el spinner
        loadSpinnerData(selectedUrl, spinnerUsuario, "id_usuario", "nombre_completo");


        // Configurar el Spinner de tipo de filtro
        ArrayAdapter<CharSequence> adapterFiltroTipo = ArrayAdapter.createFromResource(this,
                R.array.filtro_tipo_array, android.R.layout.simple_spinner_item);
        adapterFiltroTipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerFiltroTipo.setAdapter(adapterFiltroTipo);

        spinnerFiltroTipo.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String selected = parentView.getItemAtPosition(position).toString();

                // Definir las dos direcciones IP
                String ip1 = "http://192.168.100.144:3000";
                String ip2 = "http://192.168.59.26:3000"; // Nueva IP

                // Seleccionar la IP activa
                String selectedIp = ip1; // Cambia a ip2 si deseas usar la nueva IP

                switch (selected) {
                    case "Doctor":
                        selectedFilter = "doctor";
                        loadSpinnerData(selectedIp + "/doctores", spinnerFiltroDoctorEspecialidad, "id_doctor", "nombre");
                        break;
                    case "Especialidad":
                        selectedFilter = "especialidad";
                        loadSpinnerData(selectedIp + "/especialidades", spinnerFiltroDoctorEspecialidad, "id_especialidad", "nombre_especialidad");
                        break;
                    default:
                        selectedFilter = "";
                        spinnerFiltroDoctorEspecialidad.setAdapter(null);
                        break;
                }
            }


            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing here
            }
        });

        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedUsuarioId = getSelectedId(spinnerUsuario);
                if (selectedUsuarioId == -1) {
                    Toast.makeText(ConsultarCita.this, "Por favor, seleccione un usuario válido", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (selectedFilter.isEmpty() || spinnerFiltroTipo.getSelectedItem().toString().equals("Seleccionar filtro")) {
                    consultarCitas(selectedUsuarioId, -1, -1);
                } else {
                    int selectedFilterId = getSelectedId(spinnerFiltroDoctorEspecialidad);
                    if (selectedFilterId != -1) {
                        if (selectedFilter.equals("doctor")) {
                            consultarCitas(selectedUsuarioId, selectedFilterId, -1);
                        } else if (selectedFilter.equals("especialidad")) {
                            consultarCitas(selectedUsuarioId, -1, selectedFilterId);
                        }
                    } else {
                        Toast.makeText(ConsultarCita.this, "Por favor, seleccione una opción válida para el filtro", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ConsultarCita.this, CitasMedicas.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
                finish();
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
                runOnUiThread(() -> Toast.makeText(ConsultarCita.this, "Error al cargar los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ConsultarCita.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show());
                    return;
                }

                String responseBody = response.body().string();
                Log.d("ConsultarCita", "Respuesta del servidor: " + responseBody);

                final List<String> items = new ArrayList<>();
                final List<Integer> ids = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(responseBody);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        int id = jsonObject.getInt(idKey);
                        String name = jsonObject.getString(nameKey);
                        ids.add(id);
                        items.add(name);
                    }
                } catch (JSONException e) {
                    runOnUiThread(() -> Toast.makeText(ConsultarCita.this, "Error al parsear los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }

                runOnUiThread(() -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(ConsultarCita.this, android.R.layout.simple_spinner_item, items);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinner.setAdapter(adapter);
                    spinner.setTag(ids);
                    if (spinner == spinnerUsuario) {
                        setDefaultUser();
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

    private void setDefaultUser() {
        List<Integer> ids = (List<Integer>) spinnerUsuario.getTag();
        if (ids != null) {
            int position = ids.indexOf(idUsuario);
            if (position != -1) {
                spinnerUsuario.setSelection(position);
            }
        }
    }

    private void consultarCitas(int idUsuario, int idDoctor, int idEspecialidad) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");

        JSONObject consultaData = new JSONObject();
        try {
            if (idUsuario != -1) consultaData.put("id_usuario", idUsuario);
            if (idDoctor != -1) consultaData.put("id_doctor", idDoctor);
            if (idEspecialidad != -1) consultaData.put("id_especialidad", idEspecialidad);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // Definir las dos direcciones IP
        String ip1 = "http://192.168.100.144:3000";
        String ip2 = "http://192.168.59.26:3000"; // Nueva IP

        // Seleccionar la IP activa
                String selectedIp = ip1; // Cambia a ip2 si deseas usar la nueva IP

        // Construir el cuerpo de la solicitud
                RequestBody body = RequestBody.create(consultaData.toString(), JSON);

        // Construir la solicitud
        Request request = new Request.Builder()
                .url(selectedIp + "/consultarCitas")
                .post(body)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(ConsultarCita.this, "Error al consultar las citas: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(ConsultarCita.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show());
                    return;
                }

                final List<Cita> citas = new ArrayList<>();
                try {
                    JSONArray jsonArray = new JSONArray(response.body().string());
                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
                    SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        // Formatear la fecha
                        String fechaCitaOriginal = jsonObject.getString("fecha_cita");
                        Date date = inputFormat.parse(fechaCitaOriginal);
                        String fechaCitaFormateada = outputFormat.format(date);

                        Cita cita = new Cita(
                                jsonObject.getInt("id_cita"),
                                jsonObject.getInt("id_usuario"),
                                jsonObject.getInt("id_doctor"),
                                fechaCitaFormateada, // Fecha formateada
                                jsonObject.getString("hora_cita"),
                                jsonObject.getInt("id_especialidad"),
                                jsonObject.getString("ubicacion"),
                                jsonObject.getString("nota")
                        );
                        citas.add(cita);
                    }
                } catch (JSONException | ParseException e) {
                    runOnUiThread(() -> Toast.makeText(ConsultarCita.this, "Error al parsear los datos: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    e.printStackTrace();
                }

                runOnUiThread(() -> citasAdapter.updateData(citas));
            }
        });
    }



}
