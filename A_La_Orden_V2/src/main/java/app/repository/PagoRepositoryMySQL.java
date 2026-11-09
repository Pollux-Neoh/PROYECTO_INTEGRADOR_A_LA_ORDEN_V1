
package app.repository;

import app.config.ConexionMySQL;
import app.domain.Pago;
import app.domain.Pedido;
import app.domain.enums.EstadoPago;
import app.domain.enums.MetodoPago;
import app.service.OutputPort.PedidoOutport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class PagoRepositoryMySQL implements PagoRepository {

    private final PedidoOutport pedidoRepository;

    public PagoRepositoryMySQL() {
        this(new PedidoRepositoryMySQL());
    }

    public PagoRepositoryMySQL(PedidoOutport pedidoRepository) {

        if (pedidoRepository == null) {
            throw new IllegalArgumentException(
                    "El repositorio de pedidos es obligatorio."
            );
        }

        this.pedidoRepository = pedidoRepository;
    }

    @Override
    public Pago guardar(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago no puede ser nulo."
            );
        }

        if (pago.getId() != 0) {
            throw new IllegalArgumentException(
                    "El pago debe tener ID cero para registrarse."
            );
        }

        String sql = """
                INSERT INTO pagos (
                    pedido_id,
                    metodo_pago,
                    monto,
                    estado
                )
                VALUES (?, ?, ?, ?)
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS
             )) {

            sentencia.setInt(1, pago.getPedido().getId());
            sentencia.setString(2, pago.getMetodoPago().name());
            sentencia.setBigDecimal(3, pago.getMonto());
            sentencia.setString(4, pago.getEstado().name());

            sentencia.executeUpdate();

            try (ResultSet claves = sentencia.getGeneratedKeys()) {

                if (claves.next()) {
                    int idGenerado = claves.getInt(1);
                    return pago.conIdentificadorGenerado(idGenerado);
                }
            }

            throw new SQLException(
                    "No fue posible obtener el ID generado para el pago."
            );

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al registrar el pago en MySQL.", e
            );
        }
    }

    @Override
    public void actualizar(Pago pago) {

        if (pago == null) {
            throw new IllegalArgumentException(
                    "El pago no puede ser nulo."
            );
        }

        String sql = """
                UPDATE pagos
                SET pedido_id = ?,
                    metodo_pago = ?,
                    monto = ?,
                    estado = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, pago.getPedido().getId());
            sentencia.setString(2, pago.getMetodoPago().name());
            sentencia.setBigDecimal(3, pago.getMonto());
            sentencia.setString(4, pago.getEstado().name());
            sentencia.setInt(5, pago.getId());

            int filasActualizadas = sentencia.executeUpdate();

            if (filasActualizadas == 0) {
                if (!existePago(pago.getId())) {
                    throw new IllegalArgumentException(
                            "No existe un pago con ID: " + pago.getId()
                    );
                }
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar el pago en MySQL.", e
            );
        }
    }

    @Override
    public Pago buscarPorId(int id) {

        String sql = """
                SELECT id, pedido_id, metodo_pago, monto, estado
                FROM pagos
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return mapearPago(resultado);
                }
            }

            return null;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el pago con ID: " + id, e
            );
        }
    }

    @Override
    public List<Pago> listarPorPedido(int pedidoId) {

        String sql = """
                SELECT id, pedido_id, metodo_pago, monto, estado
                FROM pagos
                WHERE pedido_id = ?
                ORDER BY id
                """;

        List<Pago> pagos = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, pedidoId);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {
                    pagos.add(mapearPago(resultado));
                }
            }

            return pagos;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar los pagos del pedido: " + pedidoId, e
            );
        }
    }

    @Override
    public List<Pago> listar() {

        String sql = """
                SELECT id, pedido_id, metodo_pago, monto, estado
                FROM pagos
                ORDER BY id
                """;

        List<Pago> pagos = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                pagos.add(mapearPago(resultado));
            }

            return pagos;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar los pagos en MySQL.", e
            );
        }
    }

    private Pago mapearPago(ResultSet resultado) throws SQLException {

        int pedidoId = resultado.getInt("pedido_id");

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null) {
            throw new IllegalStateException(
                    "No se encontró el pedido asociado al pago: " + pedidoId
            );
        }

        return Pago.restaurar(
                resultado.getInt("id"),
                pedido,
                MetodoPago.valueOf(resultado.getString("metodo_pago")),
                resultado.getBigDecimal("monto"),
                EstadoPago.valueOf(resultado.getString("estado"))
        );
    }

    private boolean existePago(int id) throws SQLException {

        String sql = "SELECT 1 FROM pagos WHERE id = ?";

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }
}
