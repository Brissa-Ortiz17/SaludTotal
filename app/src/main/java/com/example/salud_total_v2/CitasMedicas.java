package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class CitasMedicas extends AppCompatActivity {

    private Button btnCrearCita, btnConsultarCita, btnRegresar;
    private TextView tvBienvenido, tvTitulo;
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

        setContentView(R.layout.activity_citas_medicas);

        btnCrearCita = findViewById(R.id.btnCrearCita);
        btnConsultarCita = findViewById(R.id.btnConsultarCita);
        btnRegresar = findViewById(R.id.btnRegresar);
        tvBienvenido = findViewById(R.id.tvBienvenido);
        tvTitulo = findViewById(R.id.tvTitulo);

        // Mostrar mensaje de bienvenida
        String mensajeBienvenida = "Bienvenido " + nombreUsuario + " " + apellidoUsuario;
        tvBienvenido.setText(mensajeBienvenida);

        btnCrearCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CitasMedicas.this, CrearCita.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
            }
        });


        btnConsultarCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CitasMedicas.this, ConsultarCita.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
            }
        });

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CitasMedicas.this, MainActivity.class);
                intent.putExtra("id_usuario", idUsuario);
                intent.putExtra("nombre", nombreUsuario);
                intent.putExtra("apellido", apellidoUsuario);
                startActivity(intent);
                finish();
            }
        });
    }
}
