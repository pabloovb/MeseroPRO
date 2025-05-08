package com.example.meseropro.network;

import com.example.meseropro.model.Mesa;
import com.example.meseropro.model.Pedido;
import com.example.meseropro.model.Product;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface SupabaseService {

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNqdW10Y251a2Z6eGhqcWtvZHFpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM2MTk4MzQsImV4cCI6MjA1OTE5NTgzNH0.3-V0_KcOZDJ6ogcjtV11rIZcDxUScwVgpJdDzjX7WVo",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNqdW10Y251a2Z6eGhqcWtvZHFpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM2MTk4MzQsImV4cCI6MjA1OTE5NTgzNH0.3-V0_KcOZDJ6ogcjtV11rIZcDxUScwVgpJdDzjX7WVo"
    })
    @GET("productos")
    Call<List<Product>> getProductos();

    @POST("pedidos")
    Call<Void> guardarPedido(@Body Pedido pedido);

    @POST("mesas")
    Call<Void> crearMesa(@Body Mesa mesa);
    @GET("mesas")
    Call<List<Mesa>> getMesaPorNumero(@Query("numero") int numero);

    @GET("pedidos")
    Call<List<Pedido>> getPedidosPorMesa(@Query("mesa_id") int mesaId, @Query("estado") String estado);
}




