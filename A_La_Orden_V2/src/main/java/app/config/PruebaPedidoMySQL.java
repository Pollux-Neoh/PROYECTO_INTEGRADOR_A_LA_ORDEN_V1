
package app.config;

import app.domain.Cliente;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.Restaurante;

import app.repository.ClienteRepositoryMySQL;
import app.repository.ProductoRepositoryMySQL;
import app.repository.RestauranteRepositoryMySQL;
import app.repository.PedidoRepositoryMySQL;

import java.util.Optional;

public class PruebaPedidoMySQL {

    public static void main(String[] args) {

        ClienteRepositoryMySQL clienteRepo =
                new ClienteRepositoryMySQL();

        RestauranteRepositoryMySQL restauranteRepo =
                new RestauranteRepositoryMySQL();

        ProductoRepositoryMySQL productoRepo =
                new ProductoRepositoryMySQL();

        PedidoRepositoryMySQL pedidoRepo =
                new PedidoRepositoryMySQL();

        System.out.println("===== RECUPERAR DATOS =====");

        // Recuperamos el cliente de prueba.
        Cliente cliente = clienteRepo.buscarPorId(1);

        // Recuperamos el restaurante ID 2.
        Optional<Restaurante> restauranteResultado =
                restauranteRepo.buscarPorId(2);

        // Recuperamos dos productos existentes.
        Optional<Producto> producto1 =
                productoRepo.buscarPorId(1);

        Optional<Producto> producto2 =
                productoRepo.buscarPorId(2);

        if (cliente == null
                || !restauranteResultado.isPresent()
                || !producto1.isPresent()
                || !producto2.isPresent()) {

            System.out.println(
                    "No se encontraron todos los datos necesarios."
            );
            return;
        }

        Restaurante restaurante = restauranteResultado.get();

        System.out.println("Cliente: " + cliente.getNombre());
        System.out.println("Restaurante: " + restaurante.getNombre());


        System.out.println("\n===== CREAR PEDIDO =====");

        Pedido pedido = Pedido.crearNuevo(cliente, restaurante);

        // Guardamos primero para obtener el ID generado por MySQL.
        pedido = pedidoRepo.guardar(pedido);

        System.out.println("Pedido creado correctamente.");
        System.out.println("ID generado: " + pedido.getId());


        System.out.println("\n===== AGREGAR PRODUCTOS =====");

        pedido.agregarProducto(producto1.get(), 2);
        pedido.agregarProducto(producto2.get(), 1);

        // Guardamos los detalles del pedido.
        pedidoRepo.guardar(pedido);

        System.out.println("Productos agregados:");

        for (var detalle : pedido.getDetalles()) {
            System.out.println(
                    "- " + detalle.getProducto().getNombre()
                            + " | Cantidad: " + detalle.getCantidad()
                            + " | Precio: $" + detalle.getPrecioUnitario()
            );
        }

        System.out.println("Subtotal: $" + pedido.getSubtotal());


        System.out.println("\n===== CONFIRMAR PEDIDO =====");

        boolean confirmado = pedido.confirmar();

        if (!confirmado) {
            System.out.println("No fue posible confirmar el pedido.");
            return;
        }

        pedido.actualizarTiempoEstimado(20);

        // Persistimos estado, QR, tiempo y detalles.
        pedidoRepo.guardar(pedido);

        System.out.println("Pedido confirmado correctamente.");
        System.out.println("Estado: " + pedido.getEstado());
        System.out.println("Código QR: " + pedido.getCodigoQr());


        System.out.println("\n===== BUSCAR PEDIDO POR ID =====");

        Pedido pedidoEncontrado =
                pedidoRepo.buscarPorId(pedido.getId());

        if (pedidoEncontrado != null) {

            System.out.println(
                    "Pedido recuperado: " + pedidoEncontrado.getId()
            );

            System.out.println(
                    "Cliente: " + pedidoEncontrado.getCliente().getNombre()
            );

            System.out.println(
                    "Restaurante: "
                            + pedidoEncontrado.getRestaurante().getNombre()
            );

            System.out.println(
                    "Estado: " + pedidoEncontrado.getEstado()
            );

            System.out.println(
                    "Productos recuperados: "
                            + pedidoEncontrado.getDetalles().size()
            );

            System.out.println(
                    "Subtotal recuperado: $"
                            + pedidoEncontrado.getSubtotal()
            );

        } else {
            System.out.println("No se encontró el pedido.");
        }


        System.out.println("\n===== BUSCAR POR CÓDIGO QR =====");

        var pedidoQr =
                pedidoRepo.buscarPorCodigoQr(pedido.getCodigoQr());

        if (pedidoQr.isPresent()) {
            System.out.println(
                    "Pedido encontrado mediante QR: "
                            + pedidoQr.get().getId()
            );
        } else {
            System.out.println("No se encontró el código QR.");
        }


        System.out.println("\n===== LISTAR PEDIDOS =====");

        for (Pedido registro : pedidoRepo.listar()) {
            System.out.println(
                    "Pedido ID: " + registro.getId()
                            + " | Estado: " + registro.getEstado()
            );
        }

        System.out.println("\n===== PRUEBAS FINALIZADAS =====");
    }
}
