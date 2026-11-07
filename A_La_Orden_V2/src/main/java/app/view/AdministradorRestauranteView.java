package app.view;

import app.domain.AdministradorRestaurante;
import app.domain.Pedido;
import app.domain.Pago;
import app.domain.DetallePedido;

import app.service.PedidoService;
import app.service.PagoService;

import java.util.List;
import java.util.Set;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.ConcurrentHashMap;

public class AdministradorRestauranteView {

    private final Scanner scanner;
    private final AdministradorRestaurante administrador;
    private final PedidoService pedidoService;
    private final PagoService pagoService;

    // Controla la consulta periódica de pedidos.
    private ScheduledExecutorService actualizadorPedidos;

    // Evita anunciar varias veces la misma comanda.
    private final Set<Integer> pedidosYaNotificados =
            ConcurrentHashMap.newKeySet();

    public AdministradorRestauranteView(
            AdministradorRestaurante administrador,
            PedidoService pedidoService,
            PagoService pagoService
    ){
        this.scanner = new Scanner(System.in);
        this.administrador = administrador;
        this.pedidoService = pedidoService;
        this.pagoService = pagoService;
    }


    private void iniciarMonitoreoPedidos() {

        int restauranteId = administrador.getRestaurante().getId();

        // Registramos los pedidos que ya existían antes de iniciar el monitoreo.
        List<Pedido> pedidosExistentes =
                pedidoService.obtenerPedidosEntrantes(restauranteId);

        for (Pedido pedido : pedidosExistentes) {
            pedidosYaNotificados.add(pedido.getId());
        }

        actualizadorPedidos = Executors.newSingleThreadScheduledExecutor();

        // Consultamos nuevamente cada cinco segundos.
        actualizadorPedidos.scheduleAtFixedRate(() -> {

            try {
                List<Pedido> pedidos =
                        pedidoService.obtenerPedidosEntrantes(restauranteId);

                for (Pedido pedido : pedidos) {

                    // Solo anunciamos pedidos que todavía no conocemos.
                    if (pedidosYaNotificados.add(pedido.getId())) {

                        System.out.println("\n");
                        System.out.println("====================================");
                        System.out.println("      ¡NUEVA COMANDA RECIBIDA!");
                        System.out.println("====================================");
                        System.out.println("Pedido #" + pedido.getId());
                        System.out.println("Cliente: "
                                + pedido.getCliente().getNombre());
                        System.out.println("Total: " + pedido.getSubtotal());
                        System.out.println("Estado: " + pedido.getEstado());
                        System.out.println("Consulte la opción 1 para ver el detalle.");
                        System.out.println("====================================");
                    }
                }

            } catch (Exception e) {
                System.out.println("Error al consultar nuevos pedidos: "
                        + e.getMessage());
            }

        }, 5, 5, TimeUnit.SECONDS);
    }

    private void detenerMonitoreoPedidos(){

        if(actualizadorPedidos != null){
            actualizadorPedidos.shutdownNow();
            actualizadorPedidos = null;
        }
    }

    public void mostrarPedidoEntrantes(){

        int restauranteId = administrador.getRestaurante().getId();

        List<Pedido> pedidos = pedidoService.obtenerPedidosEntrantes(restauranteId);

        System.out.println("\n=======================");
        System.out.println("     PEDIDOS ENTRANTES");
        System.out.println("=================");

        if (pedidos.isEmpty()){
            System.out.println("No hay pedidos pendientes por atender.");
            return;
        }

        for (Pedido pedido : pedidos) {

            System.out.println("\nPedido #" + pedido.getId());
            System.out.println("Cliente: " + pedido.getCliente().getNombre());

            System.out.println("\nProductos:");

            for (DetallePedido detalle : pedido.getDetalles()) {

                System.out.println("- " + detalle.getProducto().getNombre()
                        + "| Cantidad: " + detalle.getCantidad()
                        + "| Subtotal: " + detalle.getSubtotal());
            }

            System.out.println("Total: " + pedido.getSubtotal());

            List<Pago> pagos = pagoService.listarPagosPorPedido(pedido.getId());

            if(pagos.isEmpty()){
                System.out.println("Método de pago: Sin registrar");
            } else {
                Pago pagoActual = pagos.get(pagos.size() - 1);
                System.out.println("Método de pago: " + pagoActual.getMetodoPago());
            }

            System.out.println("Estado: " + pedido.getEstado());
            System.out.println("------------------------");
        }
    }

    private void aceptarPedido() {

        int pedidoId = leerPedidoId();

        int restauranteId = administrador.getRestaurante().getId();

        if (pedidoService.aceptarPedido(pedidoId, restauranteId)) {
            System.out.println("¡Pedido aceptado correctamente!");
            System.out.println("El pedido ahora está EN_PROCESO.");
        } else {
            System.out.println("No se pudo aceptar el pedido.");
            System.out.println("Verifique el ID, el restaurante y el estado.");
        }
    }

    private void rechazarPedido() {

        int pedidoId = leerPedidoId();

        int restauranteId = administrador.getRestaurante().getId();

        if (pedidoService.rechazarPedido(pedidoId, restauranteId)) {
            System.out.println("Pedido rechazado correctamente.");
        } else {
            System.out.println("No se pudo rechazar el pedido.");
            System.out.println("Verifique el ID, el restaurante y el estado.");
        }
    }

    private int leerPedidoId() {

        while (true) {
            System.out.println("Ingrese el ID del pedido: ");

            try {
                int pedidoId = Integer.parseInt(scanner.nextLine());

                if (pedidoId > 0) {
                    return pedidoId;
                }

                System.out.println("El ID debe ser mayor que cero.");
            } catch (NumberFormatException exception) {
                System.out.println("Ingrese un número válido");
            }
        }
    }


    public void mostrarMenu() {

        boolean continuar = true;

        // Iniciamos el monitoreo automático al entrar al menú.
        iniciarMonitoreoPedidos();


        try {

            while (continuar) {

                System.out.println("\n======================");
                System.out.println(" ADMINISTRADOR DEL RESTAURANTE");
                System.out.println("==========================");
                System.out.println("Restaurante: "
                        + administrador.getRestaurante().getNombre());

                System.out.println("\n1. Ver pedidos Entrantes");
                System.out.println("2. Aceptar pedido");
                System.out.println("3. Rechazar pedido");
                System.out.println("0. Cerrar sesión");

                System.out.println("\nSeleccione una opción: ");

                String opcion = scanner.nextLine();

                switch (opcion) {

                    case "1":
                        mostrarPedidoEntrantes();
                        break;

                    case "2":
                        aceptarPedido();
                        break;

                    case "3":
                        rechazarPedido();
                        break;

                    case "0":
                        continuar = false;
                        System.out.println("Sesión del administrador finalizada.");
                        break;

                    default:
                        System.out.println("Opción no válida.");
                }
            }

        } finally {

            // Detenemos el monitoreo al cerrar la sesión.
            detenerMonitoreoPedidos();
        }
    }

}
