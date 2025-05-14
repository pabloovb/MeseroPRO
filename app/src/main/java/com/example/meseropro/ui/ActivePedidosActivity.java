// src/main/java/com/example/meseropro/ui/ActivePedidosActivity.java
package com.example.meseropro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meseropro.R;
import com.example.meseropro.adapter.ActivePedidoAdapter;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.SupabaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ActivePedidosActivity extends BaseActivity {

    private static final String TAG = "ActivePedidosActivity";
    private RecyclerView rvPedidos;
    private FloatingActionButton fabAgregar;
    private List<Pedido> pedidosActivos = new ArrayList<>();
    private ActivePedidoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_pedidos);

        // Configura toolbar + menú
        setupToolbar(R.id.toolbar);

        rvPedidos = findViewById(R.id.recyclerPedidosActivos);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActivePedidoAdapter(this, pedidosActivos);
        rvPedidos.setAdapter(adapter);

        fabAgregar = findViewById(R.id.fabAgregarPedido);
        fabAgregar.setOnClickListener(v -> mostrarDialogoMesa());
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarPedidosActivos();
    }

    private void cargarPedidosActivos() {
        SupabaseService service = APIClient.getClient().create(SupabaseService.class);
        service.getPedidosPorEstado("eq.pendiente")
                .enqueue(new Callback<List<Pedido>>() {
                    @Override
                    public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            pedidosActivos.clear();
                            pedidosActivos.addAll(resp.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            try {
                                String err = resp.errorBody() != null
                                        ? resp.errorBody().string() : "empty";
                                Log.e(TAG, "Error cargar pedidos: code="
                                        + resp.code() + " body=" + err);
                            } catch (IOException e) {
                                Log.e(TAG, "Error leyendo errorBody", e);
                            }
                            Toast.makeText(ActivePedidosActivity.this,
                                    "Error al cargar pedidos (" + resp.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Pedido>> call, Throwable t) {
                        Log.e(TAG, "Fallo de red", t);
                        Toast.makeText(ActivePedidosActivity.this,
                                "Fallo de red: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoMesa() {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("¿Número de mesa?")
                .setView(input)
                .setPositiveButton("Siguiente", (d, w) -> {
                    String s = input.getText().toString().trim();
                    if (s.isEmpty()) {
                        Toast.makeText(this, "Introduce un número válido",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    mostrarDialogoComensales(Integer.parseInt(s));
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void mostrarDialogoComensales(int mesa) {
        EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("¿Número de comensales?")
                .setView(input)
                .setPositiveButton("Confirmar", (d, w) -> {
                    String s = input.getText().toString().trim();
                    if (s.isEmpty()) {
                        Toast.makeText(this, "Introduce un número válido",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    Intent it = new Intent(this, PedidoActivity.class);
                    it.putExtra("mesa", mesa);
                    it.putExtra("comensales", Integer.parseInt(s));
                    startActivity(it);
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }
}
