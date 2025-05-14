// src/main/java/com/example/meseropro/ui/EditPedidoActivity.java
package com.example.meseropro.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditPedidoActivity extends BaseActivity {

    private RecyclerView recyclerProductos, recyclerPedido;
    private ProductoAdapter productoAdapter;
    private PedidoAdapter pedidoAdapter;
    private List<Product> listaProductos = new ArrayList<>();
    private HashMap<String, LineaPedido> pedidoMap = new HashMap<>();
    private TextView tvTotal;
    private Button btnActualizarPedido;
    private GridLayout layoutCategorias;
    private Pedido pedido;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido); // reutiliza el layout de PedidoActivity
        setupToolbar(R.id.toolbar);

        // Recuperar el pedido pasado como Serializable
        pedido = (Pedido) getIntent().getSerializableExtra("pedido");
        for (LineaPedido lp : pedido.getProductos()) {
            pedidoMap.put(lp.getNombreProducto(), lp);
        }

        // Referencias UI
        recyclerProductos   = findViewById(R.id.recyclerProductos);
        recyclerPedido      = findViewById(R.id.recyclerPedido);
        tvTotal             = findViewById(R.id.tvTotalPedido);
        btnActualizarPedido = findViewById(R.id.btnEnviarPedido);
        layoutCategorias    = findViewById(R.id.layoutCategorias);

        // Inicializar RecyclerViews
        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerPedido.setLayoutManager(new LinearLayoutManager(this));
        pedidoAdapter = new PedidoAdapter(this, new ArrayList<>(pedidoMap.values()));
        recyclerPedido.setAdapter(pedidoAdapter);

        // Mostrar número de mesa arriba en el panel derecho
        TextView tvMesa = new TextView(this);
        tvMesa.setText(pedido.getMesa());
        tvMesa.setTextSize(18);
        tvMesa.setPadding(16, 0, 0, 16);
        tvMesa.setTextColor(Color.BLACK);
        LinearLayout panelDerecho = findViewById(R.id.layoutPanelDerecho);
        panelDerecho.addView(tvMesa, 0);

        // Carga productos desde Supabase
        cargarProductosDesdeSupabase();

        // Configurar botón de actualización
        btnActualizarPedido.setText("ACTUALIZAR");
        btnActualizarPedido.setOnClickListener(v -> actualizarPedido());

        // Mostrar total inicial
        actualizarTotal();
    }

    private void cargarProductosDesdeSupabase() {
        SupabaseService service = APIClient.getClient().create(SupabaseService.class);
        service.getProductos().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    listaProductos = resp.body();
                    productoAdapter = new ProductoAdapter(
                            EditPedidoActivity.this,
                            listaProductos,
                            EditPedidoActivity.this::añadirAlPedido
                    );
                    recyclerProductos.setAdapter(productoAdapter);
                    generarCategorias(listaProductos);
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
            if (p.getCategoria() != null && !p.getCategoria().isEmpty()) {
                cats.add(p.getCategoria());
            }
        }

        // Botón "Todos"
        Button btnTodos = new Button(this);
        btnTodos.setText("Todos");
        btnTodos.setAllCaps(false);
        btnTodos.setTextSize(13);
        btnTodos.setPadding(16, 12, 16, 12);
        btnTodos.setOnClickListener(v -> productoAdapter.updateList(listaProductos));
        layoutCategorias.addView(btnTodos);

        // Botones por categoría
        for (String c : cats) {
            Button btn = new Button(this);
            btn.setText(c);
            btn.setAllCaps(false);
            btn.setTextSize(13);
            btn.setPadding(16, 12, 16, 12);
            btn.setBackgroundColor(Color.parseColor("#CC5E12"));
            btn.setTextColor(Color.WHITE);
            GridLayout.LayoutParams params = new GridLayout.LayoutParams();
            params.setMargins(8, 8, 8, 8);
            btn.setLayoutParams(params);

            btn.setOnClickListener(v -> {
                List<Product> filt = new ArrayList<>();
                for (Product prod : listaProductos) {
                    if (c.equalsIgnoreCase(prod.getCategoria())) {
                        filt.add(prod);
                    }
                }
                productoAdapter.updateList(filt);
            });

            layoutCategorias.addView(btn);
        }
    }

    private void añadirAlPedido(Product producto) {
        if (pedidoMap.containsKey(producto.getNombre())) {
            pedidoMap.get(producto.getNombre()).aumentarCantidad();
        } else {
            pedidoMap.put(
                    producto.getNombre(),
                    new LineaPedido(producto.getNombre(), 1, producto.getPrecio())
            );
        }
        pedidoAdapter = new PedidoAdapter(this, new ArrayList<>(pedidoMap.values()));
        recyclerPedido.setAdapter(pedidoAdapter);
        actualizarTotal();
    }

    private void actualizarTotal() {
        double total = 0;
        for (LineaPedido lp : pedidoMap.values()) {
            total += lp.getTotal();
        }
        tvTotal.setText(String.format("TOTAL: € %.2f", total));
    }

    private void actualizarPedido() {
        // Reconstruir lista y total
        List<LineaPedido> prods = new ArrayList<>(pedidoMap.values());
        double total = 0;
        for (LineaPedido lp : prods) total += lp.getTotal();

        // Construir objeto con datos actualizados
        Pedido actualizado = new Pedido(
                pedido.getId(),
                pedido.getMesa(),
                prods,
                total,
                pedido.getCamarero(),
                pedido.getComensales(),
                pedido.getEstado()
        );

        // Enviar PATCH con filtro "id=eq.<valor>"
        SupabaseService svc = APIClient.getClient().create(SupabaseService.class);
        svc.updatePedido(
                "eq." + pedido.getId(),
                actualizado
        ).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> r) {
                if (r.isSuccessful()) {
                    Toast.makeText(EditPedidoActivity.this,
                            "Pedido actualizado", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    try {
                        Log.e("EditPedido", r.errorBody().string());
                    } catch (IOException e) {
                        Log.e("EditPedido", "Error leyendo body", e);
                    }
                    Toast.makeText(EditPedidoActivity.this,
                            "Error al actualizar", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("EditPedido", "Fallo de red", t);
                Toast.makeText(EditPedidoActivity.this,
                        "Fallo de red", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
