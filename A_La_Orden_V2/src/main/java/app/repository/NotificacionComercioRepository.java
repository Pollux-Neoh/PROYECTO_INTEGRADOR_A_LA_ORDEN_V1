package app.repository;

import app.domain.NotificacionComercio;
import java.util.List;

public interface NotificacionComercioRepository {

    void guardar(NotificacionComercio notificacion);

    List<NotificacionComercio> listar();
}