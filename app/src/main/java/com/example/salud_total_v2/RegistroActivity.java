package com.example.salud_total_v2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RegistroActivity extends AppCompatActivity {

    private EditText etNombre, etApellido, etFechaNacimiento, etEmail, etContrasena, etTelefono, etDireccion, etUsuario;
    private Spinner spinnerTipoUsuario;
    private Button btnRegistrar, btnRegresarLogin;
    private OkHttpClient client = new OkHttpClient();
    private String tipoUsuario;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        etNombre = findViewById(R.id.etNombre);
        etApellido = findViewById(R.id.etApellido);
        etFechaNacimiento = findViewById(R.id.etFechaNacimiento);
        etEmail = findViewById(R.id.etEmail);
        etContrasena = findViewById(R.id.etContrasena);
        etTelefono = findViewById(R.id.etTelefono);
        etDireccion = findViewById(R.id.etDireccion);
        etUsuario = findViewById(R.id.etUsuario);
        spinnerTipoUsuario = findViewById(R.id.spinnerTipoUsuario);
        btnRegistrar = findViewById(R.id.btnRegistrar);
        btnRegresarLogin = findViewById(R.id.btnRegresarLogin);

        // Configurar el spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.tipo_usuario_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerTipoUsuario.setAdapter(adapter);
        spinnerTipoUsuario.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                tipoUsuario = parentView.getItemAtPosition(position).toString().toLowerCase();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Do nothing
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nombre = etNombre.getText().toString();
                String apellido = etApellido.getText().toString();
                String fechaNacimiento = etFechaNacimiento.getText().toString();
                String email = etEmail.getText().toString();
                String contrasena = etContrasena.getText().toString();
                String telefono = etTelefono.getText().toString();
                String direccion = etDireccion.getText().toString();
                String usuario = etUsuario.getText().toString();

                registrarUsuario(nombre, apellido, fechaNacimiento, email, contrasena, telefono, direccion, usuario, tipoUsuario);
            }
        });

        btnRegresarLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de inicio de sesión
                Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void registrarUsuario(String nombre, String apellido, String fechaNacimiento, String email, String contrasena, String telefono, String direccion, String usuario, String tipoUsuario) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject userData = new JSONObject();
        try {
            userData.put("nombre", nombre);
            userData.put("apellido", apellido);
            userData.put("fecha_nacimiento", fechaNacimiento);
            userData.put("email", email);
            userData.put("contrasena", contrasena);
            userData.put("telefono", telefono);
            userData.put("direccion", direccion);
            userData.put("usuario", usuario);
            userData.put("tipo_usuario", tipoUsuario);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(userData.toString(), JSON);
        Request request = new Request.Builder()
                .url("http://10.0.2.2:3000/register")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(RegistroActivity.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegistroActivity.this, "Error al registrar usuario", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(RegistroActivity.this, "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                            // Redirigir a la actividad de inicio de sesión
                            Intent intent = new Intent(RegistroActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });
                }
            }
        });
    }
}
