package com.example.meseropro.model;

public class Mesa {
    private int id; // 🔹 Añadir este campo
    private String estado;
    private int comensales;
    private int numero;

    public Mesa(int numero, String estado, int comensales) {
        this.numero = numero;
        this.estado = estado;
        this.comensales = comensales;
    }

    // 🔹 Añadir este constructor completo si se usa deserialización
    public Mesa(int id, int numero, String estado, int comensales) {
        this.id = id;
        this.numero = numero;
        this.estado = estado;
        this.comensales = comensales;
    }

    public int getId() { return id; } // 🔹 Este es el que falta
    public int getNumero() { return numero; }
    public String getEstado() { return estado; }
    public int getComensales() { return comensales; }
}
