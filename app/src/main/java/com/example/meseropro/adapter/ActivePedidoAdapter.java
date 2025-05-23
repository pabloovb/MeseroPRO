// src/main/java/com/example/meseropro/adapter/ActivePedidoAdapter.java
package com.example.meseropro.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.ui.EditPedidoActivity;
import java.util.List;

public class ActivePedidoAdapter extends RecyclerView.Adapter<ActivePedidoAdapter.VH> {

    private final Context ctx;
    private final List<Pedido> items;

    public ActivePedidoAdapter(Context ctx, List<Pedido> items) {
        this.ctx = ctx;
        this.items = items;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(ctx)
                .inflate(android.R.layout.simple_list_item_2, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Pedido ped = items.get(position);
        holder.tv1.setText(ped.getMesa() + " (" + ped.getComensales() + " pax)");
        holder.tv2.setText(String.format("TOTAL: € %.2f", ped.getTotal()));
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(ctx, EditPedidoActivity.class);
            intent.putExtra("pedido_id", ped.getId());
            ctx.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        final TextView tv1, tv2;
        VH(View itemView) {
            super(itemView);
            tv1 = itemView.findViewById(android.R.id.text1);
            tv2 = itemView.findViewById(android.R.id.text2);
        }
    }
}
