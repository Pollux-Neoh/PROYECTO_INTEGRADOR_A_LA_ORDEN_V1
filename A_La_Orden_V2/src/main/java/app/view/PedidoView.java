package app.view;



import app.domain.Pago;
import app.service.PagoService;
import app.domain.enums.MetodoPago;
import app.domain.DetallePedido;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.enums.EstadoPedido;
import app.service.PedidoService;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class PedidoView {

    private final Scanner scanner;
    private final PedidoService pedidoService;
    private final PagoService pagoService;
    private final List<Producto> productos;

    public PedidoView(
            PedidoService pedidoService,
            PagoService pagoService,
            List<Producto> productos) {
        this.scanner = new Scanner(System.in);
        this.pedidoService = pedidoService;
        this.pagoService = pagoService;
        this.productos = Collections.unmodifiableList(new ArrayList<>(productos));
    }

    public void mostrarCarrito(int pedidoId) {
        Pedido pedido = pedidoService.buscarPorId(pedidoId);
        if (pedido == null) {
            System.out.println("No se encontró el pedido.");
            return;
        }

        System.out.println("\n--- CARRITO ---");
        if (pedido.getDetalles().isEmpty()) {
            System.out.println("El carrito está vacío.");
        } else {
            for (DetallePedido detalle : pedido.getDetalles()) {
                System.out.println(detalle.getProducto().getId() + " - "
                        + detalle.getProducto().getNombre()
                        + " | Cantidad: " + detalle.getCantidad()
                        + " | Subtotal: " + detalle.getSubtotal());
            }
        }
        System.out.println("Subtotal: " + pedido.getSubtotal());
        System.out.println("Estado: " + pedido.getEstado());
    }

    public void mostrarMenu(int pedidoId) {
        boolean continuar = true;
        while (continuar) {
            System.out.println("\n1. Mostrar carrito");
            System.out.println("2. Agregar producto");
            System.out.println("3. Modificar cantidad");
            System.out.println("4. Quitar producto");
            System.out.println("5. Revisar y confirmar pedido");
            System.out.println("6. Ver código QR del pedido");
            System.out.println("0. Salir");
            System.out.print("Seleccione una opción: ");
            String opcion = scanner.nextLine();

            switch (opcion) {
                case "1":
                    mostrarCarrito(pedidoId);
                    break;
                case "2":
                    agregarProducto(pedidoId);
                    break;
                case "3":
                    modificarCantidad(pedidoId);
                    break;
                case "4":
                    quitarProducto(pedidoId);
                    break;
                case "5":
                    confirmarPedido(pedidoId);
                    break;
                case "6":                                        // NUEVO (3): acción de la opción
                    mostrarQr(pedidoId);
                    break;
                case "0":
                    continuar = false;
                    break;
                default:
                    System.out.println("Opción no válida.");
            }
        }
    }

    private void agregarProducto(int pedidoId) {
        mostrarProductosDisponibles();
        int productoId = leerEntero("ID del producto: ");
        int cantidad = leerEntero("Cantidad: ");
        Producto producto = buscarProducto(productoId);
        if (pedidoService.agregarProducto(pedidoId, producto, cantidad)) {
            System.out.println("Producto agregado al carrito.");
        } else {
            System.out.println("No se pudo agregar. Verifique el producto, su disponibilidad y la cantidad.");
        }
    }

    private void modificarCantidad(int pedidoId) {
        int productoId = leerEntero("ID del producto: ");
        int cantidad = leerEntero("Nueva cantidad: ");
        if (pedidoService.actualizarCantidad(pedidoId, productoId, cantidad)) {
            System.out.println("Cantidad actualizada.");
        } else {
            System.out.println("No se pudo actualizar la cantidad.");
        }
    }

    private void quitarProducto(int pedidoId) {
        int productoId = leerEntero("ID del producto: ");
        if (pedidoService.quitarProducto(pedidoId, productoId)) {
            System.out.println("Producto quitado del carrito.");
        } else {
            System.out.println("No se pudo quitar el producto.");
        }
    }


    private void confirmarPedido(int pedidoId) {

        Pedido pedido = pedidoService.buscarPorId(pedidoId);

        if (pedido == null) {
            System.out.println("No se encontró el pedido.");
            return;
        }

        if (pedido.getEstado() != EstadoPedido.PENDIENTE_CONFIRMACION) {
            System.out.println("El pedido ya fue confirmado.");
            return;
        }

        // Mostramos el resumen del pedido.
        System.out.println("\n--- RESUMEN DEL PEDIDO ---");
        System.out.println("Cliente: " + pedido.getCliente().getNombre());
        System.out.println("Restaurante: " + (pedido.getRestaurante() == null
                ? "No asignado"
                : pedido.getRestaurante().getNombre()));

        for (DetallePedido detalle : pedido.getDetalles()) {
            System.out.println(detalle.getProducto().getNombre()
                    + " | Cantidad: " + detalle.getCantidad()
                    + " | Subtotal: " + detalle.getSubtotal());
        }

        System.out.println("Total: " + pedido.getSubtotal());

        // Solicitamos la confirmación del cliente.
        System.out.print("¿Confirma el pedido? (s/n): ");

        if (!"s".equalsIgnoreCase(scanner.nextLine().trim())) {
            System.out.println("Confirmación cancelada.");
            return;
        }

        // Solicitamos el met pago
        System.out.println("\n--- MÉTODO DE PAGO ---");
        System.out.println("1. Efectivo");
        System.out.println("0. Cancelar");
        System.out.print("Seleccione una opción: ");

        String metodoPago = scanner.nextLine().trim();

        if (!"1".equals(metodoPago)) {
            System.out.println("Selección cancelada. El pedido sigue pendiente.");
            return;
        }

        // Confirmamos el pedido antes de registrar el pago.
        if (!pedidoService.confirmarPedido(pedidoId)) {
            System.out.println("No se pudo confirmar el pedido.");
            return;
        }

        // Registramos el pago en efectivo.
        Pago pago = pagoService.registrarPago(
                pedidoId,
                MetodoPago.EFECTIVO
        );
        if (pago != null && pago.confirmar()) {

            System.out.println("\n¡Pedido confirmado exitosamente!");
            System.out.println("Método de pago: " + pago.getMetodoPago());
            System.out.println("Total: " + pago.getMonto());
            System.out.println("Se notificó al restaurante "
                    + pedido.getRestaurante().getNombre() + ".");

        } else {

            System.out.println("El pedido fue confirmado, pero no se pudo registrar el pago.");
        }
        mostrarQr(pedidoId);
    }

    private void mostrarQr(int pedidoId) {

        String codigoQr = pedidoService.obtenerQr(String.valueOf(pedidoId));

        if (codigoQr == null) {
            System.out.println("Este pedido aún no tiene un código QR disponible.");
            System.out.println("El QR se genera al confirmar el pedido.");
            return;
        }

        System.out.println("\n--- CÓDIGO QR DE TU PEDIDO ---");
        System.out.println("Pedido #" + pedidoId);
        System.out.println("Código QR: " + codigoQr);
        System.out.println("Preséntalo en el restaurante para reclamar tu pedido.");
    }


    private void mostrarProductosDisponibles() {
        System.out.println("\n--- PRODUCTOS DISPONIBLES ---");
        for (Producto producto : productos) {
            if (producto.isDisponible()) {
                System.out.println(producto.getId() + " - " + producto.getNombre()
                        + " | Precio: " + producto.getPrecio());
            }
        }
    }

    private Producto buscarProducto(int productoId) {
        for (Producto producto : productos) {
            if (producto.getId() == productoId) {
                return producto;
            }
        }
        return null;
    }

    private int leerEntero(String mensaje) {
        while (true) {
            System.out.print(mensaje);
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException exception) {
                System.out.println("Ingrese un número entero válido.");
            }
        }

    }
}