// src/main/java/com/example/meseropro/network/SupabaseService.java
package com.example.meseropro.network;

import com.example.meseropro.model.Notificacion;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.model.Product;
import com.example.meseropro.util.Constants;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface SupabaseService {

    @Headers({
            "apikey: YOUR_API_KEY",
            "Authorization: Bearer YOUR_API_KEY"
    })
    @GET("productos")
    Call<List<Product>> getProductos();

    @Headers({
            "apikey: YOUR_API_KEY",
            "Authorization: Bearer YOUR_API_KEY"
    })
    // Pedidos por estado
    @GET("pedidos")
    Call<List<Pedido>> getPedidosPorEstado(@Query("estado") String estado);

    @Headers({
            "apikey: YOUR_API_KEY",
            "Authorization: Bearer YOUR_API_KEY",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @POST("pedidos")
    Call<Void> guardarPedido(@Body Pedido pedido);

    @Headers({
            "apikey: "     + Constants.SUPABASE_API_KEY,
            "Authorization: Bearer " + Constants.SUPABASE_API_KEY
    })
    @GET("pedidos")
    Call<List<Pedido>> getPedidoPorId(@Query("id") String filtro);   // ej: filtro = "eq.42"

    // src/main/java/com/example/meseropro/network/SupabaseService.java
    @Headers({
            "apikey: "     + Constants.SUPABASE_API_KEY,
            "Authorization: Bearer " + Constants.SUPABASE_API_KEY,
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("pedidos")
    Call<Void> actualizarPedido(
            @Query("id") String filtro,
            @Body Map<String,String> cambios
    );

    @Headers({
            "apikey: "     + Constants.SUPABASE_API_KEY,
            "Authorization: Bearer " + Constants.SUPABASE_API_KEY,
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("pedidos")
    Call<Void> actualizarPedidoCompleto(
            @Query("id") String filtro,       // ej: "eq.42"
            @Body Pedido pedidoActualizado
    );

    /**
     * 2) PATCH parcial para cerrar el pedido (solo cambia el estado).
     */
    @Headers({
            "apikey: "     + Constants.SUPABASE_API_KEY,
            "Authorization: Bearer " + Constants.SUPABASE_API_KEY,
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("pedidos")
    Call<Void> cerrarPedido(
            @Query("id") String filtro,       // ej: "eq.42"
            @Body Map<String,String> cambios  // {"estado":"finalizado"}
    );

    // src/main/java/com/example/meseropro/network/SupabaseService.java
    @Headers({
            "apikey: "     + Constants.SUPABASE_API_KEY,
            "Authorization: Bearer " + Constants.SUPABASE_API_KEY,
            "Content-Type: application/json",
            "Prefer: return=representation"
    })
    @POST("notificaciones")
    Call<List<Notificacion>> crearNotificacion(@Body Notificacion notificacion);

    // 2) Leer notificaciones por estado
    @GET("notificaciones")
    Call<List<Notificacion>> getNotificacionesPorEstado(
            @Query("estado") String estado  // ej: "eq.pendiente"
    );

    // 3) Actualizar notificación (cambiar estado)
    @Headers({
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("notificaciones")
    Call<Void> actualizarNotificacion(
            @Query("id") String filtro,      // ej: "eq.123"
            @Body Map<String, String> cambios  // ej: {"estado":"servido"}
    );
}
