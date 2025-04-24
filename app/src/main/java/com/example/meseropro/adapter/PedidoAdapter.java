package com.example.meseropro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meseropro.R;
import com.example.meseropro.model.LineaPedido;
import com.example.meseropro.ui.PedidoActivity;

import java.util.List;

public class PedidoAdapter extends RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder> {

    private final Context context;
    private final List<LineaPedido> pedidoList;

    public PedidoAdapter(Context context, List<LineaPedido> pedidoList) {
        this.context = context;
        this.pedidoList = pedidoList;
    }

    @NonNull
    @Override
    public PedidoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_linea_pedido, parent, false);
        return new PedidoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PedidoViewHolder holder, int position) {
        LineaPedido linea = pedidoList.get(position);

        // ✅ Mostrar nombre, cantidad, total
        holder.tvNombre.setText(linea.getNombreProducto());
        holder.tvCantidad.setText("x" + linea.getCantidad());
        holder.tvTotal.setText(String.format("€ %.2f", linea.getTotal()));

        // ✅ Eliminar al hacer clic en 🗑️
        holder.btnEliminar.setOnClickListener(v -> {
            if (context instanceof PedidoActivity) {
                ((PedidoActivity) context).eliminarLineaPedido(linea.getNombreProducto());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pedidoList.size();
    }

    public static class PedidoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvCantidad, tvTotal, btnEliminar;

        public PedidoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreLinea);
            tvCantidad = itemView.findViewById(R.id.tvCantidadLinea);
            tvTotal = itemView.findViewById(R.id.tvTotalLinea);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
        }
    }
}
