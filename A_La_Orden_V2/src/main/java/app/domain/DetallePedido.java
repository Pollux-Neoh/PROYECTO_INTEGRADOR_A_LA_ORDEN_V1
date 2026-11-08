
package app.domain;

import java.math.BigDecimal;

public class DetallePedido {

    private final Producto producto;
    private final BigDecimal precioUnitario;
    private int cantidad;

    DetallePedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.precioUnitario = producto.getPrecio();
        this.cantidad = cantidad;
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
        return precioUnitario.multiply(BigDecimal.valueOf(cantidad));
    }

    void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
