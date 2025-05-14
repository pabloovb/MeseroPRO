// src/main/java/com/example/meseropro/network/SupabaseService.java
package com.example.meseropro.network;

import com.example.meseropro.model.Pedido;
import com.example.meseropro.model.Product;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
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
    @GET("pedidos")
    Call<List<Pedido>> getPedidosPorEstado(
            @Query(value = "estado", encoded = true) String estado
    );

    @Headers({
            "apikey: YOUR_API_KEY",
            "Authorization: Bearer YOUR_API_KEY",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @POST("pedidos")
    Call<Void> guardarPedido(@Body Pedido pedido);

    @Headers({
            "apikey: YOUR_API_KEY",
            "Authorization: Bearer YOUR_API_KEY",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @PATCH("pedidos")
    Call<Void> updatePedido(
            @Query(value = "id", encoded = true) String idFilter,
            @Body Pedido pedido
    );
}
