
package app.repository;

import app.config.ConexionMySQL;
import app.domain.NotificacionCliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificacionClienteRepositoryMySQL
        implements NotificacionClienteRepository {

    @Override
    public void guardar(NotificacionCliente notificacion) {

        if (notificacion == null) {
            throw new IllegalArgumentException(
                    "La notificación no puede ser nula."
            );
        }

        String sql = """
                INSERT INTO notificaciones_cliente (
                    cliente_id,
                    pedido_id,
                    mensaje
                )
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, notificacion.getClienteId());
            sentencia.setInt(2, notificacion.getPedidoId());
            sentencia.setString(3, notificacion.getMensaje());

            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar la notificación del cliente.", e
            );
        }
    }

    @Override
    public List<NotificacionCliente> listar() {

        String sql = """
                SELECT cliente_id, pedido_id, mensaje
                FROM notificaciones_cliente
                ORDER BY id
                """;

        List<NotificacionCliente> notificaciones = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                NotificacionCliente notificacion =
                        new NotificacionCliente(
                                resultado.getInt("cliente_id"),
                                resultado.getInt("pedido_id"),
                                resultado.getString("mensaje")
                        );

                notificaciones.add(notificacion);
            }

            return notificaciones;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar las notificaciones de clientes.", e
            );
        }
    }
}
