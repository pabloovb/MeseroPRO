// src/main/java/com/example/meseropro/model/Notificacion.java
package com.example.meseropro.model;

public class Notificacion {
    private Integer id;        // null al crear, llenado por Supabase
    private String mensaje;
    private Integer mesa;
    private String estado;

    public Notificacion() { /* necesario para GSON */ }

    // Constructor para creación
    public Notificacion(String mensaje, Integer mesa, String estado) {
        this.mensaje = mensaje;
        this.mesa = mesa;
        this.estado = estado;
    }

    // Getters y setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMensaje() { return mensaje; }
    public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    public Integer getMesa() { return mesa; }
    public void setMesa(Integer mesa) { this.mesa = mesa; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

