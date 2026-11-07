package app.service;

import app.domain.Cliente;
import app.domain.DetallePedido;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.enums.EstadoPedido;
import app.service.Inputport.PedidoInput;
import app.service.OutputPort.PedidoOutport;
import app.validators.PedidoValidator;
import app.domain.Restaurante;

import java.math.BigDecimal;
import java.util.Optional;

public class PedidoService implements PedidoInput {

    private final PedidoOutport pedidoRepository;
    private final NotificacionService notificacionService;

    public PedidoService(PedidoOutport pedidoRepository) {
        this(pedidoRepository, new NotificacionService());
    }

    public PedidoService(PedidoOutport pedidoRepository, NotificacionService notificacionService) {
        this.pedidoRepository = pedidoRepository;
        this.notificacionService = notificacionService;
    }

    public Pedido crearPedido(int id, Cliente cliente) {
        return crearPedido(id, cliente, null);
    }

    public Pedido crearPedido(int id, Cliente cliente, Restaurante restaurante) {
        Pedido pedido = new Pedido(id, cliente, restaurante);
        if (!PedidoValidator.validarPedido(pedido) || buscarPedido(id) != null) {
            return null;
        }
        pedidoRepository.guardar(pedido);
        return pedido;
    }

    public Pedido buscarPorId(int id) {
        return buscarPedido(id);
    }

    public boolean agregarProducto(int pedidoId, Producto producto, int cantidad) {
        Pedido pedido = buscarPedido(pedidoId);
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
        Pedido pedido = buscarPedido(pedidoId);
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
        Pedido pedido = buscarPedido(pedidoId);
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION
                || !pedido.quitarProducto(productoId)) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        return true;
    }

    public BigDecimal obtenerSubtotal(int pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
        return pedido == null ? null : pedido.getSubtotal();
    }

    public boolean confirmarPedido(int pedidoId) {
        Pedido pedido = buscarPedido(pedidoId);
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

    @Override
    public String obtenerQr(String idPedido) {
        if (idPedido == null) {
            return null;
        }
        return pedidoRepository.buscarPorId(idPedido)
                .filter(pedido -> pedido.getEstado() == EstadoPedido.CONFIRMADO)
                .map(Pedido::getCodigoQr)
                .orElse(null);
    }

    private Pedido buscarPedido(int id) {
        Optional<Pedido> pedido = pedidoRepository.buscarPorId(String.valueOf(id));
        return pedido.orElse(null);
    }
}
