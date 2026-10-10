package app.service;

import app.domain.NotificacionComercio;
import app.domain.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionService {

    private final List<NotificacionComercio> notificaciones;

    public NotificacionService() {
        this.notificaciones = new ArrayList<>();
    }

    public void notificarPedidoConfirmado(Pedido pedido) {
        String mensaje = "El pedido " + pedido.getId() + " fue confirmado. Total: " + pedido.getSubtotal();
        notificaciones.add(new NotificacionComercio(
                pedido.getRestaurante().getId(),
                pedido.getId(),
                mensaje
        ));
    }

    public List<NotificacionComercio> listarNotificaciones() {
        return Collections.unmodifiableList(new ArrayList<>(notificaciones));
    }
}
