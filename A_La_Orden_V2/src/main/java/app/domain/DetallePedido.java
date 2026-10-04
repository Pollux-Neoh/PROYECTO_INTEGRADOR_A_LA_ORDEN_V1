package app.domain;

import java.math.BigDecimal;

public class DetallePedido {

    private final Producto producto;
    private int cantidad;

    DetallePedido(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = cantidad;
    }

    public Producto getProducto() {
        return producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public BigDecimal getSubtotal() {
        return producto.getPrecio().multiply(BigDecimal.valueOf(cantidad));
    }

    void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
}
