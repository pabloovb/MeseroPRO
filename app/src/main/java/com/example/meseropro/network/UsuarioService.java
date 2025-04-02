package com.example.meseropro.network;

import com.example.meseropro.model.Usuario;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface UsuarioService {

    @GET("usuarios")
    Call<List<Usuario>> loginUsuario(
            @Query("correo") String correo,
            @Query("contraseña") String contrasena
    );
}

