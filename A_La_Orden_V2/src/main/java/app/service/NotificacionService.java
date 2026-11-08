package app.service;

import app.domain.NotificacionComercio;
import app.domain.NotificacionCliente;
import app.domain.Pedido;
import app.repository.NotificacionComercioRepository;
import app.repository.NotificacionClienteRepository;

import java.util.List;

public class NotificacionService {

    private final NotificacionComercioRepository comercioRepository;
    private final NotificacionClienteRepository clienteRepository;

    public NotificacionService(
            NotificacionComercioRepository comercioRepository,
            NotificacionClienteRepository clienteRepository) {

        if (comercioRepository == null || clienteRepository == null) {
            throw new IllegalArgumentException(
                    "Los repositorios son obligatorios."
            );
        }

        this.comercioRepository = comercioRepository;
        this.clienteRepository = clienteRepository;
    }

    // Notifica al comercio que un pedido fue confirmado.
    public void notificarPedidoConfirmado(Pedido pedido) {

        if (pedido == null || pedido.getRestaurante() == null) {
            return;
        }

        String mensaje = "El pedido " + pedido.getId()
                + " fue confirmado. Total: " + pedido.getSubtotal();

        comercioRepository.guardar(
                new NotificacionComercio(
                        pedido.getRestaurante().getId(),
                        pedido.getId(),
                        mensaje
                )
        );
    }

    // Informa al cliente el tiempo estimado de preparación.
    public void notificarTiempoEstimado(Pedido pedido) {

        if (pedido == null || pedido.getCliente() == null) {
            return;
        }

        String mensaje = "Tu pedido " + pedido.getId()
                + " tiene un tiempo estimado de preparación de "
                + pedido.getTiempoEstimadoMinutos() + " minutos.";

        clienteRepository.guardar(
                new NotificacionCliente(
                        pedido.getCliente().getId(),
                        pedido.getId(),
                        mensaje
                )
        );
    }

    // Informa al cliente que su pedido está listo para recoger.
    public void notificarPedidoListo(Pedido pedido) {

        if (pedido == null || pedido.getCliente() == null) {
            return;
        }

        String mensaje = "Tu pedido " + pedido.getId()
                + " está listo para recoger.";

        clienteRepository.guardar(
                new NotificacionCliente(
                        pedido.getCliente().getId(),
                        pedido.getId(),
                        mensaje
                )
        );
    }

    // Consulta las notificaciones del comercio.
    public List<NotificacionComercio> listarNotificaciones() {
        return comercioRepository.listar();
    }

    // Consulta las notificaciones de los clientes.
    public List<NotificacionCliente> listarNotificacionesCliente() {
        return clienteRepository.listar();
    }
}
