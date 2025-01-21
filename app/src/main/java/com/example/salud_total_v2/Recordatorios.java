package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class Recordatorios extends AppCompatActivity {
    private TextView tvBienvenido;
    private String nombreUsuario;
    private String apellidoUsuario;
    private Button btnCrearRecordatorio, btnConsultarRecordatorio, btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorios);
        Intent intent = getIntent();
        nombreUsuario = intent.getStringExtra("nombre");
        apellidoUsuario = intent.getStringExtra("apellido");

        tvBienvenido = findViewById(R.id.tvBienvenido);

        // Mostrar mensaje de bienvenida
        String mensajeBienvenida = "Bienvenido " + nombreUsuario + " " + apellidoUsuario;
        tvBienvenido.setText(mensajeBienvenida);
        btnCrearRecordatorio = findViewById(R.id.btnCrearRecordatorio);
        btnConsultarRecordatorio = findViewById(R.id.btnConsultarRecordatorio);
        btnRegresar = findViewById(R.id.btnRegresar);

        btnCrearRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Recordatorios.this, CrearRecordatorio.class);
                intent.putExtra("id_usuario", getIntent().getIntExtra("id_usuario", -1));
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
            }
        });

        btnConsultarRecordatorio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Recordatorios.this, ConsultarRecordatorio.class);
                intent.putExtra("id_usuario", getIntent().getIntExtra("id_usuario", -1));
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Recordatorios.this, MainActivity.class);
                intent.putExtra("id_usuario", getIntent().getIntExtra("id_usuario", -1));
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
            }
        });
    }
}
