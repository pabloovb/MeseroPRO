// src/main/java/com/example/meseropro/model/Usuario.java
package com.example.meseropro.model;

import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

public class Usuario implements Serializable {
    private Integer id;
    private String nombre;
    private String rol;   // p.ej. "camarero", "cocina", "barra", "admin"

    public Usuario() {}

    public Usuario(Integer id, String nombre, String rol) {
        this.id = id;
        this.nombre = nombre;
        this.rol = rol;
    }

    // getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
