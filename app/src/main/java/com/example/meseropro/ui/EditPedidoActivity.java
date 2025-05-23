// src/main/java/com/example/meseropro/ui/EditPedidoActivity.java
package com.example.meseropro.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.gridlayout.widget.GridLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.meseropro.R;
import com.example.meseropro.adapter.PedidoAdapter;
import com.example.meseropro.adapter.ProductoAdapter;
import com.example.meseropro.model.LineaPedido;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.model.Product;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.SupabaseService;
import com.google.android.material.button.MaterialButton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPedidoActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private RecyclerView recyclerProductos, recyclerPedido;
    private GridLayout layoutCategorias;
    private TextView tvTotal;
    private MaterialButton btnActualizarPedido;
    private ProductoAdapter productoAdapter;
    private PedidoAdapter pedidoAdapter;
    private final HashMap<String, LineaPedido> pedidoMap = new HashMap<>();
    private Pedido pedido;
    private SupabaseService svc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        svc = APIClient.getClient().create(SupabaseService.class);

        // 1) Toolbar
        setupToolbar(R.id.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 2) Drawer + toggle
        drawerLayout = findViewById(R.id.drawer_layout);
        ImageButton btnToggle = findViewById(R.id.btnTogglePedido);
        btnToggle.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            else
                drawerLayout.openDrawer(GravityCompat.END);
        });

        // 3) Leer ID del pedido
        int pedidoId = getIntent().getIntExtra("pedido_id", -1);
        if (pedidoId < 0) {
            Toast.makeText(this, "ID de pedido inválido", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // 4) Fetch desde Supabase
        svc.getPedidoPorId("eq." + pedidoId).enqueue(new Callback<List<Pedido>>() {
            @Override
            public void onResponse(Call<List<Pedido>> call, Response<List<Pedido>> resp) {
                if (resp.isSuccessful() && resp.body() != null && !resp.body().isEmpty()) {
                    pedido = resp.body().get(0);
                    inicializarUI();
                } else {
                    Toast.makeText(EditPedidoActivity.this,
                            "No se encontró el pedido", Toast.LENGTH_LONG).show();
                    finish();
                }
            }
            @Override
            public void onFailure(Call<List<Pedido>> call, Throwable t) {
                Toast.makeText(EditPedidoActivity.this,
                        "Fallo de red al cargar pedido", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    private void inicializarUI() {
        // Poblar el map con las líneas existentes
        for (LineaPedido lp : pedido.getProductos()) {
            pedidoMap.put(lp.getNombreProducto(), lp);
        }

        // Referencias
        layoutCategorias    = findViewById(R.id.layoutCategorias);
        recyclerProductos   = findViewById(R.id.recyclerProductos);
        recyclerPedido      = findViewById(R.id.recyclerPedido);
        tvTotal             = findViewById(R.id.tvTotalPedido);
        btnActualizarPedido = findViewById(R.id.btnEnviarPedido);

        // Recycler productos
        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
        productoAdapter = new ProductoAdapter(
                this, new ArrayList<>(), this::añadirAlPedido
        );
        recyclerProductos.setAdapter(productoAdapter);

        // Recycler pedido y total
        recyclerPedido.setLayoutManager(new LinearLayoutManager(this));
        actualizarListaPedido();

        // Mostrar “Mesa X” en el drawer
        LinearLayout panel = findViewById(R.id.layoutPanelDerecho);
        TextView tvMesa = new TextView(this);
        tvMesa.setText(pedido.getMesa());
        tvMesa.setTextColor(Color.BLACK);
        tvMesa.setTextSize(18);
        tvMesa.setPadding(16,0,0,16);
        panel.addView(tvMesa, 1);

        // Carga categorías y productos remotos
        cargarProductosDesdeSupabase();

        // Botón ACTUALIZAR
        btnActualizarPedido.setText("ACTUALIZAR");
        btnActualizarPedido.setOnClickListener(v -> actualizarPedido());

        // Auto-abrir drawer
        drawerLayout.openDrawer(GravityCompat.END);
    }

    private void cargarProductosDesdeSupabase() {
        svc.getProductos().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    List<Product> lista = resp.body();
                    productoAdapter.updateList(lista);
                    generarCategorias(lista);
                } else {
                    Toast.makeText(EditPedidoActivity.this,
                            "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(EditPedidoActivity.this,
                        "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generarCategorias(List<Product> productos) {
        layoutCategorias.removeAllViews();
        layoutCategorias.setColumnCount(2);
        HashSet<String> cats = new HashSet<>();
        for (Product p : productos) {
            if (p.getCategoria()!=null && !p.getCategoria().isEmpty())
                cats.add(p.getCategoria());
        }
        // “Todos”
        MaterialButton btnTodos = new MaterialButton(this);
        btnTodos.setText("Todos");
        btnTodos.setOnClickListener(v -> productoAdapter.updateList(productos));
        layoutCategorias.addView(btnTodos);
        // Por categoría
        for (String c : cats) {
            MaterialButton b = new MaterialButton(this);
            b.setText(c);
            b.setOnClickListener(v -> {
                List<Product> filt = new ArrayList<>();
                for (Product pr : productos)
                    if (c.equalsIgnoreCase(pr.getCategoria()))
                        filt.add(pr);
                productoAdapter.updateList(filt);
            });
            layoutCategorias.addView(b);
        }
    }

    private void añadirAlPedido(Product producto) {
        if (pedidoMap.containsKey(producto.getNombre())) {
            pedidoMap.get(producto.getNombre()).aumentarCantidad();
        } else {
            pedidoMap.put(
                    producto.getNombre(),
                    new LineaPedido(producto.getNombre(),1,producto.getPrecio())
            );
        }
        actualizarListaPedido();
        Toast.makeText(this,
                producto.getNombre()+" añadido al pedido",
                Toast.LENGTH_SHORT
        ).show();
    }

    private void actualizarListaPedido() {
        List<LineaPedido> lista = new ArrayList<>(pedidoMap.values());
        pedidoAdapter = new PedidoAdapter(this, lista);
        recyclerPedido.setAdapter(pedidoAdapter);
        double total=0;
        for (LineaPedido lp: lista) total+=lp.getTotal();
        tvTotal.setText(String.format("TOTAL: € %.2f", total));
    }

    private void actualizarPedido() {
        // Reconstruir objeto con las líneas nuevas + viejas
        List<LineaPedido> prods = new ArrayList<>(pedidoMap.values());
        double total=0; for(LineaPedido lp:prods) total+=lp.getTotal();
        Pedido actualizado = new Pedido(
                pedido.getId(),
                pedido.getMesa(),
                prods,
                total,
                pedido.getCamarero(),
                pedido.getComensales(),
                pedido.getEstado()
        );
        // Llamada PATCH completa
        String filtro = "eq." + pedido.getId();
        svc.actualizarPedidoCompleto(filtro, actualizado)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> c, Response<Void> r) {
                        if (r.isSuccessful()) {
                            Toast.makeText(EditPedidoActivity.this,
                                    "Pedido actualizado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            String err;
                            try { err = r.errorBody()!=null
                                    ? r.errorBody().string()
                                    : "sin cuerpo";
                            } catch(IOException e){
                                err = e.getMessage();
                            }
                            Toast.makeText(EditPedidoActivity.this,
                                    "Error al actualizar: "+err,
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<Void> c, Throwable t) {
                        Toast.makeText(EditPedidoActivity.this,
                                "Fallo de red: "+t.getMessage(),
                                Toast.LENGTH_LONG).show();
                    }
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId()==android.R.id.home) {
            if (drawerLayout.isDrawerOpen(GravityCompat.END))
                drawerLayout.closeDrawer(GravityCompat.END);
            else
                drawerLayout.openDrawer(GravityCompat.END);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
