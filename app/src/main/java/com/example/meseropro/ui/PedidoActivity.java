// src/main/java/com/example/meseropro/ui/PedidoActivity.java
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PedidoActivity extends BaseActivity {

    private RecyclerView recyclerProductos, recyclerPedido;
    private ProductoAdapter productoAdapter;
    private PedidoAdapter pedidoAdapter;
    private List<Product> listaProductos = new ArrayList<>();
    private HashMap<String, LineaPedido> pedidoMap = new HashMap<>();
    private TextView tvTotal;
    private Button btnEnviarPedido;
    private GridLayout layoutCategorias;
    private int mesaNumero;
    private int comensales;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        setupToolbar(R.id.toolbar);
        // Obtener mesa y comensales del Intent
        mesaNumero = getIntent().getIntExtra("mesa", -1);
        comensales = getIntent().getIntExtra("comensales", 1);

        // Referencias UI
        recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerPedido     = findViewById(R.id.recyclerPedido);
        tvTotal            = findViewById(R.id.tvTotalPedido);
        btnEnviarPedido    = findViewById(R.id.btnEnviarPedido);
        layoutCategorias   = findViewById(R.id.layoutCategorias);

        // Configurar RecyclerViews
        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerPedido.setLayoutManager(new LinearLayoutManager(this));
        pedidoAdapter = new PedidoAdapter(this, new ArrayList<>());
        recyclerPedido.setAdapter(pedidoAdapter);

        // Mostrar el número de mesa
        TextView tvMesa = new TextView(this);
        tvMesa.setText("Mesa: " + mesaNumero);
        tvMesa.setTextSize(18);
        tvMesa.setPadding(16, 0, 0, 16);
        tvMesa.setTextColor(Color.BLACK);
        LinearLayout panelDerecho = findViewById(R.id.layoutPanelDerecho);
        panelDerecho.addView(tvMesa, 0);

        // Cargar productos
        cargarProductosDesdeSupabase();

        // Enviar pedido
        btnEnviarPedido.setOnClickListener(v -> {
            if (pedidoMap.isEmpty()) {
                Toast.makeText(this, "No hay productos en el pedido", Toast.LENGTH_SHORT).show();
            } else {
                guardarPedido();
            }
        });
    }

    private void cargarProductosDesdeSupabase() {
        SupabaseService service = APIClient.getClient().create(SupabaseService.class);
        service.getProductos().enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> resp) {
                if (resp.isSuccessful() && resp.body() != null) {
                    listaProductos = resp.body();
                    productoAdapter = new ProductoAdapter(
                            PedidoActivity.this,
                            listaProductos,
                            PedidoActivity.this::añadirAlPedido
                    );
                    recyclerProductos.setAdapter(productoAdapter);
                    generarCategorias(listaProductos);
                } else {
                    Toast.makeText(PedidoActivity.this,
                            "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(PedidoActivity.this,
                        "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void generarCategorias(List<Product> productos) {
        layoutCategorias.removeAllViews();
        layoutCategorias.setColumnCount(2);

        HashSet<String> categorias = new HashSet<>();
        for (Product p : productos) {
            if (p.getCategoria() != null && !p.getCategoria().isEmpty()) {
                categorias.add(p.getCategoria());
            }
        }

        Button btnTodos = crearBotonCategoria("Todos");
        btnTodos.setOnClickListener(v -> productoAdapter.updateList(listaProductos));
        layoutCategorias.addView(btnTodos);

        for (String categoria : categorias) {
            Button btn = crearBotonCategoria(categoria);
            btn.setOnClickListener(v -> {
                List<Product> filtrados = new ArrayList<>();
                for (Product p : listaProductos) {
                    if (categoria.equalsIgnoreCase(p.getCategoria())) {
                        filtrados.add(p);
                    }
                }
                productoAdapter.updateList(filtrados);
            });
            layoutCategorias.addView(btn);
        }
    }

    private Button crearBotonCategoria(String texto) {
        Button btn = new Button(this);
        btn.setText(texto);
        btn.setAllCaps(false);
        btn.setTextSize(13);
        btn.setPadding(16, 12, 16, 12);
        btn.setBackgroundColor(Color.parseColor("#CC5E12"));
        btn.setTextColor(Color.WHITE);
        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(8, 8, 8, 8);
        btn.setLayoutParams(params);
        return btn;
    }

    private void añadirAlPedido(Product producto) {
        if (pedidoMap.containsKey(producto.getNombre())) {
            pedidoMap.get(producto.getNombre()).aumentarCantidad();
        } else {
            pedidoMap.put(producto.getNombre(),
                    new LineaPedido(producto.getNombre(), 1, producto.getPrecio()));
        }
        actualizarListaPedido();
    }

    // Este método es llamado desde PedidoAdapter al hacer clic en 🗑️
    public void eliminarLineaPedido(String nombreProducto) {
        pedidoMap.remove(nombreProducto);
        actualizarListaPedido();
    }

    private void actualizarListaPedido() {
        List<LineaPedido> lineas = new ArrayList<>(pedidoMap.values());
        pedidoAdapter = new PedidoAdapter(this, lineas);
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

    private void guardarPedido() {
        List<LineaPedido> productos = new ArrayList<>(pedidoMap.values());
        double total = 0;
        for (LineaPedido lp : productos) total += lp.getTotal();

        Pedido pedido = new Pedido(
                "Mesa " + mesaNumero,
                productos,
                total,
                "Camarero 1",
                comensales,
                "pendiente"
        );

        SupabaseService service = APIClient.getClient().create(SupabaseService.class);
        service.guardarPedido(pedido).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> resp) {
                if (resp.isSuccessful()) {
                    Toast.makeText(PedidoActivity.this,
                            "Pedido guardado exitosamente", Toast.LENGTH_SHORT).show();
                    pedidoMap.clear();
                    actualizarListaPedido();
                } else {
                    Log.e("PedidoActivity",
                            "Error guardar pedido: " + resp.code());
                    Toast.makeText(PedidoActivity.this,
                            "Error al guardar pedido", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e("PedidoActivity",
                        "Fallo red al guardar pedido", t);
                Toast.makeText(PedidoActivity.this,
                        "Fallo al guardar pedido", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
