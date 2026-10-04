package app.validators;

import app.domain.Pedido;
import app.domain.Producto;

import java.math.BigDecimal;

public class PedidoValidator {

    private PedidoValidator() {
    }

    public static boolean validarPedido(Pedido pedido) {
        return pedido != null && pedido.getId() > 0 && pedido.getCliente() != null;
    }

    public static boolean validarProducto(Producto producto) {
        return producto != null
                && producto.getId() > 0
                && producto.getNombre() != null
                && !producto.getNombre().trim().isEmpty()
                && producto.getPrecio() != null
                && producto.getPrecio().compareTo(BigDecimal.ZERO) > 0
                && producto.isDisponible();
    }

    public static boolean validarCantidad(int cantidad) {
        return cantidad > 0;
    }
}
