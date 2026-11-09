
package app.repository;

import app.config.ConexionMySQL;
import app.domain.Cliente;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ClienteRepositoryMySQL implements ClienteRepository {

    @Override
    public Cliente guardar(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo."
            );
        }

        if (cliente.getId() < 0) {
            throw new IllegalArgumentException(
                    "El ID del cliente no puede ser negativo."
            );
        }

        String sql;

        if (cliente.getId() == 0) {
            sql = """
                    INSERT INTO clientes (
                        nombre, correo, contrasenia,
                        telefono, estado, direccion
                    )
                    VALUES (?, ?, ?, ?, ?, ?)
                    """;
        } else {
            sql = """
                    INSERT INTO clientes (
                        id, nombre, correo, contrasenia,
                        telefono, estado, direccion
                    )
                    VALUES (?, ?, ?, ?, ?, ?, ?)
                    """;
        }

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(
                     sql, Statement.RETURN_GENERATED_KEYS
             )) {

            int posicion = 1;

            if (cliente.getId() != 0) {
                sentencia.setInt(posicion++, cliente.getId());
            }

            sentencia.setString(posicion++, cliente.getNombre());
            sentencia.setString(posicion++, cliente.getCorreo());
            sentencia.setString(posicion++, cliente.getContrasenia());
            sentencia.setString(posicion++, cliente.getTelefono());
            sentencia.setBoolean(posicion++, cliente.isEstado());
            sentencia.setString(posicion, cliente.getDireccion());

            sentencia.executeUpdate();

            if (cliente.getId() == 0) {

                try (ResultSet claves = sentencia.getGeneratedKeys()) {

                    if (claves.next()) {
                        cliente.setId(claves.getInt(1));
                    } else {
                        throw new SQLException(
                                "No se pudo obtener el ID generado."
                        );
                    }
                }
            }

            return cliente;

        } catch (SQLException e) {

            if ("23000".equals(e.getSQLState())) {
                throw new IllegalArgumentException(
                        "Ya existe un cliente con ese ID o correo.", e
                );
            }

            throw new IllegalStateException(
                    "Error al guardar el cliente en MySQL.", e
            );
        }
    }

    @Override
    public Cliente buscarPorId(int id) {

        String sql = """
                SELECT id, nombre, correo, contrasenia,
                       telefono, estado, direccion
                FROM clientes
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return mapearCliente(resultado);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el cliente con ID: " + id, e
            );
        }
    }

    @Override
    public Cliente buscarPorCorreo(String correo) {

        if (correo == null) {
            return null;
        }

        String sql = """
                SELECT id, nombre, correo, contrasenia,
                       telefono, estado, direccion
                FROM clientes
                WHERE correo = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, correo);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return mapearCliente(resultado);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el cliente por correo.", e
            );
        }
    }

    @Override
    public List<Cliente> listar() {

        String sql = """
                SELECT id, nombre, correo, contrasenia,
                       telefono, estado, direccion
                FROM clientes
                ORDER BY id
                """;

        List<Cliente> clientes = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                clientes.add(mapearCliente(resultado));
            }

            return Collections.unmodifiableList(clientes);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar los clientes en MySQL.", e
            );
        }
    }

    @Override
    public void actualizarContraseina(
            Cliente cliente,
            String nuevaContrasenia
    ) {

        if (cliente == null || nuevaContrasenia == null) {
            throw new IllegalArgumentException(
                    "El cliente y la contraseña son obligatorios."
            );
        }

        String sql = """
                UPDATE clientes
                SET contrasenia = ?
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, nuevaContrasenia);
            sentencia.setInt(2, cliente.getId());

            int filasActualizadas = sentencia.executeUpdate();

            if (filasActualizadas == 0) {

                // Comprobamos si el cliente existe.
                // Puede que la contraseña ya tuviera ese mismo valor.
                String consulta = """
                        SELECT id
                        FROM clientes
                        WHERE id = ?
                        """;

                try (PreparedStatement verificacion =
                             conexion.prepareStatement(consulta)) {

                    verificacion.setInt(1, cliente.getId());

                    try (ResultSet resultado = verificacion.executeQuery()) {

                        if (!resultado.next()) {
                            throw new IllegalArgumentException(
                                    "No existe el cliente que se desea actualizar."
                            );
                        }
                    }
                }
            }

            cliente.setContrasenia(nuevaContrasenia);

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al actualizar la contraseña del cliente.", e
            );
        }
    }

    private Cliente mapearCliente(ResultSet resultado)
            throws SQLException {

        return new Cliente(
                resultado.getInt("id"),
                resultado.getString("nombre"),
                resultado.getString("correo"),
                resultado.getString("contrasenia"),
                resultado.getString("telefono"),
                resultado.getBoolean("estado"),
                resultado.getString("direccion")
        );
    }
}
