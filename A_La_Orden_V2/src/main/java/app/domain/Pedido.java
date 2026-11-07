package app.domain;

import app.domain.enums.EstadoPedido;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Pedido {

    private final int id;
    private final Cliente cliente;
    private final Restaurante restaurante;
    private final Map<Integer, DetallePedido> detalles;
    private EstadoPedido estado;
    private Integer tiempoEstimadoMinutos; //es integer porque inicialmente el pedido no tendra un tiempo sera un null
    private String codigoQr;

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

    public Integer getTiempoEstimadoMinutos() {
        return tiempoEstimadoMinutos;
    }

    public String getCodigoQr() {return codigoQr;}








    //Permite guardar el tiempo calculado y el ajustado por el comerciante, no iguales o inf a 0
    public boolean actualizarTiempoEstimado(int minutos) {
        if (minutos <= 0) {
            return false;
        }

        this.tiempoEstimadoMinutos = minutos;
        return true;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(new ArrayList<>(detalles.values()));
    }

    public boolean terminar() {
        if (estado != EstadoPedido.EN_PROCESO) {
            return false;
        }
        estado = EstadoPedido.TERMINADO;
        return true;
    }

    public boolean aceptar() {
        if (estado != EstadoPedido.CONFIRMADO) {
            return false;
        }

        estado = EstadoPedido.EN_PROCESO;
        return true;
    }

    public boolean rechazar() {
        if (estado != EstadoPedido.CONFIRMADO) {
            return false;
        }

        estado = EstadoPedido.RECHAZADO;
        return true;
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
        codigoQr = "QR-" + id + "-"
                + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        generarCodigoQr();

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

    // Generación del código QR.
    private void generarCodigoQr() {
        this.codigoQr = "PEDIDO-" + id + "-"
                + java.util.UUID.randomUUID();
    }

    // Marcar como entregado mediante validación QR.
    public boolean marcarComoEntregado(String codigoQrEscaneado) {

        if (estado != EstadoPedido.LISTO
                || codigoQr == null
                || !codigoQr.equals(codigoQrEscaneado)) {
            return false;
        }

        estado = EstadoPedido.ENTREGADO;
        return true;
    }

    //Marcar como Listo
    public void marcarComoListo() {
        if (estado != EstadoPedido.TERMINADO) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como listos los pedidos terminados."
            );
        }

        estado = EstadoPedido.LISTO;
    }

}
