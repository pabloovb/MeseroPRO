package com.example.meseropro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
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

        // 1) toolbar
        setupToolbar(R.id.toolbar);

        // 2) RecyclerView + Adapter
        rvPedidos = findViewById(R.id.recyclerPedidosActivos);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ActivePedidoAdapter(this, pedidosActivos);
        rvPedidos.setAdapter(adapter);

        // 3) Botón +
        fabAgregar = findViewById(R.id.fabAgregarPedido);
        fabAgregar.setOnClickListener(v -> mostrarDialogoMesa());

        // 4) ¡Carga al arrancar!
        cargarPedidosActivos();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // también recarga al volver de otra pantalla
        cargarPedidosActivos();
    }

    private void cargarPedidosActivos() {
        SupabaseService service = APIClient.getClient().create(SupabaseService.class);
        service.getPedidosPorEstado("eq.pendiente")
                .enqueue(new Callback<List<Pedido>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Pedido>> call,
                                           @NonNull Response<List<Pedido>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            pedidosActivos.clear();
                            pedidosActivos.addAll(resp.body());
                            Log.d(TAG, "Pedidos recibidos: " + pedidosActivos.size());
                            adapter.notifyDataSetChanged();
                            if (pedidosActivos.isEmpty()) {
                                Toast.makeText(ActivePedidosActivity.this,
                                        "No hay pedidos pendientes", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            String err;
                            try {
                                err = (resp.errorBody() != null)
                                        ? resp.errorBody().string()
                                        : "vacío";
                            } catch (IOException e) {
                                err = e.getMessage();
                            }
                            Log.e(TAG, "Error cargar pedidos: code="
                                    + resp.code() + " body=" + err);
                            Toast.makeText(ActivePedidosActivity.this,
                                    "Error al cargar pedidos ("+resp.code()+")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<Pedido>> call, @NonNull Throwable t) {
                        Log.e(TAG, "Fallo red al cargar pedidos", t);
                        Toast.makeText(ActivePedidosActivity.this,
                                "Fallo de red: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void mostrarDialogoMesa() {
        EditText inputMesa = new EditText(this);
        inputMesa.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("¿Número de mesa?")
                .setView(inputMesa)
                .setPositiveButton("Siguiente", (d, w) -> {
                    String s = inputMesa.getText().toString().trim();
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
        EditText inputCom = new EditText(this);
        inputCom.setInputType(InputType.TYPE_CLASS_NUMBER);
        new AlertDialog.Builder(this)
                .setTitle("¿Número de comensales?")
                .setView(inputCom)
                .setPositiveButton("Confirmar", (d, w) -> {
                    String s = inputCom.getText().toString().trim();
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
