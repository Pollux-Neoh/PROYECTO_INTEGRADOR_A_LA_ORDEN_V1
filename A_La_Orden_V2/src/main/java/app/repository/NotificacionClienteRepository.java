package app.repository;

import app.domain.NotificacionCliente;
import java.util.List;

public interface NotificacionClienteRepository {

    void guardar(NotificacionCliente notificacion);

    List<NotificacionCliente> listar();
}
