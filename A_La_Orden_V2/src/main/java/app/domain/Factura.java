package app.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// Representa la factura generada por un pago confirmado.
public class Factura {

    private final int id;
    private final String numeroFactura;
    private final LocalDateTime fechaGeneracion;

    private final int pedidoId;
    private final int pagoId;

    // Datos históricos del cliente.
    private final int clienteId;
    private final String nombreCliente;
    private final String correoCliente;
    private final String telefonoCliente;
    private final String direccionCliente;

    private final List<DetalleFactura> detalles;
    private final BigDecimal total;

    public Factura(int id, String numeroFactura, LocalDateTime fechaGeneracion,
                   int pedidoId, int pagoId, Cliente cliente,
                   List<DetalleFactura> detalles) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de la factura debe ser positivo."
            );
        }

        if (numeroFactura == null || numeroFactura.isBlank()) {
            throw new IllegalArgumentException(
                    "El número de factura es obligatorio."
            );
        }

        if (fechaGeneracion == null) {
            throw new IllegalArgumentException(
                    "La fecha de generación es obligatoria."
            );
        }

        if (pedidoId <= 0 || pagoId <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores del pedido y pago deben ser positivos."
            );
        }

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente es obligatorio."
            );
        }

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "La factura debe contener al menos un producto."
            );
        }

        this.id = id;
        this.numeroFactura = numeroFactura;
        this.fechaGeneracion = fechaGeneracion;
        this.pedidoId = pedidoId;
        this.pagoId = pagoId;

        this.clienteId = cliente.getId();
        this.nombreCliente = cliente.getNombre();
        this.correoCliente = cliente.getCorreo();
        this.telefonoCliente = cliente.getTelefono();
        this.direccionCliente = cliente.getDireccion();

        this.detalles = Collections.unmodifiableList(
                new ArrayList<>(detalles)
        );

        // Calculamos el total sumando los subtotales.
        BigDecimal acumulado = BigDecimal.ZERO;

        for (DetalleFactura detalle : this.detalles) {
            if (detalle == null) {
                throw new IllegalArgumentException(
                        "La factura no puede contener detalles nulos."
                );
            }

            acumulado = acumulado.add(detalle.getSubtotal());
        }
        this.total = acumulado;
    }
    public int getId() {
        return id;
    }
    public String getNumeroFactura() {
        return numeroFactura;
    }
    public LocalDateTime getFechaGeneracion() {
        return fechaGeneracion;
    }
    public int getPedidoId() {
        return pedidoId;
    }
    public int getPagoId() {
        return pagoId;
    }
    public int getClienteId() {
        return clienteId;
    }
    public String getNombreCliente() {
        return nombreCliente;
    }
    public String getCorreoCLiente() {
        return correoCliente;
    }
    public String getTelefonoCliente() {
        return telefonoCliente;
    }
    public String getDireccionCliente() {
        return direccionCliente;
    }
    public List<DetalleFactura> getDetalles() {
        return detalles;
    }
    public BigDecimal getTotal() {
        return total;
    }
}
