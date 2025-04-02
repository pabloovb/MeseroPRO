package com.example.meseropro.model;

public class Usuario {

    private String id;
    private String nombre;
    private String correo;
    private String contraseña;
    private String rol;

    public Usuario() {
        // Constructor vacío obligatorio para Retrofit/Gson
    }

    // Getters
    public String getId() { return id; }
    public String getNombre() { return nombre; }
    public String getCorreo() { return correo; }
    public String getContraseña() { return contraseña; }
    public String getRol() { return rol; }

    // Setters
    public void setId(String id) { this.id = id; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setCorreo(String correo) { this.correo = correo; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
    public void setRol(String rol) { this.rol = rol; }
}

