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

    // Constructor para facturas que ya tienen identificador.
    public Factura(
            int id,
            String numeroFactura,
            LocalDateTime fechaGeneracion,
            int pedidoId,
            int pagoId,
            Cliente cliente,
            List<DetalleFactura> detalles) {

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID de la factura debe ser positivo."
            );
        }

        if (pedidoId <= 0 || pagoId <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores del pedido y pago deben ser positivos."
            );
        }

        if (fechaGeneracion == null) {
            throw new IllegalArgumentException(
                    "La fecha de generación es obligatoria."
            );
        }

        if (cliente == null || cliente.getId() <= 0) {
            throw new IllegalArgumentException(
                    "El cliente debe ser válido."
            );
        }

        this.id = id;
        this.numeroFactura = validarTextoObligatorio(
                numeroFactura,
                "El número de factura es obligatorio."
        );
        this.fechaGeneracion = fechaGeneracion;
        this.pedidoId = pedidoId;
        this.pagoId = pagoId;

        this.clienteId = cliente.getId();
        this.nombreCliente = validarTextoObligatorio(
                cliente.getNombre(),
                "El nombre del cliente es obligatorio."
        );
        this.correoCliente = normalizarTextoOpcional(cliente.getCorreo());
        this.telefonoCliente = normalizarTextoOpcional(cliente.getTelefono());
        this.direccionCliente = normalizarTextoOpcional(cliente.getDireccion());

        this.detalles = validarYCopiarDetalles(detalles);
        this.total = calcularTotal(this.detalles);
    }

    // Constructor privado para crear una factura pendiente de guardar.
    private Factura(
            LocalDateTime fechaGeneracion,
            int pedidoId,
            int pagoId,
            Cliente cliente,
            List<DetalleFactura> detalles) {

        if (pedidoId <= 0 || pagoId <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores del pedido y pago deben ser positivos."
            );
        }

        if (fechaGeneracion == null) {
            throw new IllegalArgumentException(
                    "La fecha de generación es obligatoria."
            );
        }

        if (cliente == null || cliente.getId() <= 0) {
            throw new IllegalArgumentException(
                    "El cliente debe ser válido."
            );
        }

        // El ID y el número se asignarán al guardar la factura.
        this.id = 0;
        this.numeroFactura = null;

        this.fechaGeneracion = fechaGeneracion;
        this.pedidoId = pedidoId;
        this.pagoId = pagoId;

        this.clienteId = cliente.getId();
        this.nombreCliente = validarTextoObligatorio(
                cliente.getNombre(),
                "El nombre del cliente es obligatorio."
        );
        this.correoCliente = normalizarTextoOpcional(cliente.getCorreo());
        this.telefonoCliente = normalizarTextoOpcional(cliente.getTelefono());
        this.direccionCliente = normalizarTextoOpcional(cliente.getDireccion());

        this.detalles = validarYCopiarDetalles(detalles);
        this.total = calcularTotal(this.detalles);
    }

    // Crea una factura nueva sin asignarle todavía un identificador.
    public static Factura crearNueva(
            LocalDateTime fechaGeneracion,
            int pedidoId,
            int pagoId,
            Cliente cliente,
            List<DetalleFactura> detalles) {

        return new Factura(
                fechaGeneracion,
                pedidoId,
                pagoId,
                cliente,
                detalles
        );
    }

    // Devuelve una nueva factura con el ID generado por el repositorio.
    public Factura conIdentificadorGenerado(int id) {

        if (this.id != 0) {
            throw new IllegalStateException(
                    "La factura ya tiene un identificador asignado."
            );
        }

        if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID generado debe ser positivo."
            );
        }

        String numero = String.format("FAC-%06d", id);

        return restaurar(
                id,
                numero,
                fechaGeneracion,
                pedidoId,
                pagoId,
                clienteId,
                nombreCliente,
                correoCliente,
                telefonoCliente,
                direccionCliente,
                detalles
        );
    }

    // Constructor privado para reconstruir una factura desde MySQL.
    private Factura(
            int id,
            String numeroFactura,
            LocalDateTime fechaGeneracion,
            int pedidoId,
            int pagoId,
            int clienteId,
            String nombreCliente,
            String correoCliente,
            String telefonoCliente,
            String direccionCliente,
            List<DetalleFactura> detalles) {

        if (id <= 0 || pedidoId <= 0 || pagoId <= 0 || clienteId <= 0) {
            throw new IllegalArgumentException(
                    "Los identificadores deben ser positivos."
            );
        }

        if (fechaGeneracion == null) {
            throw new IllegalArgumentException(
                    "La fecha de generación es obligatoria."
            );
        }

        this.id = id;
        this.numeroFactura = validarTextoObligatorio(
                numeroFactura,
                "El número de factura es obligatorio."
        );
        this.fechaGeneracion = fechaGeneracion;
        this.pedidoId = pedidoId;
        this.pagoId = pagoId;

        this.clienteId = clienteId;
        this.nombreCliente = validarTextoObligatorio(
                nombreCliente,
                "El nombre del cliente es obligatorio."
        );
        this.correoCliente = normalizarTextoOpcional(correoCliente);
        this.telefonoCliente = normalizarTextoOpcional(telefonoCliente);
        this.direccionCliente = normalizarTextoOpcional(direccionCliente);

        this.detalles = validarYCopiarDetalles(detalles);
        this.total = calcularTotal(this.detalles);
    }

    // Valida un texto obligatorio y elimina espacios innecesarios.
    private static String validarTextoObligatorio(
            String texto, String mensaje) {

        if (texto == null || texto.isBlank()) {
            throw new IllegalArgumentException(mensaje);
        }

        return texto.trim();
    }

    // Normaliza textos opcionales sin convertir null en una cadena vacía.
    private static String normalizarTextoOpcional(String texto) {
        return texto == null ? null : texto.trim();
    }

    // Valida, copia y protege la lista de detalles.
    private static List<DetalleFactura> validarYCopiarDetalles(
            List<DetalleFactura> detalles) {

        if (detalles == null || detalles.isEmpty()) {
            throw new IllegalArgumentException(
                    "La factura debe contener al menos un producto."
            );
        }

        for (DetalleFactura detalle : detalles) {
            if (detalle == null) {
                throw new IllegalArgumentException(
                        "La factura no puede contener detalles nulos."
                );
            }
        }

        return Collections.unmodifiableList(
                new ArrayList<>(detalles)
        );
    }

    // Calcula el total sumando los subtotales de cada producto.
    private static BigDecimal calcularTotal(List<DetalleFactura> detalles) {

        BigDecimal acumulado = BigDecimal.ZERO;

        for (DetalleFactura detalle : detalles) {
            acumulado = acumulado.add(detalle.getSubtotal());
        }

        return acumulado;
    }

    // Reconstruye una factura recuperada de MySQL.
    public static Factura restaurar(
            int id,
            String numeroFactura,
            LocalDateTime fechaGeneracion,
            int pedidoId,
            int pagoId,
            int clienteId,
            String nombreCliente,
            String correoCliente,
            String telefonoCliente,
            String direccionCliente,
            List<DetalleFactura> detalles) {

        return new Factura(
                id,
                numeroFactura,
                fechaGeneracion,
                pedidoId,
                pagoId,
                clienteId,
                nombreCliente,
                correoCliente,
                telefonoCliente,
                direccionCliente,
                detalles
        );
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

    public String getCorreoCliente() {
        return correoCliente;
    }

    @Deprecated
    public String getCorreoCLiente() {
        return getCorreoCliente();
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