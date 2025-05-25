// src/main/java/com/example/meseropro/ui/GestionProductosActivity.java
package com.example.meseropro.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meseropro.R;
import com.example.meseropro.adapter.ProductoGestionAdapter;
import com.example.meseropro.model.Product;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.SupabaseService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GestionProductosActivity extends BaseActivity {

    private SupabaseService svc;
    private List<Product> productos = new ArrayList<>();
    private ProductoGestionAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gestion_productos);

        // Toolbar
        MaterialToolbar tb = findViewById(R.id.toolbarProd);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        svc = APIClient.getClient().create(SupabaseService.class);

        RecyclerView rv = findViewById(R.id.rvProductosGestion);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProductoGestionAdapter(productos, new ProductoGestionAdapter.Listener() {
            @Override public void onEdit(Product p)   { mostrarDialog(p); }
            @Override public void onDelete(Product p) { borrarProducto(p); }
        });
        rv.setAdapter(adapter);

        FloatingActionButton fab = findViewById(R.id.fabAddProducto);
        fab.setOnClickListener(v -> mostrarDialog(null));

        cargarProductos();
    }

    private void cargarProductos() {
        svc.getProductos().enqueue(new Callback<List<Product>>() {
            @Override public void onResponse(@NonNull Call<List<Product>> c,
                                             @NonNull Response<List<Product>> r) {
                if (r.isSuccessful() && r.body()!=null) {
                    productos.clear();
                    productos.addAll(r.body());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(GestionProductosActivity.this,
                            "Error cargar productos ("+r.code()+")",
                            Toast.LENGTH_SHORT).show();
                }
            }
            @Override public void onFailure(@NonNull Call<List<Product>> c,
                                            @NonNull Throwable t) {
                Toast.makeText(GestionProductosActivity.this,
                        "Fallo red: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarDialog(Product existente) {
        View dlg = LayoutInflater.from(this)
                .inflate(R.layout.dialog_producto, null, false);
        EditText etN = dlg.findViewById(R.id.etNombreProd);
        EditText etC = dlg.findViewById(R.id.etCategoriaProd);
        EditText etP = dlg.findViewById(R.id.etPrecioProd);
        EditText etU = dlg.findViewById(R.id.etImagenUrlProd);

        String titulo = existente==null ? "Nuevo producto" : "Editar producto";
        if (existente != null) {
            etN.setText(existente.getNombre());
            etC.setText(existente.getCategoria());
            etP.setText(String.valueOf(existente.getPrecio()));
            etU.setText(existente.getImagen_url());
        }

        new AlertDialog.Builder(this)
                .setTitle(titulo)
                .setView(dlg)
                .setPositiveButton("Guardar", (d, w) -> {
                    String nombre    = etN.getText().toString().trim();
                    String categoria = etC.getText().toString().trim();
                    String precioStr = etP.getText().toString().trim();
                    String urlImg     = etU.getText().toString().trim();

                    if (TextUtils.isEmpty(nombre)
                            || TextUtils.isEmpty(categoria)
                            || TextUtils.isEmpty(precioStr)) {
                        Toast.makeText(this,
                                "Rellena nombre, categoría y precio",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double precio = Double.parseDouble(precioStr);
                    if (existente==null) {
                        // crear
                        Product nuevo = new Product(nombre, categoria, precio, urlImg);
                        svc.crearProducto(nuevo)
                                .enqueue(new SimpleCallback("creado"));
                    } else {
                        // editar
                        Map<String,Object> cambios = new HashMap<>();
                        cambios.put("nombre",    nombre);
                        cambios.put("categoria", categoria);
                        cambios.put("precio",    precio);
                        cambios.put("imagen_url", urlImg);
                        String filtro = "eq." + existente.getId();
                        svc.actualizarProducto(filtro, cambios)
                                .enqueue(new SimpleCallback("actualizado"));
                    }
                })
                .setNegativeButton("Cancelar", null)
                .show();
    }

    private void borrarProducto(Product p) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar “"+p.getNombre()+"”?")
                .setPositiveButton("Sí", (d,w)-> {
                    String filtro = "eq." + p.getId();
                    svc.eliminarProducto(filtro)
                            .enqueue(new SimpleCallback("eliminado"));
                })
                .setNegativeButton("No", null)
                .show();
    }

    private class SimpleCallback implements Callback<Void> {
        private final String accion;
        SimpleCallback(String accion) { this.accion = accion; }
        @Override public void onResponse(Call<Void> c, Response<Void> r) {
            if (r.isSuccessful()) {
                Toast.makeText(GestionProductosActivity.this,
                        "Producto "+accion, Toast.LENGTH_SHORT).show();
                cargarProductos();
            } else {
                String msg;
                try { msg = r.errorBody()!=null
                        ? r.errorBody().string()
                        : ("code="+r.code());
                } catch(IOException e){ msg=e.getMessage(); }
                Toast.makeText(GestionProductosActivity.this,
                        "Error "+accion+": "+msg, Toast.LENGTH_LONG).show();
            }
        }
        @Override public void onFailure(Call<Void> c, Throwable t) {
            Toast.makeText(GestionProductosActivity.this,
                    "Fallo red: "+t.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }
}

