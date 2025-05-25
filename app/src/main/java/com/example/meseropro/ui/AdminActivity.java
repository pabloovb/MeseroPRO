// src/main/java/com/example/meseropro/ui/AdminActivity.java
package com.example.meseropro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.meseropro.R;

public class AdminActivity extends AppCompatActivity {

    private Button btnGestionProductos;
    private Button btnGestionUsuarios;
    private Button btnModoCamarero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        // 1) Botón Gestión de Productos
        btnGestionProductos = findViewById(R.id.btnGestionProductos);
        btnGestionProductos.setOnClickListener(v ->
                startActivity(new Intent(this, GestionProductosActivity.class))
        );

        // 2) Botón Gestión de Usuarios (solo info)
        btnGestionUsuarios = findViewById(R.id.btnGestionUsuarios);
        btnGestionUsuarios.setOnClickListener(v ->
                new AlertDialog.Builder(this)
                        .setTitle("Crear usuarios")
                        .setMessage(
                                "La creación de usuarios la gestiona el equipo de soporte de MeseroPRO.\n\n" +
                                        "Escríbenos a soporte@meseropro.com y en menos de 1 hora te activamos la cuenta."
                        )
                        .setPositiveButton("Entendido", null)
                        .show()
        );

        // 3) Botón Modo Camarero
        btnModoCamarero = findViewById(R.id.btnModoCamarero);
        btnModoCamarero.setOnClickListener(v ->
                startActivity(new Intent(this, ActivePedidosActivity.class))
        );
    }
}
