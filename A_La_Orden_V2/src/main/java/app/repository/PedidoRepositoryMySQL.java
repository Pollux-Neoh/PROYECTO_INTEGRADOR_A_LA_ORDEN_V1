
package app.repository;

import app.config.ConexionMySQL;
import app.domain.Cliente;
import app.domain.DetallePedido;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.Restaurante;
import app.domain.enums.EstadoPedido;
import app.service.OutputPort.PedidoOutport;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PedidoRepositoryMySQL implements PedidoOutport {

    private final ClienteRepositoryMySQL clienteRepository;
    private final RestauranteRepositoryMySQL restauranteRepository;
    private final ProductoRepositoryMySQL productoRepository;

    public PedidoRepositoryMySQL() {
        clienteRepository = new ClienteRepositoryMySQL();
        restauranteRepository = new RestauranteRepositoryMySQL();
        productoRepository = new ProductoRepositoryMySQL();
    }

    @Override
    public Pedido guardar(Pedido pedido) {

        if (pedido == null
                || pedido.getCliente() == null
                || pedido.getRestaurante() == null
                || pedido.getCliente().getId() <= 0
                || pedido.getRestaurante().getId() <= 0) {

            throw new IllegalArgumentException(
                    "El pedido, cliente y restaurante son obligatorios."
            );
        }

        Connection conexion = null;

        try {
            conexion = ConexionMySQL.obtenerConexion();
            conexion.setAutoCommit(false);

            Pedido pedidoGuardado = pedido;

            if (pedido.getId() == 0) {

                String sql = """
                        INSERT INTO pedidos (
                            cliente_id, restaurante_id, estado,
                            tiempo_estimado_minutos, codigo_qr
                        )
                        VALUES (?, ?, ?, ?, ?)
                        """;

                try (PreparedStatement sentencia =
                             conexion.prepareStatement(
                                     sql,
                                     Statement.RETURN_GENERATED_KEYS
                             )) {

                    configurarPedido(sentencia, pedido, 1);
                    sentencia.executeUpdate();

                    try (ResultSet claves = sentencia.getGeneratedKeys()) {

                        if (!claves.next()) {
                            throw new SQLException(
                                    "No se pudo obtener el ID del pedido."
                            );
                        }

                        int idGenerado = claves.getInt(1);

                        pedidoGuardado =
                                pedido.conIdentificadorGenerado(idGenerado);
                    }
                }

            } else {

                boolean existe = existePedido(conexion, pedido.getId());

                if (existe) {

                    String sql = """
                            UPDATE pedidos
                            SET cliente_id = ?,
                                restaurante_id = ?,
                                estado = ?,
                                tiempo_estimado_minutos = ?,
                                codigo_qr = ?
                            WHERE id = ?
                            """;

                    try (PreparedStatement sentencia =
                                 conexion.prepareStatement(sql)) {

                        configurarPedido(sentencia, pedido, 1);
                        sentencia.setInt(6, pedido.getId());
                        sentencia.executeUpdate();
                    }

                } else {

                    String sql = """
                            INSERT INTO pedidos (
                                id, cliente_id, restaurante_id,
                                estado, tiempo_estimado_minutos, codigo_qr
                            )
                            VALUES (?, ?, ?, ?, ?, ?)
                            """;

                    try (PreparedStatement sentencia =
                                 conexion.prepareStatement(sql)) {

                        sentencia.setInt(1, pedido.getId());
                        configurarPedido(sentencia, pedido, 2);
                        sentencia.executeUpdate();
                    }
                }
            }

            guardarDetalles(
                    conexion,
                    pedidoGuardado.getId(),
                    pedidoGuardado.getDetalles()
            );

            conexion.commit();

            return pedidoGuardado;

        } catch (SQLException | RuntimeException e) {

            if (conexion != null) {
                try {
                    conexion.rollback();
                } catch (SQLException errorRollback) {
                    e.addSuppressed(errorRollback);
                }
            }

            if (e instanceof SQLException) {
                throw new IllegalStateException(
                        "Error al guardar el pedido en MySQL.", e
                );
            }

            throw (RuntimeException) e;

        } finally {

            if (conexion != null) {
                try {
                    conexion.setAutoCommit(true);
                    conexion.close();
                } catch (SQLException e) {
                    // La conexión se cierra al finalizar la operación.
                }
            }
        }
    }

    private void configurarPedido(
            PreparedStatement sentencia,
            Pedido pedido,
            int posicionInicial
    ) throws SQLException {

        sentencia.setInt(
                posicionInicial,
                pedido.getCliente().getId()
        );

        sentencia.setInt(
                posicionInicial + 1,
                pedido.getRestaurante().getId()
        );

        sentencia.setString(
                posicionInicial + 2,
                pedido.getEstado().name()
        );

        if (pedido.getTiempoEstimadoMinutos() == null) {
            sentencia.setNull(
                    posicionInicial + 3,
                    java.sql.Types.INTEGER
            );
        } else {
            sentencia.setInt(
                    posicionInicial + 3,
                    pedido.getTiempoEstimadoMinutos()
            );
        }

        sentencia.setString(
                posicionInicial + 4,
                pedido.getCodigoQr()
        );
    }

    private boolean existePedido(
            Connection conexion,
            int id
    ) throws SQLException {

        String sql = """
                SELECT id
                FROM pedidos
                WHERE id = ?
                """;

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {
                return resultado.next();
            }
        }
    }

    private void guardarDetalles(
            Connection conexion,
            int pedidoId,
            List<DetallePedido> detalles
    ) throws SQLException {

        // Reemplazamos los detalles anteriores por los actuales.
        String eliminarSql = """
                DELETE FROM detalle_pedido
                WHERE pedido_id = ?
                """;

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(eliminarSql)) {

            sentencia.setInt(1, pedidoId);
            sentencia.executeUpdate();
        }

        String insertarSql = """
                INSERT INTO detalle_pedido (
                    pedido_id, producto_id, cantidad, precio_unitario
                )
                VALUES (?, ?, ?, ?)
                """;

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(insertarSql)) {

            for (DetallePedido detalle : detalles) {

                sentencia.setInt(1, pedidoId);
                sentencia.setInt(
                        2,
                        detalle.getProducto().getId()
                );
                sentencia.setInt(3, detalle.getCantidad());
                sentencia.setBigDecimal(
                        4,
                        detalle.getPrecioUnitario()
                );

                sentencia.addBatch();
            }

            sentencia.executeBatch();
        }
    }

    @Override
    public Pedido buscarPorId(int id) {

        String sql = """
                SELECT id, cliente_id, restaurante_id,
                       estado, tiempo_estimado_minutos, codigo_qr
                FROM pedidos
                WHERE id = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setInt(1, id);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return mapearPedido(conexion, resultado);
                }

                return null;
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el pedido con ID: " + id, e
            );
        }
    }

    @Override
    public Optional<Pedido> buscarPorId(String id) {

        if (id == null) {
            return Optional.empty();
        }

        try {
            return Optional.ofNullable(
                    buscarPorId(Integer.parseInt(id))
            );

        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Pedido> buscarPorCodigoQr(String codigoQr) {

        if (codigoQr == null || codigoQr.trim().isEmpty()) {
            return Optional.empty();
        }

        String sql = """
                SELECT id, cliente_id, restaurante_id,
                       estado, tiempo_estimado_minutos, codigo_qr
                FROM pedidos
                WHERE codigo_qr = ?
                """;

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setString(1, codigoQr);

            try (ResultSet resultado = sentencia.executeQuery()) {

                if (resultado.next()) {
                    return Optional.of(
                            mapearPedido(conexion, resultado)
                    );
                }

                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al buscar el pedido por código QR.", e
            );
        }
    }

    @Override
    public List<Pedido> listar() {

        String sql = """
                SELECT id
                FROM pedidos
                ORDER BY id
                """;

        List<Integer> identificadores = new ArrayList<>();
        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conexion = ConexionMySQL.obtenerConexion();
             PreparedStatement sentencia =
                     conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                identificadores.add(resultado.getInt("id"));
            }

        } catch (SQLException e) {
            throw new IllegalStateException(
                    "Error al consultar los pedidos en MySQL.", e
            );
        }

        for (int id : identificadores) {

            Pedido pedido = buscarPorId(id);

            if (pedido != null) {
                pedidos.add(pedido);
            }
        }

        return java.util.Collections.unmodifiableList(pedidos);
    }

    private Pedido mapearPedido(
            Connection conexion,
            ResultSet resultado
    ) throws SQLException {

        int id = resultado.getInt("id");
        int clienteId = resultado.getInt("cliente_id");
        int restauranteId = resultado.getInt("restaurante_id");

        Cliente cliente = clienteRepository.buscarPorId(clienteId);

        Optional<Restaurante> restaurante =
                restauranteRepository.buscarPorId(restauranteId);

        if (cliente == null || !restaurante.isPresent()) {
            throw new IllegalStateException(
                    "No se pudo recuperar el cliente o restaurante del pedido."
            );
        }

        EstadoPedido estado = EstadoPedido.valueOf(
                resultado.getString("estado")
        );

        int tiempo = resultado.getInt("tiempo_estimado_minutos");

        Integer tiempoEstimado =
                resultado.wasNull() ? null : tiempo;

        String codigoQr = resultado.getString("codigo_qr");

        List<DetallePedido> detalles =
                cargarDetalles(conexion, id);

        return Pedido.restaurar(
                id,
                cliente,
                restaurante.get(),
                estado,
                tiempoEstimado,
                codigoQr,
                detalles
        );
    }

    private List<DetallePedido> cargarDetalles(
            Connection conexion,
            int pedidoId
    ) throws SQLException {

        String sql = """
                SELECT producto_id, cantidad, precio_unitario
                FROM detalle_pedido
                WHERE pedido_id = ?
                ORDER BY producto_id
                """;

        List<DetallePedido> detalles = new ArrayList<>();

        try (PreparedStatement sentencia =
                     conexion.prepareStatement(sql)) {

            sentencia.setInt(1, pedidoId);

            try (ResultSet resultado = sentencia.executeQuery()) {

                while (resultado.next()) {

                    int productoId =
                            resultado.getInt("producto_id");

                    Optional<Producto> producto =
                            productoRepository.buscarPorId(productoId);

                    if (!producto.isPresent()) {
                        throw new IllegalStateException(
                                "No se encontró el producto ID: "
                                        + productoId
                        );
                    }

                    DetallePedido detalle = DetallePedido.restaurar(
                            producto.get(),
                            resultado.getInt("cantidad"),
                            resultado.getBigDecimal("precio_unitario")
                    );

                    detalles.add(detalle);
                }
            }
        }

        return detalles;
    }
}
