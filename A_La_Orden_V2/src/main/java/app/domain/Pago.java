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

    // Constructor para reconstruir o crear un pago con ID existente.
    public Pago(int id, Pedido pedido, MetodoPago metodoPago, BigDecimal monto) {
        this(validarIdExistente(id), pedido, metodoPago, monto, EstadoPago.PENDIENTE);
    }

    // Constructor privado que permite crear pagos nuevos con ID 0.
    private Pago(
            int id,
            Pedido pedido,
            MetodoPago metodoPago,
            BigDecimal monto,
            EstadoPago estado) {

        if (id < 0) {
            throw new IllegalArgumentException("El ID del pago no puede ser negativo.");
        }

        if (pedido == null) {
            throw new IllegalArgumentException("El pedido es obligatorio.");
        }

        if (metodoPago == null) {
            throw new IllegalArgumentException("El metodo de pago es obligatorio.");
        }

        if (monto == null || monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero.");
        }

        if (estado == null) {
            throw new IllegalArgumentException("El estado del pago es obligatorio.");
        }

        this.id = id;
        this.pedido = pedido;
        this.metodoPago = metodoPago;
        this.monto = monto;
        this.estado = estado;
    }

    // Evita que el constructor público reciba un ID igual o menor que cero.
    private static int validarIdExistente(int id) {
        if (id <= 0) {
            throw new IllegalArgumentException("El ID del pago debe ser positivo.");
        }
        return id;
    }

    // Crea un pago nuevo pendiente, todavía sin ID asignado.
    public static Pago crearNuevo(
            Pedido pedido,
            MetodoPago metodoPago,
            BigDecimal monto) {

        return new Pago(0, pedido, metodoPago, monto, EstadoPago.PENDIENTE);
    }

    // Devuelve una nueva instancia con el ID generado por el repositorio.
    public Pago conIdentificadorGenerado(int id) {

        if (this.id != 0) {
            throw new IllegalStateException(
                    "El pago ya tiene un identificador asignado."
            );
        }

        if (id <= 0) {
            throw new IllegalArgumentException("El ID generado debe ser positivo.");
        }

        return restaurar(id, pedido, metodoPago, monto, estado);
    }

    // Reconstruye un pago recuperado de MySQL.
    public static Pago restaurar(
            int id,
            Pedido pedido,
            MetodoPago metodoPago,
            BigDecimal monto,
            EstadoPago estado) {

        validarIdExistente(id);
        return new Pago(id, pedido, metodoPago, monto, estado);
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
    public boolean cancelar() {

        if (estado != EstadoPago.PENDIENTE) {
            return false;
        }

        estado = EstadoPago.CANCELADO;
        return true;
    }
}
