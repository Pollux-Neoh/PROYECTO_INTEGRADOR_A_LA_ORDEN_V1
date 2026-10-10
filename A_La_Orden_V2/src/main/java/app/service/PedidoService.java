package app.service;

import app.domain.Cliente;
import app.domain.DetallePedido;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.Restaurante;
import app.domain.enums.EstadoPedido;
import app.repository.PedidoRepository;
import app.validators.PedidoValidator;

import java.math.BigDecimal;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final NotificacionService notificacionService;

    public PedidoService(PedidoRepository pedidoRepository) {
        this(pedidoRepository, new NotificacionService());
    }

    public PedidoService(PedidoRepository pedidoRepository, NotificacionService notificacionService) {
        this.pedidoRepository = pedidoRepository;
        this.notificacionService = notificacionService;
    }

    public Pedido crearPedido(int id, Cliente cliente) {
        return crearPedido(id, cliente, null);
    }

    public Pedido crearPedido(int id, Cliente cliente, Restaurante restaurante) {
        Pedido pedido = new Pedido(id, cliente, restaurante);
        if (!PedidoValidator.validarPedido(pedido) || pedidoRepository.buscarPorId(id) != null) {
            return null;
        }
        pedidoRepository.guardar(pedido);
        return pedido;
    }

    public Pedido buscarPorId(int id) {
        return pedidoRepository.buscarPorId(id);
    }

    public boolean agregarProducto(int pedidoId, Producto producto, int cantidad) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION
                || !PedidoValidator.validarProducto(producto)
                || !PedidoValidator.validarCantidad(cantidad)) {
            return false;
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getProducto().getId() == producto.getId()
                    && cantidad > Integer.MAX_VALUE - detalle.getCantidad()) {
                return false;
            }
        }
        pedido.agregarProducto(producto, cantidad);
        pedidoRepository.guardar(pedido);
        return true;
    }

    public boolean actualizarCantidad(int pedidoId, int productoId, int cantidad) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION
                || !PedidoValidator.validarCantidad(cantidad)) {
            return false;
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (detalle.getProducto().getId() == productoId) {
                if (cantidad > detalle.getCantidad()
                        && !PedidoValidator.validarProducto(detalle.getProducto())) {
                    return false;
                }
                break;
            }
        }
        if (!pedido.actualizarCantidad(productoId, cantidad)) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        return true;
    }

    public boolean quitarProducto(int pedidoId, int productoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION
                || !pedido.quitarProducto(productoId)) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        return true;
    }

    public BigDecimal obtenerSubtotal(int pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        return pedido == null ? null : pedido.getSubtotal();
    }

    public boolean confirmarPedido(int pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (!PedidoValidator.validarConfirmacion(pedido) || !pedido.confirmar()) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        notificacionService.notificarPedidoConfirmado(pedido);
        return true;
    }

    public NotificacionService getNotificacionService() {
        return notificacionService;
    }
}
