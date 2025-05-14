// src/main/java/com/example/meseropro/adapter/ProductoAdapter.java
package com.example.meseropro.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.meseropro.R;
import com.example.meseropro.model.Product;

import java.util.ArrayList;
import java.util.List;

public class ProductoAdapter extends RecyclerView.Adapter<ProductoAdapter.ViewHolder> {

    public interface OnProductoClickListener {
        void onProductoClick(Product producto);
    }

    private Context context;
    private List<Product> productos;
    private OnProductoClickListener listener;

    public ProductoAdapter(Context context, List<Product> productos, OnProductoClickListener listener) {
        this.context = context;
        this.productos = new ArrayList<>(productos);
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Product producto = productos.get(position);
        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(String.format("€ %.2f", producto.getPrecio()));

        if (producto.getImagenUrl() != null && !producto.getImagenUrl().isEmpty()) {
            Glide.with(context)
                    .load(producto.getImagenUrl())
                    .placeholder(R.drawable.placeholder)
                    .into(holder.imgProducto);
        } else {
            holder.imgProducto.setImageResource(R.drawable.placeholder);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onProductoClick(producto);
        });
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    // Nuevo método para actualizar lista
    public void updateList(List<Product> nuevaLista) {
        this.productos = new ArrayList<>(nuevaLista);
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombre, tvPrecio;
        ImageView imgProducto;

        public ViewHolder(View itemView) {
            super(itemView);
            tvNombre = itemView.findViewById(R.id.tvNombreProducto);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProducto);
            imgProducto = itemView.findViewById(R.id.imgProducto);
        }
    }
}
