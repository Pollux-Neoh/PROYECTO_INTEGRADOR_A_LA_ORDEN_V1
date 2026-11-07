package app.service;

import app.domain.NotificacionComercio;
import app.domain.NotificacionCliente;
import app.domain.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificacionService {

    private final List<NotificacionComercio> notificaciones;
    private final List<NotificacionCliente> notificacionesCliente;

    public NotificacionService() {
        this.notificaciones = new ArrayList<>();
        this.notificacionesCliente = new ArrayList<>();
    }

    public void notificarPedidoConfirmado(Pedido pedido) {
        String mensaje = "El pedido " + pedido.getId() + " fue confirmado. Total: " + pedido.getSubtotal();

        notificaciones.add(new NotificacionComercio(
                pedido.getRestaurante().getId(),
                pedido.getId(),
                mensaje
        ));
    }

    // Informa al cliente el tiempo estimado de preparación.
    public void notificarTiempoEstimado(Pedido pedido) {

        String mensaje = "Tu pedido " + pedido.getId()
                + " tiene un tiempo estimado de preparación de "
                + pedido.getTiempoEstimadoMinutos() + " minutos.";

        notificacionesCliente.add(new NotificacionCliente(
                pedido.getCliente().getId(),
                pedido.getId(),
                mensaje
        ));
    }

    // Informa al cliente que su pedido está listo para recoger.
    public void notificarPedidoListo(Pedido pedido) {

        String mensaje = "Tu pedido " + pedido.getId()
                + " está listo para recoger.";

        notificacionesCliente.add(new NotificacionCliente(
                pedido.getCliente().getId(),
                pedido.getId(),
                mensaje
        ));
    }

    public List<NotificacionComercio> listarNotificaciones() {
        return Collections.unmodifiableList(new ArrayList<>(notificaciones));
    }

    public List<NotificacionCliente> listarNotificacionesCliente() {
        return Collections.unmodifiableList(new ArrayList<>(notificacionesCliente));
    }

}
