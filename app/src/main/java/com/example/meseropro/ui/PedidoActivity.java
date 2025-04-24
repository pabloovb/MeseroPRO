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

public class PedidoActivity extends AppCompatActivity {

    RecyclerView recyclerProductos, recyclerPedido;
    ProductoAdapter productoAdapter;
    PedidoAdapter pedidoAdapter;
    List<Product> listaProductos = new ArrayList<>();
    HashMap<String, LineaPedido> pedidoMap = new HashMap<>();
    TextView tvTotal;
    Button btnEnviarPedido;
    GridLayout layoutCategorias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);

        recyclerProductos = findViewById(R.id.recyclerProductos);
        recyclerPedido = findViewById(R.id.recyclerPedido);
        tvTotal = findViewById(R.id.tvTotalPedido);
        btnEnviarPedido = findViewById(R.id.btnEnviarPedido);
        layoutCategorias = findViewById(R.id.layoutCategorias);


        recyclerProductos.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerPedido.setLayoutManager(new LinearLayoutManager(this));

        cargarProductosDesdeSupabase();

        pedidoAdapter = new PedidoAdapter(this, new ArrayList<>());
        recyclerPedido.setAdapter(pedidoAdapter);

        int numeroMesa = getIntent().getIntExtra("mesa", -1);

        TextView tvMesa = new TextView(this);
        tvMesa.setText("Mesa: " + numeroMesa);
        tvMesa.setTextSize(18);
        tvMesa.setPadding(16, 0, 0, 16);
        tvMesa.setTextColor(getResources().getColor(android.R.color.black));

// Lo añades arriba del layout o donde tú veas
        LinearLayout panelDerecho = findViewById(R.id.layoutPanelDerecho); // ponle ID al LinearLayout derecho
        panelDerecho.addView(tvMesa, 0); // lo mete arriba de todo

        btnEnviarPedido.setOnClickListener(v -> {
            if (pedidoMap.isEmpty()) {
                Toast.makeText(this, "No hay productos en el pedido", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Pedido enviado correctamente ✅", Toast.LENGTH_SHORT).show();
                pedidoMap.clear();
                actualizarListaPedido();
            }
        });
    }

    private void cargarProductosDesdeSupabase() {
        SupabaseService service = APIClient.getClient().create(SupabaseService.class);
        Call<List<Product>> call = service.getProductos();

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(Call<List<Product>> call, Response<List<Product>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    listaProductos = response.body();
                    mostrarProductos(listaProductos);
                    generarCategorias(listaProductos);
                } else {
                    Toast.makeText(PedidoActivity.this, "Error al cargar productos", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Product>> call, Throwable t) {
                Toast.makeText(PedidoActivity.this, "Fallo de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarProductos(List<Product> productos) {
        productoAdapter = new ProductoAdapter(this, productos, this::añadirAlPedido);
        recyclerProductos.setAdapter(productoAdapter);
    }

    private void generarCategorias(List<Product> productos) {
        layoutCategorias.removeAllViews();
        layoutCategorias.setColumnCount(2); // 👉 Ahora 2 columnas

        HashSet<String> categorias = new HashSet<>();

        for (Product p : productos) {
            if (p.getCategoria() != null && !p.getCategoria().isEmpty()) {
                categorias.add(p.getCategoria());
            }
        }

        Button btnTodos = crearBotonCategoria("Todos");
        btnTodos.setOnClickListener(v -> mostrarProductos(listaProductos));
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
                mostrarProductos(filtrados);
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
        btn.setMinWidth(200);
        btn.setMaxLines(1);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.setMargins(8, 8, 8, 8);
        params.width = GridLayout.LayoutParams.WRAP_CONTENT;
        btn.setLayoutParams(params);

        return btn;
    }

    private void añadirAlPedido(Product producto) {
        if (pedidoMap.containsKey(producto.getNombre())) {
            pedidoMap.get(producto.getNombre()).aumentarCantidad();
        } else {
            pedidoMap.put(producto.getNombre(), new LineaPedido(
                    producto.getNombre(), // 🟢 ¡Esto debe ser el nombre real!
                    1,
                    producto.getPrecio()
            ));
        }
        actualizarListaPedido();
        Log.d("DEBUG_PEDIDO", "Añadiendo producto: " + producto.getNombre());
    }

    public void eliminarLineaPedido(String nombreProducto) {
        pedidoMap.remove(nombreProducto);
        actualizarListaPedido();
    }

    private void actualizarListaPedido() {
        List<LineaPedido> lineas = new ArrayList<>(pedidoMap.values());
        pedidoAdapter = new PedidoAdapter(this, lineas);
        recyclerPedido.setAdapter(pedidoAdapter);

        double total = 0;
        for (LineaPedido lp : lineas) {
            total += lp.getTotal();
        }
        tvTotal.setText(String.format("TOTAL: € %.2f", total));
    }
}
