// src/main/java/com/example/meseropro/model/Product.java
package com.example.meseropro.model;

import java.io.Serializable;

public class Product implements Serializable {
    private Integer id;
    private String nombre;
    private String categoria;
    private double precio;
    private String imagen_url;

    public Product() {}

    // Para creación (sin id)
    public Product(String nombre, String categoria, double precio, String imagen_url) {
        this.nombre      = nombre;
        this.categoria   = categoria;
        this.precio      = precio;
        this.imagen_url  = imagen_url;
    }

    // Getters / Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCategoria() { return categoria; }
    public void setCategoria(String categoria) { this.categoria = categoria; }

    public double getPrecio() { return precio; }
    public void setPrecio(double precio) { this.precio = precio; }

    public String getImagen_url() { return imagen_url; }
    public void setImagen_url(String imagen_url) { this.imagen_url = imagen_url; }


}
