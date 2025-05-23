package com.example.meseropro.model;

import java.io.Serializable;
import java.util.List;

public class Pedido implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String mesa;
    private List<LineaPedido> productos;
    private double total;
    private String camarero;
    private int comensales;
    private String estado;

    public Pedido() {}

    // Constructor para nuevas inserciones (no incluye id)
    public Pedido(String mesa, List<LineaPedido> productos,
                  double total, String camarero,
                  int comensales, String estado) {
        this.mesa = mesa;
        this.productos = productos;
        this.total = total;
        this.camarero = camarero;
        this.comensales = comensales;
        this.estado = estado;
    }

    // Constructor completo para edición
    public Pedido(Integer id, String mesa, List<LineaPedido> productos,
                  double total, String camarero,
                  int comensales, String estado) {
        this.id = id;
        this.mesa = mesa;
        this.productos = productos;
        this.total = total;
        this.camarero = camarero;
        this.comensales = comensales;
        this.estado = estado;
    }

    // Getters y setters...
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getMesa() { return mesa; }
    public void setMesa(String mesa) { this.mesa = mesa; }
    public List<LineaPedido> getProductos() { return productos; }
    public void setProductos(List<LineaPedido> productos) {
        this.productos = productos;
    }
    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }
    public String getCamarero() { return camarero; }
    public void setCamarero(String camarero) { this.camarero = camarero; }
    public int getComensales() { return comensales; }
    public void setComensales(int comensales) {
        this.comensales = comensales;
    }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}
