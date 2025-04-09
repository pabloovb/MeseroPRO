package com.example.meseropro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meseropro.R;

public class CamareroActivity extends AppCompatActivity {

    Button btnMesa1, btnMesa2, btnMesa3, btnMesa4, btnMesa5, btnMesa6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camarero);

        btnMesa1 = findViewById(R.id.btnMesa1);
        btnMesa2 = findViewById(R.id.btnMesa2);
        btnMesa3 = findViewById(R.id.btnMesa3);
        btnMesa4 = findViewById(R.id.btnMesa4);
        btnMesa5 = findViewById(R.id.btnMesa5);
        btnMesa6 = findViewById(R.id.btnMesa6);

        View.OnClickListener mesaClickListener = v -> {
            String mesa = ((Button) v).getText().toString();
            Intent intent = new Intent(CamareroActivity.this, PedidoActivity.class);
            intent.putExtra("mesa", mesa);
            startActivity(intent);
        };

        btnMesa1.setOnClickListener(mesaClickListener);
        btnMesa2.setOnClickListener(mesaClickListener);
        btnMesa3.setOnClickListener(mesaClickListener);
        btnMesa4.setOnClickListener(mesaClickListener);
        btnMesa5.setOnClickListener(mesaClickListener);
        btnMesa6.setOnClickListener(mesaClickListener);
    }
}



