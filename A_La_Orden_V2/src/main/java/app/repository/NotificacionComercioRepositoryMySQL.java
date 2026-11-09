
package app.repository;

import app.config.ConexionMySQL;
import app.domain.NotificacionComercio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class NotificacionComercioRepositoryMySQL
        implements NotificacionComercioRepository {

    @Override
    public void guardar(NotificacionComercio notificacion) {

        if (notificacion == null) {
            throw new IllegalArgumentException(
                    "La notificación no puede ser nula."
            );
        }

        String sql = """
                INSERT INTO notificaciones_comercio (
                    restaurante_id,
                    pedido_id,
                    mensaje
                )
                VALUES (?, ?, ?)
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, notificacion.getRestauranteId());
            sentencia.setInt(2, notificacion.getPedidoId());
            sentencia.setString(3, notificacion.getMensaje());

            sentencia.executeUpdate();

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar la notificación del comercio.", e
            );
        }
    }

    @Override
    public List<NotificacionComercio> listar() {

        String sql = """
                SELECT restaurante_id, pedido_id, mensaje
                FROM notificaciones_comercio
                ORDER BY id
                """;

        List<NotificacionComercio> notificaciones = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                NotificacionComercio notificacion =
                        new NotificacionComercio(
                                resultado.getInt("restaurante_id"),
                                resultado.getInt("pedido_id"),
                                resultado.getString("mensaje")
                        );

                notificaciones.add(notificacion);
            }

            return notificaciones;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al listar las notificaciones del comercio.", e
            );
        }
    }
}
