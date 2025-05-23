// src/main/java/com/example/meseropro/adapter/AdaptadorNotificacion.java
package com.example.meseropro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meseropro.R;
import com.example.meseropro.model.Notificacion;
import java.util.List;

public class AdaptadorNotificacion
        extends RecyclerView.Adapter<AdaptadorNotificacion.VH> {

    public interface OnNotificacionClickListener {
        void onNotificacionClick(Notificacion notificacion);
    }

    private final List<Notificacion> items;
    private final OnNotificacionClickListener listener;

    public AdaptadorNotificacion(List<Notificacion> items,
                                 OnNotificacionClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_notificacion, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Notificacion n = items.get(pos);
        h.tvMensaje.setText(n.getMensaje() + " (Mesa " + n.getMesa() + ")");
        h.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onNotificacionClick(n);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvMensaje;
        VH(@NonNull View itemView) {
            super(itemView);
            tvMensaje = itemView.findViewById(R.id.tvMensajeNotificacion);
        }
    }
}
