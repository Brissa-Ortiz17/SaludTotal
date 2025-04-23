package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
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

public class MainActivity extends AppCompatActivity {

    private Button btnCitasMedicas, btnRecordatorios, btnCalendario;
    private TextView tvBienvenido, tvProximaCita, tvDetalleProximaCita;
    private int idUsuario;
    private String nombreUsuario;
    private String apellidoUsuario;
    private OkHttpClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        idUsuario = intent.getIntExtra("id_usuario", -1);
        nombreUsuario = intent.getStringExtra("nombre");
        apellidoUsuario = intent.getStringExtra("apellido");

        if (!isAuthenticated()) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
            return;
        }

        setContentView(R.layout.activity_main);

        client = new OkHttpClient();

        btnCitasMedicas = findViewById(R.id.btnCitasMedicas);
        btnRecordatorios = findViewById(R.id.btnRecordatorios);
        btnCalendario = findViewById(R.id.btnCalendario);
        tvBienvenido = findViewById(R.id.tvBienvenido);
        tvProximaCita = findViewById(R.id.tvProximaCita);
        tvDetalleProximaCita = findViewById(R.id.tvDetalleProximaCita);

        String mensajeBienvenida = "Bienvenido " + nombreUsuario + " " + apellidoUsuario;
        tvBienvenido.setText(mensajeBienvenida);

        btnCitasMedicas.setOnClickListener(view -> {
            Intent intent1 = new Intent(MainActivity.this, CitasMedicas.class);
            intent1.putExtra("id_usuario", idUsuario);
            intent1.putExtra("nombre", nombreUsuario);
            intent1.putExtra("apellido", apellidoUsuario);
            startActivity(intent1);
        });

        btnRecordatorios.setOnClickListener(view -> {
            Intent intent12 = new Intent(MainActivity.this, Recordatorios.class);
            intent12.putExtra("id_usuario", idUsuario);
            intent12.putExtra("nombre", nombreUsuario);
            intent12.putExtra("apellido", apellidoUsuario);
            startActivity(intent12);
        });

        btnCalendario.setOnClickListener(view -> {
            Intent intent13 = new Intent(MainActivity.this, CalendarioActivity.class);
            intent13.putExtra("id_usuario", idUsuario);
            intent13.putExtra("nombre", nombreUsuario);
            intent13.putExtra("apellido", apellidoUsuario);
            startActivity(intent13);
        });

        Button btnLogout = findViewById(R.id.btn_logout); // Usa el mismo ID que en el XML
            btnLogout.setOnClickListener(view -> {
                Intent logoutIntent = new Intent(MainActivity.this, LoginActivity.class);
                logoutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(logoutIntent);
                finish();
        });



        // Cargar la cita más próxima
        cargarProximaCita();
    }

    private boolean isAuthenticated() {
        return idUsuario != -1;
    }

    // Definir las dos direcciones IP
    String ip1 = "http://192.168.100.144:3000";
    String ip2 = "http://192.168.59.26:3000"; // Nueva IP

    // Seleccionar la IP activa (esto se puede cambiar dinámicamente según sea necesario)
    String selectedIp = ip1; // Cambia a ip2 si deseas usar la nueva IP

    // Cargar la URL con la IP seleccionada
    private void cargarProximaCita() {
        String url = selectedIp + "/getProximaCita?id_usuario=" + idUsuario;

        Request request = new Request.Builder()
                .url(url)
                .build();


        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al cargar la próxima cita: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error en la respuesta del servidor: " + response.message(), Toast.LENGTH_SHORT).show());
                    return;
                }

                try {
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String fechaCita = jsonObject.getString("fecha_cita").split("T")[0];
                    String horaCita = jsonObject.getString("hora_cita").substring(0, 5); // Solo hora y minutos
                    String detalles = "Fecha: " + fechaCita + "\nHora: " + horaCita;

                    runOnUiThread(() -> tvDetalleProximaCita.setText(detalles));
                } catch (JSONException e) {
                    runOnUiThread(() -> Toast.makeText(MainActivity.this, "Error al parsear los datos de la próxima cita: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                }
            }
        });
    }

}
