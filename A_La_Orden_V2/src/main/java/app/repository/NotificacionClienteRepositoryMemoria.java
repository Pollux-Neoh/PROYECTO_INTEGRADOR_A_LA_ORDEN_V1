package app.repository;

import app.domain.NotificacionCliente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionClienteRepositoryMemoria
        implements NotificacionClienteRepository {

    private final List<NotificacionCliente> notificaciones =
            new ArrayList<>();

    @Override
    public void guardar(NotificacionCliente notificacion) {
        if (notificacion == null) {
            throw new IllegalArgumentException(
                    "La notificación no puede ser nula."
            );
        }

        notificaciones.add(notificacion);
    }

    @Override
    public List<NotificacionCliente> listar() {
        return Collections.unmodifiableList(
                new ArrayList<>(notificaciones)
        );
    }
}
