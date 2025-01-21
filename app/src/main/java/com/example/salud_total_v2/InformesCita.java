package com.example.salud_total_v2;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class InformesCita extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_informes_cita);

        // Obtener el ID de la cita desde el intent
        int idCita = getIntent().getIntExtra("id_cita", -1);

        // Aquí puedes cargar los datos de la cita usando el ID de la cita
    }
}
