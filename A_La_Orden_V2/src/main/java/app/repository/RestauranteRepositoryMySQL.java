
package app.repository;

import app.config.ConexionMySQL;
import app.domain.Producto;
import app.domain.Restaurante;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RestauranteRepositoryMySQL implements RestauranteRepository {

    @Override
    public List<Restaurante> obtenerTodos() {

        String sql = """
                SELECT id, nombre, imagen_url,
                       calificacion_promedio, distancia_km, categoria
                FROM restaurantes
                ORDER BY id
                """;

        List<Restaurante> restaurantes = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {

            try (PreparedStatement sentencia = conexion.prepareStatement(sql);
                 ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {
                    restaurantes.add(mapearRestaurante(resultado));
                }
            }

            // Recuperamos los productos de los restaurantes.
            cargarMenus(conexion, restaurantes);

            return restaurantes;

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar los restaurantes en MySQL.", e
            );
        }
    }

    @Override
    public Optional<Restaurante> buscarPorId(int id) {

        String sql = """
                SELECT id, nombre, imagen_url,
                       calificacion_promedio, distancia_km, categoria
                FROM restaurantes
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {

            Restaurante restaurante = null;

            try (PreparedStatement sentencia = conexion.prepareStatement(sql)) {

                sentencia.setInt(1, id);

                try (ResultSet resultado = sentencia.executeQuery()) {

                    if (resultado.next()) {
                        restaurante = mapearRestaurante(resultado);
                    }
                }
            }

            if (restaurante == null) {
                return Optional.empty();
            }

            List<Restaurante> restaurantes = new ArrayList<>();
            restaurantes.add(restaurante);

            cargarMenus(conexion, restaurantes);

            return Optional.of(restaurante);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el restaurante con ID: " + id, e
            );
        }
    }

    private Restaurante mapearRestaurante(ResultSet resultado)
            throws SQLException {

        int id = resultado.getInt("id");
        String nombre = resultado.getString("nombre");
        String imagenUrl = resultado.getString("imagen_url");

        double calificacion = resultado.getDouble("calificacion_promedio");
        Double calificacionPromedio =
                resultado.wasNull() ? null : calificacion;

        double distancia = resultado.getDouble("distancia_km");
        Double distanciaKm =
                resultado.wasNull() ? null : distancia;

        String categoria = resultado.getString("categoria");

        return new Restaurante(
                id,
                nombre,
                imagenUrl,
                calificacionPromedio,
                distanciaKm,
                categoria
        );
    }

    private void cargarMenus(
            Connection conexion,
            List<Restaurante> restaurantes
    ) throws SQLException {

        String sql = """
                SELECT id, restaurante_id, nombre, descripcion,
                       precio, disponible, categoria
                FROM productos
                ORDER BY restaurante_id, id
                """;

        Map<Integer, List<Producto>> productosPorRestaurante =
                new HashMap<>();

        for (Restaurante restaurante : restaurantes) {
            productosPorRestaurante.put(
                    restaurante.getId(),
                    new ArrayList<>()
            );
        }

        try (PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {

                int restauranteId = resultado.getInt("restaurante_id");

                List<Producto> menu =
                        productosPorRestaurante.get(restauranteId);

                if (menu != null) {

                    Producto producto = Producto.restaurar(
                            resultado.getInt("id"),
                            restauranteId,
                            resultado.getString("nombre"),
                            resultado.getString("descripcion"),
                            resultado.getBigDecimal("precio"),
                            resultado.getBoolean("disponible"),
                            resultado.getString("categoria")
                    );

                    menu.add(producto);
                }
            }
        }

        for (Restaurante restaurante : restaurantes) {

            restaurante.setMenu(
                    productosPorRestaurante.get(restaurante.getId())
            );
        }
    }
}
