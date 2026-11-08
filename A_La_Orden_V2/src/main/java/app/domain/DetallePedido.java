
package app.domain;

import java.math.BigDecimal;

public class DetallePedido {

    private final Producto producto;
    private final BigDecimal precioUnitario;
    private int cantidad;

    // Constructor utilizado cuando se agrega un producto nuevo al pedido.
    DetallePedido(Producto producto, int cantidad) {
        this(
                producto,
                cantidad,
                producto != null ? producto.getPrecio() : null
        );
    }

    // Constructor privado que permite establecer el precio histórico.
    private DetallePedido(
            Producto producto,
            int cantidad,
            BigDecimal precioUnitario
    ) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto es obligatorio."
            );
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser positiva."
            );
        }

        if (precioUnitario == null || precioUnitario.signum() <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser positivo."
            );
        }

        this.producto = producto;
        this.precioUnitario = precioUnitario;
        this.cantidad = cantidad;
    }

    // Reconstruye detalles recuperados de MySQL.
    public static DetallePedido restaurar(
            Producto producto,
            int cantidad,
            BigDecimal precioUnitario
    ) {
        return new DetallePedido(
                producto,
                cantidad,
                precioUnitario
        );
    }

    public Producto getProducto() {
        return producto;
    }

    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        return precioUnitario.multiply(
                BigDecimal.valueOf(cantidad)
        );
    }

    // Modificación interna de la cantidad.
    void setCantidad(int cantidad) {

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser positiva."
            );
        }

        this.cantidad = cantidad;
    }
}
