package com.example.meseropro.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.gridlayout.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meseropro.R;

public class SeleccionarMesaActivity extends AppCompatActivity {

    private GridLayout layoutMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_mesa);

        layoutMesas = findViewById(R.id.layoutMesas);

        // Creamos botones de mesa dinámicamente (1 a 20 por ejemplo)
        for (int i = 1; i <= 20; i++) {
            Button btn = new Button(this);
            btn.setText("Mesa " + i);
            btn.setTag(i);
            btn.setTextSize(16);
            btn.setAllCaps(false);
            btn.setBackgroundColor(Color.parseColor("#CC5E12"));
            btn.setTextColor(getResources().getColor(android.R.color.white));

            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(8, 8, 8, 8);
            params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1f);
            params.width = 0;
            layoutMesas.addView(btn, params);

            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int numeroMesa = (int) v.getTag();
                    Intent intent = new Intent(SeleccionarMesaActivity.this, PedidoActivity.class);
                    intent.putExtra("mesa", numeroMesa);
                    startActivity(intent);
                }
            });
        }
    }
}

