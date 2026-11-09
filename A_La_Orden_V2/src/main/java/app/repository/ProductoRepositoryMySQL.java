
package app.repository;

import app.config.ConexionMySQL;
import app.domain.Producto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductoRepositoryMySQL implements ProductoRepository {

    @Override
    public Producto guardar(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        String sql;

        if (producto.getId() == 0) {
            sql = """
                    INSERT INTO productos (
                        restaurante_id, nombre, descripcion,
                        precio, disponible, categoria
                    )
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
        } else {
            sql = """
                    INSERT INTO productos (
                        id, restaurante_id, nombre, descripcion,
                        precio, disponible, categoria
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
        }

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS
             )) {

            int posicion = 1;

            if (producto.getId() != 0) {
                sentencia.setInt(posicion++, producto.getId());
            }

            sentencia.setInt(posicion++, producto.getRestauranteId());
            sentencia.setString(posicion++, producto.getNombre());
            sentencia.setString(posicion++, producto.getDescripcion());
            sentencia.setBigDecimal(posicion++, producto.getPrecio());
            sentencia.setBoolean(posicion++, producto.isDisponible());
            sentencia.setString(posicion, producto.getCategoria());

            sentencia.executeUpdate();

            if (producto.getId() == 0) {

                try (ResultSet claves = sentencia.getGeneratedKeys()) {

                    if (claves.next()) {
                        int idGenerado = claves.getInt(1);
                        return producto.conIdentificadorGenerado(idGenerado);
                    }

                    throw new SQLException(
                            "No se pudo obtener el ID generado."
                    );
                }
            }

            return producto;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al guardar el producto en MySQL.", e
            );
        }
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {

        String sql = """
                SELECT id, restaurante_id, nombre, descripcion,
                       precio, disponible, categoria
                FROM productos
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return Optional.of(mapearProducto(resultado));
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el producto con ID: " + id, e
            );
        }
    }

    @Override
    public List<Producto> obtenerTodos() {

        String sql = """
                SELECT id, restaurante_id, nombre, descripcion,
                       precio, disponible, categoria
                FROM productos
                ORDER BY id
                """;

        List<Producto> productos = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                productos.add(mapearProducto(resultado));
            }

            return productos;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar los productos en MySQL.", e
            );
        }
    }

    @Override
    public Producto actualizar(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        String sql = """
                UPDATE productos
                SET nombre = ?,
                    descripcion = ?,
                    precio = ?,
                    disponible = ?,
                    categoria = ?
                WHERE id = ?
                  AND restaurante_id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, producto.getNombre());
            sentencia.setString(2, producto.getDescripcion());
            sentencia.setBigDecimal(3, producto.getPrecio());
            sentencia.setBoolean(4, producto.isDisponible());
            sentencia.setString(5, producto.getCategoria());
            sentencia.setInt(6, producto.getId());
            sentencia.setInt(7, producto.getRestauranteId());

            int filasActualizadas = sentencia.executeUpdate();

            if (filasActualizadas == 0) {
                throw new IllegalArgumentException(
                        "No existe el producto o no pertenece al restaurante indicado."
                );
            }

            return producto;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar el producto en MySQL.", e
            );
        }
    }

    @Override
    public boolean eliminar(int id) {

        String sql = """
                DELETE FROM productos
                WHERE id = ?
                  AND NOT EXISTS (
                      SELECT 1
                      FROM detalle_pedido
                      WHERE producto_id = ?
                  )
                  AND NOT EXISTS (
                      SELECT 1
                      FROM detalle_factura
                      WHERE producto_id = ?
                  )
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);
            sentencia.setInt(2, id);
            sentencia.setInt(3, id);

            int filasEliminadas = sentencia.executeUpdate();

            return filasEliminadas > 0;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al eliminar el producto de MySQL.", e
            );
        }
    }

    private Producto mapearProducto(ResultSet resultado)
            throws SQLException {

        return Producto.restaurar(
                resultado.getInt("id"),
                resultado.getInt("restaurante_id"),
                resultado.getString("nombre"),
                resultado.getString("descripcion"),
                resultado.getBigDecimal("precio"),
                resultado.getBoolean("disponible"),
                resultado.getString("categoria")
        );
    }
}
