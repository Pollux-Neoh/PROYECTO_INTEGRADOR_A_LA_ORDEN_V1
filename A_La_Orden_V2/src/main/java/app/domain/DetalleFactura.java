package app.domain;


import java.math.BigDecimal;

// Representa un producto incluido en una factura.
public class DetalleFactura {

    private final int productoId;
    private final String nombreProducto;
    private final int cantidad;
    private final BigDecimal precioUnitario;
    private final BigDecimal subtotal;

    // Constructor: conserva los datos históricos del producto.
    public DetalleFactura(int productoId, String nombreProducto,
                          int cantidad, BigDecimal precioUnitario) {


        if (productoId <= 0) {
            throw new IllegalArgumentException(
                    "El ID del producto debe ser positivo."
            );
        }

        if (nombreProducto == null || nombreProducto.isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio."
            );
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException(
                    "La cantidad debe ser mayor que cero"
            );
        }
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "La precio unitario debe ser mayor que cero"
            );
        }

        this.productoId = productoId;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;

        // Calculamos el subtotal del producto.
        this.subtotal = precioUnitario.multiply(BigDecimal.valueOf(cantidad)
        );


    }

    // Reconstruye un detalle de factura recuperado de MySQL.
    public static DetalleFactura restaurar(
            int productoId,
            String nombreProducto,
            int cantidad,
            BigDecimal precioUnitario) {

        return new DetalleFactura(
                productoId,
                nombreProducto,
                cantidad,
                precioUnitario
        );
    }


    public int getProductoId() {
        return productoId;
    }
    public String getNombreProducto() {
        return nombreProducto;
    }
    public int getCantidad() {
        return cantidad;
    }
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    public BigDecimal getSubtotal() {
        return subtotal;
    }
}
