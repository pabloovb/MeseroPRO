// src/main/java/com/example/meseropro/notifications/GestorNotificaciones.java
package com.example.meseropro.Notifications;

import com.example.meseropro.model.Notificacion;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GestorNotificaciones {
    private static final GestorNotificaciones INST = new GestorNotificaciones();
    private final List<Notificacion> lista = new ArrayList<>();

    private GestorNotificaciones(){}

    public static GestorNotificaciones getInstance() {
        return INST;
    }

    public void agregar(Notificacion n) {
        lista.add(0, n);
    }

    public List<Notificacion> obtenerTodas() {
        return Collections.unmodifiableList(lista);
    }

    public void borrarTodas() {
        lista.clear();
    }
}

