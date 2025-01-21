package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {

    private EditText etUsuario, etContrasena;
    private Button btnIngresar, btnRegistrar;
    private OkHttpClient client = new OkHttpClient();
    private static final String LOGIN_URL = "http://10.0.2.2:3000/authenticate";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etUsuario = findViewById(R.id.etUsuario);
        etContrasena = findViewById(R.id.etContrasena);
        btnIngresar = findViewById(R.id.btnLogin);
        btnRegistrar = findViewById(R.id.btnRegistro);

        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String usuario = etUsuario.getText().toString();
                String contrasena = etContrasena.getText().toString();
                autenticarUsuario(usuario, contrasena);
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Redirigir a la actividad de registro
                Intent intent = new Intent(LoginActivity.this, RegistroActivity.class);
                startActivity(intent);
            }
        });
    }

    private void autenticarUsuario(String usuario, String contrasena) {
        MediaType JSON = MediaType.get("application/json; charset=utf-8");
        JSONObject authData = new JSONObject();
        try {
            authData.put("usuario", usuario);
            authData.put("contrasena", contrasena);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(authData.toString(), JSON);
        Request request = new Request.Builder()
                .url(LOGIN_URL)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(LoginActivity.this, "Error de conexión: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(LoginActivity.this, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    try {
                        JSONObject jsonResponse = new JSONObject(response.body().string());
                        int idUsuario = jsonResponse.getInt("id_usuario");
                        String nombre = jsonResponse.getString("nombre");
                        String apellido = jsonResponse.getString("apellido");
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("id_usuario", idUsuario);
                        intent.putExtra("nombre", nombre);
                        intent.putExtra("apellido", apellido);
                        startActivity(intent);
                        finish();
                    } catch (JSONException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Error al parsear la respuesta: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }
}
