package app.service;

import app.domain.Cliente;
import app.domain.DetallePedido;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.enums.EstadoPedido;
import app.repository.PedidoRepository;
import app.validators.PedidoValidator;

import java.math.BigDecimal;

public class PedidoService {

    private final PedidoRepository pedidoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
    }

    public Pedido crearPedido(int id, Cliente cliente) {
        Pedido pedido = new Pedido(id, cliente);
        if (!PedidoValidator.validarPedido(pedido) || pedidoRepository.buscarPorId(id) != null) {
            return null;
        }
        pedidoRepository.guardar(pedido);
        return pedido;
    }

    public Pedido buscarPorId(int id) {
        return pedidoRepository.buscarPorId(id);
    }

    public boolean terminarPedido(int pedidoId) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null || !pedido.terminar()) {
            return false;
        }
        pedidoRepository.guardar(pedido);
        return true;
    }

    public boolean agregarProducto(int pedidoId, Producto producto, int cantidad) {
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);
        if (pedido == null
                || pedido.getEstado() != EstadoPedido.EN_PROCESO
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
                || pedido.getEstado() != EstadoPedido.EN_PROCESO
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
                || pedido.getEstado() != EstadoPedido.EN_PROCESO
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


}
