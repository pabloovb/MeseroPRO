// src/main/java/com/example/meseropro/ui/NotificacionesActivity.java
package com.example.meseropro.ui;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.meseropro.R;
import com.example.meseropro.adapter.AdaptadorNotificacion;
import com.example.meseropro.model.Notificacion;
import com.example.meseropro.network.APIClient;
import com.example.meseropro.network.SupabaseService;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificacionesActivity extends BaseActivity {

    private RecyclerView rvNoti;
    private AdaptadorNotificacion adapter;
    private SupabaseService service;

    @Override
    protected void onCreate(@Nullable Bundle s) {
        super.onCreate(s);
        setContentView(R.layout.activity_notificaciones);

        setupToolbar(R.id.toolbarNoti);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        service = APIClient.getClient().create(SupabaseService.class);

        rvNoti = findViewById(R.id.rvNotificaciones);
        rvNoti.setLayoutManager(new LinearLayoutManager(this));

        cargarNotificaciones();
    }

    private void cargarNotificaciones() {
        service.getNotificacionesPorEstado("eq.pendiente")
                .enqueue(new Callback<List<Notificacion>>() {
                    @Override
                    public void onResponse(Call<List<Notificacion>> call,
                                           Response<List<Notificacion>> resp) {
                        if (resp.isSuccessful() && resp.body() != null) {
                            List<Notificacion> lista = resp.body();
                            adapter = new AdaptadorNotificacion(
                                    lista,
                                    NotificacionesActivity.this::mostrarDialogoCerrar
                            );
                            rvNoti.setAdapter(adapter);
                        } else {
                            Toast.makeText(
                                    NotificacionesActivity.this,
                                    "Error al cargar notificaciones",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Notificacion>> call, Throwable t) {
                        Toast.makeText(
                                NotificacionesActivity.this,
                                "Fallo de red: " + t.getMessage(),
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                });
    }

    private void mostrarDialogoCerrar(Notificacion n) {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Marcar servido")
                .setMessage("¿Marcar \"" + n.getMensaje() + "\" como servido?")
                .setPositiveButton("Sí", (d, w) -> {
                    // PATCH -> estado = servido
                    Map<String,String> cambios = new HashMap<>();
                    cambios.put("estado", "servido");
                    service.actualizarNotificacion(
                            "eq." + n.getId(), cambios
                    ).enqueue(new Callback<Void>() {
                        @Override public void onResponse(
                                Call<Void> c, Response<Void> r) {
                            if (r.isSuccessful()) {
                                Toast.makeText(
                                        NotificacionesActivity.this,
                                        "Notificación cerrada",
                                        Toast.LENGTH_SHORT
                                ).show();
                                cargarNotificaciones(); // recarga lista
                            } else {
                                try {
                                    String err = r.errorBody()!=null
                                            ? r.errorBody().string() : "??";
                                    Toast.makeText(
                                            NotificacionesActivity.this,
                                            "Error: " + err,
                                            Toast.LENGTH_LONG
                                    ).show();
                                } catch (IOException ignored){}
                            }
                        }
                        @Override public void onFailure(Call<Void> c, Throwable t) {
                            Toast.makeText(
                                    NotificacionesActivity.this,
                                    "Fallo de red", Toast.LENGTH_SHORT
                            ).show();
                        }
                    });
                })
                .setNegativeButton("No", null)
                .show();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}


