// src/main/java/com/example/meseropro/adapter/ProductoGestionAdapter.java
package com.example.meseropro.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meseropro.R;
import com.example.meseropro.model.Product;
import java.util.List;

public class ProductoGestionAdapter
        extends RecyclerView.Adapter<ProductoGestionAdapter.VH> {

    public interface Listener {
        void onEdit(Product p);
        void onDelete(Product p);
    }

    private final List<Product> items;
    private final Listener listener;

    public ProductoGestionAdapter(List<Product> items, Listener listener) {
        this.items    = items;
        this.listener = listener;
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_producto_gestion, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        Product p = items.get(pos);
        h.tvNombre.setText(p.getNombre());
        h.tvCategoria.setText(p.getCategoria());
        h.tvPrecio.setText(String.format("€ %.2f", p.getPrecio()));

        h.btnEdit.setOnClickListener(v -> listener.onEdit(p));
        h.btnDelete.setOnClickListener(v -> listener.onDelete(p));
    }

    @Override public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView    tvNombre, tvCategoria, tvPrecio;
        ImageButton btnEdit, btnDelete;
        VH(View v) {
            super(v);
            tvNombre   = v.findViewById(R.id.tvNombreProd);
            tvCategoria= v.findViewById(R.id.tvCategoriaProd);
            tvPrecio   = v.findViewById(R.id.tvPrecioProd);
            btnEdit    = v.findViewById(R.id.btnEditProd);
            btnDelete  = v.findViewById(R.id.btnDeleteProd);
        }
    }
}
