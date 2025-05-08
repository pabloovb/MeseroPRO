package com.example.meseropro.network;

import com.example.meseropro.model.Pedido;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface PedidoService {


    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNqdW10Y251a2Z6eGhqcWtvZHFpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM2MTk4MzQsImV4cCI6MjA1OTE5NTgzNH0.3-V0_KcOZDJ6ogcjtV11rIZcDxUScwVgpJdDzjX7WVo",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNqdW10Y251a2Z6eGhqcWtvZHFpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM2MTk4MzQsImV4cCI6MjA1OTE5NTgzNH0.3-V0_KcOZDJ6ogcjtV11rIZcDxUScwVgpJdDzjX7WVo",
            "Content-Type: application/json",
            "Prefer: return=minimal"
    })
    @POST("pedidos")
    Call<Void> guardarPedido(@Body Pedido pedido);}