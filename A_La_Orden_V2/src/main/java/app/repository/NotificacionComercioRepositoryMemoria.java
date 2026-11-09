package app.repository;

import app.domain.NotificacionComercio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionComercioRepositoryMemoria
        implements NotificacionComercioRepository {

    private final List<NotificacionComercio> notificaciones =
            new ArrayList<>();

    @Override
    public void guardar(NotificacionComercio notificacion) {
        if (notificacion == null) {
            throw new IllegalArgumentException(
                    "La notificación no puede ser nula."
            );
        }

        notificaciones.add(notificacion);
    }

    @Override
    public List<NotificacionComercio> listar() {
        return Collections.unmodifiableList(
                new ArrayList<>(notificaciones)
        );
    }
}
