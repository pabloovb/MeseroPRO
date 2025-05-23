// src/main/java/com/example/meseropro/adapter/PedidoBarraAdapter.java
package com.example.meseropro.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meseropro.R;
import com.example.meseropro.model.LineaPedido;
import com.example.meseropro.model.Notificacion;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.network.SupabaseService;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoBarraAdapter
        extends RecyclerView.Adapter<PedidoBarraAdapter.VH> {

    private final List<Pedido> pedidos;
    private final Map<Integer, HashSet<String>> bebidasListas;
    private final SupabaseService svc;

    private static final String TAG = "PedidoBarraAdapter";

    public PedidoBarraAdapter(
            List<Pedido> pedidos,
            Map<Integer, HashSet<String>> bebidasListas,
            SupabaseService svc
    ) {
        this.pedidos = pedidos;
        this.bebidasListas = bebidasListas;
        this.svc = svc;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pedido_barra, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Pedido p = pedidos.get(pos);
        Context ctx = h.itemView.getContext();

        h.tvMesa.setText(p.getMesa());

        // Resumen de productos
        StringBuilder sb = new StringBuilder();
        for (LineaPedido lp : p.getProductos()) {
            sb.append(lp.getCantidad())
                    .append("× ")
                    .append(lp.getNombreProducto())
                    .append(", ");
        }
        String resumen = sb.length() > 0
                ? sb.substring(0, sb.length() - 2)
                : "";
        h.tvResumen.setText(resumen);

        // Toggle del detalle
        h.itemView.setOnClickListener(v -> {
            int vis = h.productos.getVisibility() == View.VISIBLE
                    ? View.GONE : View.VISIBLE;
            h.productos.setVisibility(vis);
            h.btnCerrar.setVisibility(vis);
        });

        // Estado local de checkboxes de bebidas
        HashSet<String> marcadas = bebidasListas
                .computeIfAbsent(p.getId(), k -> new HashSet<>());

        h.productos.removeAllViews();
        for (LineaPedido lp : p.getProductos()) {
            CheckBox cb = new CheckBox(ctx);
            cb.setText(lp.getNombreProducto());
            cb.setChecked(marcadas.contains(lp.getNombreProducto()));
            cb.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    marcadas.add(lp.getNombreProducto());
                    // Aquí podrías crear notificación si lo deseas
                } else {
                    marcadas.remove(lp.getNombreProducto());
                }
            });
            h.productos.addView(cb);
        }

        // BOTÓN: cerrar pedido (PATCH parcial solo estado)
        h.btnCerrar.setOnClickListener(v -> {
            String filtro = "eq." + p.getId();
            Map<String, String> cambios = new HashMap<>();
            cambios.put("estado", "finalizado");

            svc.cerrarPedido(filtro, cambios)
                    .enqueue(new Callback<Void>() {
                        @Override
                        public void onResponse(Call<Void> call, Response<Void> r) {
                            if (r.isSuccessful()) {
                                Toast.makeText(ctx,
                                        "Pedido finalizado correctamente",
                                        Toast.LENGTH_SHORT
                                ).show();
                                pedidos.remove(p);
                                notifyDataSetChanged();
                            } else {
                                String detalle;
                                try {
                                    detalle = r.errorBody() != null
                                            ? r.errorBody().string()
                                            : "sin cuerpo";
                                } catch (IOException e) {
                                    detalle = e.getMessage();
                                }
                                Log.e(TAG, "Error cierre pedido: " + detalle);
                                Toast.makeText(ctx,
                                        "Error al cerrar: " + detalle,
                                        Toast.LENGTH_LONG
                                ).show();
                            }
                        }
                        @Override
                        public void onFailure(Call<Void> call, Throwable t) {
                            Log.e(TAG, "Fallo red cierre pedido", t);
                            Toast.makeText(ctx,
                                    "Fallo red: " + t.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        }
                    });
        });
    }

    @Override public int getItemCount() {
        return pedidos.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tvMesa, tvResumen;
        final LinearLayout productos;
        final Button btnCerrar;

        VH(View v) {
            super(v);
            tvMesa    = v.findViewById(R.id.tvMesaBarra);
            tvResumen = v.findViewById(R.id.tvResumenBarra);
            productos = v.findViewById(R.id.layoutProductosBarra);
            btnCerrar = v.findViewById(R.id.btnCerrarPedido);
        }
    }
}
