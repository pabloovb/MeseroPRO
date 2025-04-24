package com.example.meseropro.model;

public class Product {

    private String nombre;
    private double precio;
    private String categoria;
    private String imagen_url; // opcional

    public Product() {
        // Requerido por Retrofit/Gson
    }

    public String getNombre() { return nombre; }
    public double getPrecio() { return precio; }
    public String getCategoria() { return categoria; }
    public String getImagenUrl() { return imagen_url; }

    public void setNombre(String nombre) { this.nombre = nombre; }
    public void setPrecio(double precio) { this.precio = precio; }
    public void setCategoria(String categoria) { this.categoria = categoria; }
    public void setImagenUrl(String imagen_url) { this.imagen_url = imagen_url; }
}
