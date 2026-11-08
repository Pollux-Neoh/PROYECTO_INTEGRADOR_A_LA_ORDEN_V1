package app.view;

import app.domain.AdministradorRestaurante;
import app.domain.Pedido;
import app.domain.Pago;
import app.domain.DetallePedido;
import app.domain.enums.EscanerQr;
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

// Escanear QR del cliente y validar la entrega
private void escanearQr() {

    System.out.println("\n=== ESCANEAR QR DEL CLIENTE ===");
    System.out.println("Ingrese el código QR (o 0 para cancelar): ");

    String codigoQr = scanner.nextLine().trim();

    if (codigoQr.equals("0")) {
        System.out.println("Escaneo cancelado.");
        return;
    }

    int restauranteId = administrador.getRestaurante().getId();

    EscanerQr resultado = pedidoService.escanearQr(codigoQr, restauranteId);

    mostrarResultadoEscaneo(resultado);
}

// Mostrar resultado de la validación del QR
private void mostrarResultadoEscaneo(EscanerQr resultado) {

    switch (resultado) {

        case ENTREGADO:
            System.out.println("¡QR válido! El pedido fue marcado como ENTREGADO.");
            break;

        case QR_INVALIDO:
            System.out.println("Error: el código QR no es válido.");
            break;

        case PEDIDO_NO_ACTIVO:
            System.out.println("Error: el pedido no está activo "
                    + "(ya fue entregado, rechazado o no se ha confirmado).");
            break;

        case RESTAURANTE_INCORRECTO:
            System.out.println("Error: este pedido pertenece a otro restaurante.");
            break;
    }
}

// Terminar preparación del pedido
private void terminarPedido() {

    int pedidoId = leerPedidoId();

    if (pedidoService.terminarPedido(
            pedidoId,
            administrador.getRestaurante().getId()
    )) {
        System.out.println("¡Preparación finalizada!");
        System.out.println("El pedido ahora está TERMINADO.");
        System.out.println("Recuerda marcarlo como LISTO cuando esté disponible para recoger.");
    } else {
        System.out.println("No se pudo terminar el pedido.");
        System.out.println("Verifica el ID y el estado del pedido.");
    }
}

    //Marcar como listo para recoger
    private void marcarPedidoComoListo() {

        int pedidoId = leerPedidoId();

        if (pedidoService.marcarPedidoComoListo(
                pedidoId,
                administrador.getRestaurante().getId()
        )) {
            System.out.println("¡Pedido marcado como LISTO!");
            System.out.println("El cliente ha recibido una notificación.");
        } else {
            System.out.println("No se pudo marcar el pedido como listo.");
            System.out.println("Verifica que el pedido esté en estado TERMINADO.");
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

System.out.println("\n1. Ver pedidos entrantes");
System.out.println("2. Aceptar pedido");
System.out.println("3. Rechazar pedido");
System.out.println("4. Terminar preparación del pedido");
System.out.println("5. Marcar pedido como listo");
System.out.println("6. Escanear QR del cliente");
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
                    
                    case "4":
                        terminarPedido();
                        break;

                    case "5":
                        marcarPedidoComoListo();
                        break;

                    case "6":
                        escanearQr();
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
