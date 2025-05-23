// src/main/java/com/example/meseropro/ui/CocinaActivity.java
package com.example.meseropro.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meseropro.R;
import com.example.meseropro.model.LineaPedido;
import com.example.meseropro.model.Notificacion;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.SupabaseService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CocinaActivity extends BaseActivity {

    private RecyclerView rvPedidos;
    private final List<Pedido> pedidos = new ArrayList<>();
    private PedidoCocinaAdapter adapter;
    private SupabaseService svc;

    // Mapa: pedidoId → conjunto de nombres de platos marcados como “listo”
    private final Map<Integer, HashSet<String>> lineasListas = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cocina);
        setupToolbar(R.id.toolbar);

        svc = APIClient.getClient().create(SupabaseService.class);

        rvPedidos = findViewById(R.id.recyclerPedidosCocina);
        rvPedidos.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PedidoCocinaAdapter(pedidos);
        rvPedidos.setAdapter(adapter);

        cargarPedidosPendientes();
    }

    private void cargarPedidosPendientes() {
        svc.getPedidosPorEstado("eq.pendiente")
                .enqueue(new Callback<List<Pedido>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Pedido>> call,
                                           @NonNull Response<List<Pedido>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            pedidos.clear();
                            pedidos.addAll(resp.body());
                            adapter.notifyDataSetChanged();
                        } else {
                            Toast.makeText(CocinaActivity.this,
                                    "Error al cargar pedidos", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<Pedido>> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(CocinaActivity.this,
                                "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private class PedidoCocinaAdapter
            extends RecyclerView.Adapter<PedidoCocinaAdapter.ViewHolder> {

        private final List<Pedido> lista;

        PedidoCocinaAdapter(List<Pedido> lista) {
            this.lista = lista;
        }

        @NonNull @Override
        public ViewHolder onCreateViewHolder(
                @NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pedido_cocina, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(
                @NonNull ViewHolder vh, int position) {

            Pedido p = lista.get(position);
            vh.tvMesa.setText(p.getMesa());

            // Toggle del bloque de productos
            vh.itemView.setOnClickListener(v -> {
                int vis = vh.productos.getVisibility() == View.VISIBLE
                        ? View.GONE : View.VISIBLE;
                vh.productos.setVisibility(vis);
            });

            // Obtener o crear el set de líneas marcadas para este pedido
            final HashSet<String> marcados = lineasListas
                    .computeIfAbsent(p.getId(), k -> new HashSet<>());

            // Repintar siempre los CheckBoxes
            vh.productos.removeAllViews();
            for (LineaPedido lp : p.getProductos()) {
                CheckBox cb = new CheckBox(vh.productos.getContext());
                cb.setText(lp.getNombreProducto());
                cb.setChecked(marcados.contains(lp.getNombreProducto()));

                cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        marcados.add(lp.getNombreProducto());
                        int numeroMesa = Integer.parseInt(
                                p.getMesa().replaceAll("\\D+", "")
                        );
                        Notificacion noti = new Notificacion(
                                lp.getNombreProducto() + " listo",
                                numeroMesa,
                                "pendiente"
                        );
                        svc.crearNotificacion(noti)
                                .enqueue(new Callback<List<Notificacion>>() {
                                    @Override
                                    public void onResponse(
                                            Call<List<Notificacion>> call,
                                            Response<List<Notificacion>> resp
                                    ) {
                                        if (resp.isSuccessful() && resp.body() != null
                                                && !resp.body().isEmpty()) {
                                            Notificacion creada = resp.body().get(0);
                                            Toast.makeText(
                                                    CocinaActivity.this,
                                                    lp.getNombreProducto() + " notificado (id=" + creada.getId() + ")",
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        } else {
                                            Toast.makeText(
                                                    CocinaActivity.this,
                                                    "Error notificación: " + resp.code(),
                                                    Toast.LENGTH_SHORT
                                            ).show();
                                        }
                                    }

                                    @Override
                                    public void onFailure(
                                            Call<List<Notificacion>> call,
                                            Throwable t
                                    ) {
                                        Toast.makeText(
                                                CocinaActivity.this,
                                                "Fallo de red notificación: " + t.getMessage(),
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }
                                });
                    } else {
                        // Desmarcar localmente
                        marcados.remove(lp.getNombreProducto());
                    }
                });

                vh.productos.addView(cb);
            }
        }

        @Override public int getItemCount() {
            return lista.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvMesa;
            final LinearLayout productos;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvMesa    = itemView.findViewById(R.id.tvMesaCocina);
                productos = itemView.findViewById(R.id.layoutProductosCocina);
            }
        }
    }
}

