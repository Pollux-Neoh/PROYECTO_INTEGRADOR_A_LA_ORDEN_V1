package app.validators;

import app.domain.Pago;
import app.domain.Pedido;
import app.domain.enums.EstadoPago;
import app.domain.enums.EstadoPedido;

import java.math.BigDecimal;

// Contiene las validaciones necesarias para generar una factura.
public class FacturaValidator {

    // Comprueba si el pago puede generar una factura.
    public static boolean validarPagoParaFacturar(Pago pago){

        return pago != null
                && pago.getEstado() == EstadoPago.CONFIRMADO
                && pago.getPedido() != null;
    }

    // Comprueba si el pedido puede generar una factura.
    public static boolean validarPedidoParaFacturar(Pedido pedido){

        return pedido != null
                && pedido.getEstado() == EstadoPedido.CONFIRMADO
                && !pedido.getDetalles().isEmpty();
    }

    // Comprueba que el monto del pago coincida con el total de la factura.
    public static boolean validarMonto(
            BigDecimal montoPago,
            BigDecimal totalFactura) {

        return montoPago != null
                && totalFactura != null
                && montoPago.compareTo(totalFactura) == 0
                && totalFactura.compareTo(BigDecimal.ZERO) > 0;
    }
}
