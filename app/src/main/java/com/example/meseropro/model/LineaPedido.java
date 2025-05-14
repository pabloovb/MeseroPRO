// src/main/java/com/example/meseropro/model/LineaPedido.java
package com.example.meseropro.model;

import java.io.Serializable;

public class LineaPedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nombreProducto;
    private int cantidad;
    private double precioUnitario;

    public LineaPedido(String nombreProducto, int cantidad, double precioUnitario) {
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getPrecioUnitario() {
        return precioUnitario;
    }

    public double getTotal() {
        return cantidad * precioUnitario;
    }

    public void aumentarCantidad() {
        this.cantidad++;
    }
}
