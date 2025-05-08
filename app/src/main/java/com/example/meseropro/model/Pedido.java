package com.example.meseropro.model;

import java.util.List;

public class Pedido {
    private String mesa; // número en texto, ej: "Mesa 4"
    private List<LineaPedido> productos;
    private double total;
    private String camarero;
    private int comensales;

    public Pedido(String mesa, List<LineaPedido> productos, double total, String camarero, int comensales) {
        this.mesa = mesa;
        this.productos = productos;
        this.total = total;
        this.camarero = camarero;
        this.comensales = comensales;
    }

    public String getMesa() { return mesa; }
    public List<LineaPedido> getProductos() { return productos; }
    public double getTotal() { return total; }
    public String getCamarero() { return camarero; }
    public int getComensales() { return comensales; }
}
