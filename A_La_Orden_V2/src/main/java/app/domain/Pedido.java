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
    private Integer tiempoEstimadoMinutos;
    private String codigoQr;

    public Pedido(int id, Cliente cliente) {
        this(id, cliente, null);
    }

    public Pedido(int id, Cliente cliente, Restaurante restaurante) {
        this(id, cliente, restaurante, false);
    }

    // Constructor interno para permitir temporalmente el ID 0.
    private Pedido(
            int id,
            Cliente cliente,
            Restaurante restaurante,
            boolean permitirIdTemporal
    ) {

        if (permitirIdTemporal) {
            if (id != 0) {
                throw new IllegalArgumentException(
                        "El identificador temporal debe ser 0."
                );
            }
        } else if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del pedido debe ser mayor que cero."
            );
        }

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        this.id = id;
        this.cliente = cliente;
        this.restaurante = restaurante;
        this.detalles = new LinkedHashMap<>();
        this.estado = EstadoPedido.PENDIENTE_CONFIRMACION;
    }

    // Crea un pedido nuevo que todavía no tiene ID en la base de datos.
    public static Pedido crearNuevo(
            Cliente cliente,
            Restaurante restaurante
    ) {
        return new Pedido(0, cliente, restaurante, true);
    }

    // Devuelve una copia del pedido con el ID generado por MySQL.
    public Pedido conIdentificadorGenerado(int idGenerado) {

        if (this.id != 0) {
            throw new IllegalStateException(
                    "El pedido ya tiene un identificador asignado."
            );
        }

        if (idGenerado <= 0) {
            throw new IllegalArgumentException(
                    "El ID generado debe ser mayor que cero."
            );
        }

        return restaurar(
                idGenerado,
                cliente,
                restaurante,
                estado,
                tiempoEstimadoMinutos,
                codigoQr,
                getDetalles()
        );
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

    public String getCodigoQr() {
        return codigoQr;
    }

    // Reconstruye un pedido sin modificar su estado ni generar otro QR.
    public static Pedido restaurar(
            int id,
            Cliente cliente,
            Restaurante restaurante,
            EstadoPedido estado,
            Integer tiempoEstimadoMinutos,
            String codigoQr,
            List<DetallePedido> detallesRecuperados
    ) {

        if (id <= 0 || cliente == null || estado == null) {
            throw new IllegalArgumentException(
                    "Los datos principales del pedido son inválidos."
            );
        }

        if (tiempoEstimadoMinutos != null
                && tiempoEstimadoMinutos <= 0) {
            throw new IllegalArgumentException(
                    "El tiempo estimado debe ser positivo."
            );
        }

        Pedido pedido = new Pedido(id, cliente, restaurante);

        pedido.estado = estado;
        pedido.tiempoEstimadoMinutos = tiempoEstimadoMinutos;
        pedido.codigoQr = codigoQr;

        if (detallesRecuperados != null) {

            for (DetallePedido detalle : detallesRecuperados) {

                if (detalle == null || detalle.getProducto() == null) {
                    throw new IllegalArgumentException(
                            "El detalle del pedido no es válido."
                    );
                }

                int productoId = detalle.getProducto().getId();

                if (pedido.detalles.containsKey(productoId)) {
                    throw new IllegalArgumentException(
                            "El pedido contiene productos duplicados."
                    );
                }

                pedido.detalles.put(productoId, detalle);
            }
        }

        return pedido;
    }

    public boolean actualizarTiempoEstimado(int minutos) {

        if (minutos <= 0) {
            return false;
        }

        this.tiempoEstimadoMinutos = minutos;
        return true;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(
                new ArrayList<>(detalles.values())
        );
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
            throw new IllegalArgumentException(
                    "El producto y una cantidad positiva son obligatorios."
            );
        }

        DetallePedido detalleExistente =
                detalles.get(producto.getId());

        if (detalleExistente == null) {

            detalles.put(
                    producto.getId(),
                    new DetallePedido(producto, cantidad)
            );

        } else {

            if (cantidad > Integer.MAX_VALUE
                    - detalleExistente.getCantidad()) {
                throw new IllegalArgumentException(
                        "La cantidad total supera el límite permitido."
                );
            }

            detalleExistente.setCantidad(
                    detalleExistente.getCantidad() + cantidad
            );
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
            throw new IllegalStateException(
                    "No se puede modificar un pedido confirmado."
            );
        }
    }

    private void generarCodigoQr() {

        this.codigoQr = "PEDIDO-" + id + "-"
                + UUID.randomUUID();
    }

    public boolean marcarComoEntregado(String codigoQrEscaneado) {

        if (estado != EstadoPedido.LISTO
                || codigoQr == null
                || !codigoQr.equals(codigoQrEscaneado)) {
            return false;
        }

        estado = EstadoPedido.ENTREGADO;
        return true;
    }

    public void marcarComoListo() {

        if (estado != EstadoPedido.TERMINADO) {
            throw new IllegalStateException(
                    "Solo se pueden marcar como listos los pedidos terminados."
            );
        }

        estado = EstadoPedido.LISTO;
    }
}
