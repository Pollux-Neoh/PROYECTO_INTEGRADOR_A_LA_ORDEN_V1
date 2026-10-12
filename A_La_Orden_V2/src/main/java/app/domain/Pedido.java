package app.domain;

import app.domain.enums.EstadoPedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class Pedido {

    private final int id;
    private final Cliente cliente;
    private final Restaurante restaurante;
    private final Map<Integer, DetallePedido> detalles;
    private EstadoPedido estado;

    public Pedido(int id, Cliente cliente) {
        this(id, cliente, null);
    }

    public Pedido(int id, Cliente cliente, Restaurante restaurante) {
        this.id = id;
        this.cliente = cliente;
        this.restaurante = restaurante;
        this.detalles = new LinkedHashMap<>();
        this.estado = EstadoPedido.PENDIENTE_CONFIRMACION;
    }

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(new ArrayList<>(detalles.values()));
    }

    public void agregarProducto(Producto producto, int cantidad) {
        validarPendiente();
        if (producto == null || cantidad <= 0) {
            throw new IllegalArgumentException("El producto y una cantidad positiva son obligatorios.");
        }
        DetallePedido detalleExistente = detalles.get(producto.getId());
        if (detalleExistente == null) {
            detalles.put(producto.getId(), new DetallePedido(producto, cantidad));
        } else {
            if (cantidad > Integer.MAX_VALUE - detalleExistente.getCantidad()) {
                throw new IllegalArgumentException("La cantidad total supera el límite permitido.");
            }
            detalleExistente.setCantidad(detalleExistente.getCantidad() + cantidad);
        }
    }

    public boolean actualizarCantidad(int productoId, int cantidad) {
        validarPendiente();
        if (cantidad <= 0) {
            return false;
        }
        DetallePedido detalle = detalles.get(productoId);
        if (detalle == null) {
            return false;
        }
        detalle.setCantidad(cantidad);
        return true;
    }

    public boolean quitarProducto(int productoId) {
        validarPendiente();
        return detalles.remove(productoId) != null;
    }

    public boolean confirmar() {
        if (estado != EstadoPedido.PENDIENTE_CONFIRMACION
                || restaurante == null
                || detalles.isEmpty()) {
            return false;
        }
        estado = EstadoPedido.CONFIRMADO;
        return true;
    }

    public BigDecimal getSubtotal() {
        BigDecimal subtotal = BigDecimal.ZERO;
        for (DetallePedido detalle : detalles.values()) {
            subtotal = subtotal.add(detalle.getSubtotal());
        }
        return subtotal;
    }

    private void validarPendiente() {
        if (estado != EstadoPedido.PENDIENTE_CONFIRMACION) {
            throw new IllegalStateException("No se puede modificar un pedido confirmado.");
        }
    }
}
