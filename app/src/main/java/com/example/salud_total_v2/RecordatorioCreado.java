package com.example.salud_total_v2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;


public class RecordatorioCreado extends AppCompatActivity {

    private Button btnRegresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recordatorio_creado);

        btnRegresar = findViewById(R.id.btnRegresar);

        btnRegresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RecordatorioCreado.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}
