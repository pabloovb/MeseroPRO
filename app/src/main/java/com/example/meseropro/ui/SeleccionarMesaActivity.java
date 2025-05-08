package com.example.meseropro.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.gridlayout.widget.GridLayout;

import com.example.meseropro.R;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.SupabaseService;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SeleccionarMesaActivity extends AppCompatActivity {

    private GridLayout layoutMesas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seleccionar_mesa);

        layoutMesas = findViewById(R.id.layoutMesas);

        // Crear botones de mesas dinámicamente
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

            btn.setOnClickListener(v -> {
                int numeroMesa = (int) v.getTag();
                verificarPedidoActivo(numeroMesa);
            });
        }
    }

    private void verificarPedidoActivo(int mesaId) {
        SupabaseService service = APIClient.getClient().create(SupabaseService.class);
        Call<List<Pedido>> call = service.getPedidosPorMesa(mesaId, "activo");

        call.enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> response) {
                if (response.isSuccessful() && response.body() != null && !response.body().isEmpty()) {
                    // Ya hay un pedido activo
                    lanzarPedidoActivity(mesaId, -1); // -1 indica que no hace falta preguntar comensales
                } else {
                    mostrarPopupComensales(mesaId);
                }
            }

            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                Toast.makeText(SeleccionarMesaActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarPopupComensales(int mesaId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("¿Cuántos comensales?");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        input.setHint("Ej: 4");
        builder.setView(input);

        builder.setPositiveButton("Aceptar", (dialog, which) -> {
            String texto = input.getText().toString().trim();
            if (!texto.isEmpty()) {
                int comensales = Integer.parseInt(texto);
                lanzarPedidoActivity(mesaId, comensales);
            } else {
                Toast.makeText(this, "Introduce un número válido", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void lanzarPedidoActivity(int mesaId, int comensales) {
        Intent intent = new Intent(SeleccionarMesaActivity.this, PedidoActivity.class);
        intent.putExtra("mesa", mesaId);
        intent.putExtra("comensales", comensales);
        startActivity(intent);
    }
}
