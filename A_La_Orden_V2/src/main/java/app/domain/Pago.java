package app.domain;

import app.domain.enums.EstadoPago;
import app.domain.enums.MetodoPago;

import java.math.BigDecimal;


// Representa un intento de pago asociado a un pedido.
public class Pago {

    private final int id;
    private final Pedido pedido;
    private final MetodoPago metodoPago;
    private final BigDecimal monto;

    private EstadoPago estado;

    // Constructor: crea un pago inicialmente pendiente.
    public Pago(int id, Pedido pedido, MetodoPago metodoPago, BigDecimal monto) {

        if (id <= 0) {
            throw new IllegalArgumentException("El ID del pago debe ser positivo");
        }

        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio");
        }

        if (metodoPago == null) {
            throw new IllegalArgumentException("El metodo de pago es obligatorio");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        this.id = id;
        this.pedido = pedido;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = EstadoPago.PENDIENTE;
    }

    // Consultan la información del pago.
    public int getId() {
        return id;
    }
    public Pedido getPedido() {
        return pedido;
    }
    public MetodoPago getMetodoPago() {
        return metodoPago;
    }
    public BigDecimal getMonto() {
        return monto;
    }
    public EstadoPago getEstado() {
        return estado;
    }

    // Cambia el estado del pago cuando se confirma.
    public boolean confirmar() {

        if (estado != EstadoPago.PENDIENTE) {
            return false;
        }

        estado = EstadoPago.CONFIRMADO;
        return true;
    }

    // Cancela el pago únicamente si todavía está pendiente.
    public boolean cancelar(){

        // No permitimos cancelar un pago confirmado o ya cancelado.
        if (estado != EstadoPago.PENDIENTE) {
            return false;
        }

        // Actualizamos el estado del pago.
        estado = EstadoPago.CANCELADO;
        return true;
    }
}
