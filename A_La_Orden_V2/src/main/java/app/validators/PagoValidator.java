package app.validators;

import app.domain.Pedido;
import app.domain.enums.EstadoPedido;
import app.domain.enums.MetodoPago;

import java.math.BigDecimal;


//Centraliza las reglas de validación relacionadas con los pagos.
public class PagoValidator {

    // Evita crear objetos de esta clase.
    private PagoValidator() {
    }

    // Comprueba si un pedido cumple las condiciones para registrar un pago.
    public static boolean validarPedidoParaPago(Pedido pedido) {

        return pedido != null
                && PedidoValidator.validarPedido(pedido)
                && pedido.getEstado() == EstadoPedido.CONFIRMADO
                && pedido.getSubtotal() != null
                && pedido.getSubtotal().compareTo(BigDecimal.ZERO) > 0;
    }

    // Comprueba que se haya seleccionado un mét de pago.
    public static boolean validarMetodoPago(MetodoPago metodoPago) {
        return metodoPago != null;
    }

    // Comprueba que el monto corresponda al valor actual del pedido.
    public static boolean validarMonto(Pedido pedido, BigDecimal monto) {

        return pedido != null
                && pedido.getSubtotal() != null
                && monto != null
                && monto.compareTo(BigDecimal.ZERO) > 0
                && monto.compareTo(pedido.getSubtotal()) == 0;
    }
}
