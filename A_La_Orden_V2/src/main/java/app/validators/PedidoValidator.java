package app.validators;

import app.domain.Pedido;
import app.domain.Producto;
import app.domain.DetallePedido;

import java.math.BigDecimal;

public class PedidoValidator {

    private PedidoValidator() {
    }

    public static boolean validarPedido(Pedido pedido) {
        return pedido != null && pedido.getId() > 0 && pedido.getCliente() != null;
    }

    public static boolean validarConfirmacion(Pedido pedido) {
        if (!(validarPedido(pedido)
                && pedido.getRestaurante() != null
                && pedido.getRestaurante().getId() > 0
                && validarNombreRestaurante(pedido.getRestaurante())
                && !pedido.getDetalles().isEmpty())) {
            return false;
        }
        for (DetallePedido detalle : pedido.getDetalles()) {
            if (!validarProducto(detalle.getProducto()) || !validarCantidad(detalle.getCantidad())) {
                return false;
            }
        }
        return true;
    }

    public static boolean validarNombreRestaurante(Restaurante restaurante) {
        return restaurante != null
                && restaurante.getNombre() != null
                && !restaurante.getNombre().trim().isEmpty();
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
