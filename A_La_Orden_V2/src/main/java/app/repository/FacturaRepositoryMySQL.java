
package app.repository;

import app.config.ConexionMySQL;
import app.domain.DetalleFactura;
import app.domain.Factura;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class FacturaRepositoryMySQL implements FacturaRepository {

    @Override
    public Factura guardar(Factura factura) {

        if (factura == null) {
            throw new IllegalArgumentException(
                    "La factura no puede ser nula."
            );
        }

        if (factura.getId() != 0) {
            throw new IllegalArgumentException(
                    "La factura ya tiene un identificador asignado."
            );
        }

        String numeroTemporal = "T" +
                UUID.randomUUID().toString().replace("-", "").substring(0, 19);

        String sqlFactura = """
                INSERT INTO facturas (
                    numero_factura,
                    fecha_generacion,
                    pedido_id,
                    pago_id,
                    cliente_id,
                    nombre_cliente,
                    correo_cliente,
                    telefono_cliente,
                    direccion_cliente,
                    total
                )
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        String sqlActualizarNumero = """
                UPDATE facturas
                SET numero_factura = ?
                WHERE id = ?
                """;

        String sqlDetalle = """
                INSERT INTO detalle_factura (
                    factura_id,
                    producto_id,
                    nombre_producto,
                    cantidad,
                    precio_unitario,
                    subtotal
                )
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {

            conexion.setAutoCommit(false);

            try {

                int idGenerado;

                // 1. Insertamos la factura con un número temporal.
                try (PreparedStatement sentencia = conexion.prepareStatement(
                        sqlFactura, Statement.RETURN_GENERATED_KEYS
                )) {

                    sentencia.setString(1, numeroTemporal);
                    sentencia.setTimestamp(
                            2,
                            Timestamp.valueOf(factura.getFechaGeneracion())
                    );
                    sentencia.setInt(3, factura.getPedidoId());
                    sentencia.setInt(4, factura.getPagoId());
                    sentencia.setInt(5, factura.getClienteId());
                    sentencia.setString(6, factura.getNombreCliente());
                    sentencia.setString(7, factura.getCorreoCliente());
                    sentencia.setString(8, factura.getTelefonoCliente());
                    sentencia.setString(9, factura.getDireccionCliente());
                    sentencia.setBigDecimal(10, factura.getTotal());

                    sentencia.executeUpdate();

                    try (ResultSet claves = sentencia.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID de la factura."
                            );
                        }

                        idGenerado = claves.getInt(1);
                    }
                }

                // 2. Generamos la factura con su ID y número definitivo.
                Factura facturaGuardada =
                        factura.conIdentificadorGenerado(idGenerado);

                // 3. Actualizamos el número temporal.
                try (PreparedStatement sentencia =
                             conexion.prepareStatement(sqlActualizarNumero)) {

                    sentencia.setString(
                            1,
                            facturaGuardada.getNumeroFactura()
                    );
                    sentencia.setInt(2, idGenerado);

                    sentencia.executeUpdate();
                }

                // 4. Guardamos los detalles históricos.
                try (PreparedStatement sentencia =
                             conexion.prepareStatement(sqlDetalle)) {

                    for (DetalleFactura detalle : factura.getDetalles()) {

                        sentencia.setInt(1, idGenerado);
                        sentencia.setInt(2, detalle.getProductoId());
                        sentencia.setString(
                                3,
                                detalle.getNombreProducto()
                        );
                        sentencia.setInt(4, detalle.getCantidad());
                        sentencia.setBigDecimal(
                                5,
                                detalle.getPrecioUnitario()
                        );
                        sentencia.setBigDecimal(
                                6,
                                detalle.getSubtotal()
                        );

                        sentencia.addBatch();
                    }

                    sentencia.executeBatch();
                }

                // 5. Confirmamos toda la operación.
                conexion.commit();

                return facturaGuardada;

            } catch (Exception e) {

                conexion.rollback();

                throw e;

            } finally {
                conexion.setAutoCommit(true);
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar la factura en MySQL.", e
            );
        }
    }

    @Override
    public Factura buscarPorPago(int pagoId) {

        String sql = """
                SELECT id
                FROM facturas
                WHERE pago_id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, pagoId);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return buscarPorId(conexion, resultado.getInt("id"));
                }
            }

            return null;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar la factura del pago: " + pagoId, e
            );
        }
    }

    @Override
    public Factura buscarPorId(int id) {

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {
            return buscarPorId(conexion, id);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar la factura con ID: " + id, e
            );
        }
    }

    @Override
    public List<Factura> listarPorPedido(int pedidoId) {

        String sql = """
                SELECT id
                FROM facturas
                WHERE pedido_id = ?
                ORDER BY id
                """;

        List<Factura> facturas = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, pedidoId);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {
                    Factura factura = buscarPorId(
                            conexion,
                            resultado.getInt("id")
                    );

                    if (factura != null) {
                        facturas.add(factura);
                    }
                }
            }

            return facturas;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar las facturas del pedido: " + pedidoId,
                    e
            );
        }
    }

    @Override
    public List<Factura> listar() {

        String sql = """
                SELECT id
                FROM facturas
                ORDER BY id
                """;

        List<Factura> facturas = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                Factura factura = buscarPorId(
                        conexion,
                        resultado.getInt("id")
                );

                if (factura != null) {
                    facturas.add(factura);
                }
            }

            return facturas;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar las facturas en MySQL.", e
            );
        }
    }

    // Recupera la factura y sus detalles usando la misma conexión.
    private Factura buscarPorId(Connection conexion, int id)
            throws SQLException {

        String sqlFactura = """
                SELECT id, numero_factura, fecha_generacion,
                       pedido_id, pago_id, cliente_id,
                       nombre_cliente, correo_cliente,
                       telefono_cliente, direccion_cliente
                FROM facturas
                WHERE id = ?
                """;

        int facturaId;
        String numeroFactura;
        java.time.LocalDateTime fechaGeneracion;
        int pedidoId;
        int pagoId;
        int clienteId;
        String nombreCliente;
        String correoCliente;
        String telefonoCliente;
        String direccionCliente;

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(sqlFactura)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (!resultado.next()) {
                    return null;
                }

                facturaId = resultado.getInt("id");
                numeroFactura = resultado.getString("numero_factura");
                fechaGeneracion = resultado.getTimestamp(
                        "fecha_generacion"
                ).toLocalDateTime();

                pedidoId = resultado.getInt("pedido_id");
                pagoId = resultado.getInt("pago_id");
                clienteId = resultado.getInt("cliente_id");
                nombreCliente = resultado.getString("nombre_cliente");
                correoCliente = resultado.getString("correo_cliente");
                telefonoCliente = resultado.getString("telefono_cliente");
                direccionCliente = resultado.getString("direccion_cliente");
            }
        }

        // Recuperamos los detalles históricos de la factura.
        List<DetalleFactura> detalles = cargarDetalles(conexion, facturaId);

        return Factura.restaurar(
                facturaId,
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

    private List<DetalleFactura> cargarDetalles(
            Connection conexion,
            int facturaId
    ) throws SQLException {

        String sql = """
                SELECT producto_id, nombre_producto,
                       cantidad, precio_unitario
                FROM detalle_factura
                WHERE factura_id = ?
                ORDER BY id
                """;

        List<DetalleFactura> detalles = new ArrayList<>();

        try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, facturaId);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    detalles.add(DetalleFactura.restaurar(
                            resultado.getInt("producto_id"),
                            resultado.getString("nombre_producto"),
                            resultado.getInt("cantidad"),
                            resultado.getBigDecimal("precio_unitario")
                    ));
                }
            }
        }

        return detalles;
    }
}
