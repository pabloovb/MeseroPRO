// src/main/java/com/example/meseropro/model/Pedido.java
package com.example.meseropro.model;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    private int id;
    private String mesa;
    private List<LineaPedido> productos;
    private double total;
    private String camarero;
    private int comensales;
    private String estado;

    // Para lectura desde Supabase (incluye el id)
    public Pedido(int id,
                  String mesa,
                  List<LineaPedido> productos,
                  double total,
                  String camarero,
                  int comensales,
                  String estado) {
        this.id = id;
        this.mesa = mesa;
        this.productos = productos;
        this.total = total;
        this.camarero = camarero;
        this.comensales = comensales;
        this.estado = estado;
    }

    // Para creación de un nuevo pedido (sin id)
    public Pedido(String mesa,
                  List<LineaPedido> productos,
                  double total,
                  String camarero,
                  int comensales,
                  String estado) {
        this.mesa = mesa;
        this.productos = productos;
        this.total = total;
        this.camarero = camarero;
        this.comensales = comensales;
        this.estado = estado;
    }

    public int getId() { return id; }
    public String getMesa() { return mesa; }
    public List<LineaPedido> getProductos() { return productos; }
    public double getTotal() { return total; }
    public String getCamarero() { return camarero; }
    public int getComensales() { return comensales; }
    public String getEstado() { return estado; }

    public void setId(int id) { this.id = id; }
    public void setMesa(String mesa) { this.mesa = mesa; }
    public void setProductos(List<LineaPedido> productos) { this.productos = productos; }
    public void setTotal(double total) { this.total = total; }
    public void setCamarero(String camarero) { this.camarero = camarero; }
    public void setComensales(int comensales) { this.comensales = comensales; }
    public void setEstado(String estado) { this.estado = estado; }
}
