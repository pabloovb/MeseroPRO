package com.example.meseropro.network;

import com.example.meseropro.model.Product;
import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Headers;

public interface SupabaseService {

    @Headers({
            "apikey: eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNqdW10Y251a2Z6eGhqcWtvZHFpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM2MTk4MzQsImV4cCI6MjA1OTE5NTgzNH0.3-V0_KcOZDJ6ogcjtV11rIZcDxUScwVgpJdDzjX7WVo",
            "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImNqdW10Y251a2Z6eGhqcWtvZHFpIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NDM2MTk4MzQsImV4cCI6MjA1OTE5NTgzNH0.3-V0_KcOZDJ6ogcjtV11rIZcDxUScwVgpJdDzjX7WVo"
    })
    @GET("productos")
    Call<List<Product>> getProductos();
}


