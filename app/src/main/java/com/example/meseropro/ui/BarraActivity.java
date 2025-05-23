// src/main/java/com/example/meseropro/ui/BarraActivity.java
package com.example.meseropro.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meseropro.R;
import com.example.meseropro.adapter.PedidoBarraAdapter;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.SupabaseService;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BarraActivity extends BaseActivity {

    private RecyclerView rvPedidos;
    private PedidoBarraAdapter adapter;
    private List<Pedido> pedidos = new ArrayList<>();
    private SupabaseService svc;

    // Guarda en sesión qué bebidas ya se marcaron
    private Map<Integer, HashSet<String>> bebidasListas = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barra);

        setupToolbar(R.id.toolbarBarra);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        svc = APIClient.getClient().create(SupabaseService.class);

        rvPedidos = findViewById(R.id.rvPedidosBarra);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidoBarraAdapter(pedidos, bebidasListas, svc);
        rvPedidos.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabNuevoPedido);
        fab.setOnClickListener(v ->
                startActivity(new Intent(this, PedidoActivity.class))
        );

        cargarPedidosBarra();
    }

    private void cargarPedidosBarra() {
        svc.getPedidosPorEstado("eq.pendiente")
                .enqueue(new Callback<List<Pedido>>() {
                    @Override public void onResponse(
                            Call<List<Pedido>> call,
                            Response<List<Pedido>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            pedidos.clear();
                            // Ahora simplemente añadimos todos los pedidos pendientes:
                            pedidos.addAll(resp.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(BarraActivity.this,
                                    "Error cargar pedidos", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override public void onFailure(
                            Call<List<Pedido>> call,
                            Throwable t) {
                        Toast.makeText(BarraActivity.this,
                                "Fallo red: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_barra, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_notifications) {
            startActivity(new Intent(this, NotificacionesActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
