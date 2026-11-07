package app.application;

import app.domain.Cliente;
import app.domain.Factura;
import app.domain.Pago;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.Restaurante;

import app.domain.enums.EscanerQr;
import app.domain.enums.MetodoPago;
import app.repository.FacturaRepository;
import app.repository.FacturaRepositoryMemoria;
import app.repository.PagoRepository;
import app.repository.PagoRepositoryMemoria;
import app.repository.PedidoRepository;

import app.service.FacturaService;
import app.service.PagoService;
import app.service.PedidoService;
import app.service.NotificacionService;

import app.domain.NotificacionCliente;


import java.math.BigDecimal;

import app.view.HistorialView;

public class PruebaFactura {

    public static void main(String[] args) {

        // 1. Creamos el cliente.
        Cliente cliente = new Cliente(
                1,
                "Camilo",
                "camilo@gmail.com",
                "123456",
                "3001234567",
                true,
                "Medellín"
        );

        // 2. Creamos el restaurante.
        Restaurante restaurante = new Restaurante(
          1,
          "Restaurante de prueba",
          "",
          4.5,
          2.0,
          "Comida rápida"
        );

        // 3. Creamos los productos.
        Producto hamburguesa = new Producto(
                1,
                "Hamburguesa",
                "Hamburguesa especial",
                new BigDecimal("35000"),
                true,
                "Comida rápida"
        );

        Producto gaseosa = new Producto(
              2,
              "Gaseosa",
              "Gaseosa personal",
              new BigDecimal("5000"),
              true,
              "Bebidas"
        );

        // 4. Creamos los repositorios.
        PedidoRepository pedidoRepository =
                new PedidoRepository();
        PagoRepository pagoRepository =
                new PagoRepositoryMemoria();
        FacturaRepository facturaRepository =
                new FacturaRepositoryMemoria();

        // 5. Creamos los servicios.
        NotificacionService notificacionService = new NotificacionService();

        PedidoService pedidoService =
                new PedidoService(pedidoRepository, notificacionService);
        PagoService pagoService =
                new PagoService(pagoRepository, pedidoRepository);

        FacturaService facturaService = new FacturaService(facturaRepository, pagoRepository);

        // 6. Creamos la vista del historial.
        HistorialView historialView = new HistorialView(
                facturaService,
                pedidoRepository
        );

        // 7. Creamos el pedido.
        Pedido pedido = pedidoService.crearPedido(
                100,
                cliente,
                restaurante
        );

        // 8. Agregamos los productos.
        pedidoService.agregarProducto(100, hamburguesa, 2);
        pedidoService.agregarProducto(100, gaseosa, 1);

        // 9. Confirmamos el pedido.
        boolean confirmado =
                pedidoService.confirmarPedido(100);

        // 10. Registramos el pago.
        Pago pago = pagoService.registrarPago(100, MetodoPago.EFECTIVO);

        // 11. Simulamos la confirmación del pago.
        // Esto representa la responsabilidad de US-22.
        if (pago != null){
            pago.confirmar();
        }

        // 12. Generamos la factura.
        Factura factura = null;

        if (pago != null) {
            factura = facturaService.generarFactura(pago.getId());
        }

        // 13. Mostramos los resultados.
        System.out.println("==== PRUEBA US-07 ====");

        System.out.println("Pedido confirmado: " + confirmado);

        if (pago != null) {
            System.out.println("Estado del pago: " + pago.getEstado());
        }

        if (factura != null) {

            System.out.println("Factura generada correctamente.");
            System.out.println("Número: " + factura.getNumeroFactura());
            System.out.println("ID: " + factura.getId());
            System.out.println("Cliente: " + factura.getNombreCliente());
            System.out.println("Pedido: " + factura.getPedidoId());
            System.out.println("Total: $" + factura.getTotal());

            System.out.println("--- DETALLE DE FACTURA ---");

            for (var detalle : factura.getDetalles()) {
                System.out.println(
                        detalle.getNombreProducto()
                        + " | Cantidad: " + detalle.getCantidad()
                        + " | Precio: $" + detalle.getPrecioUnitario()
                        + " | Subtotal: $" + detalle.getSubtotal()
                );
            }
        } else {
            System.out.println("No fue posible generar la factura.");
        }

        // 14. Consultamos la factura por el pago.
        System.out.println(
                "Factura encontrada por pago: " + (facturaService.buscarPorPago(pago.getId()) != null)
        );

        // 15. Consultamos las facturas del pedido.
        System.out.println(
                "Facturas del pedido: " + facturaService.listarFacturasPorPedido(100).size()
        );

        // 16. Intentamos generar una factura duplicada.
        Factura facturaDuplicada =
                facturaService.generarFactura(pago.getId());

        System.out.println(
                "¿Se generó una factura duplicada?: " + (facturaDuplicada != null)
        );

        // 17. Creamos otro pedido para probar un pago pendiente.
        Pedido pedidoPendiente = pedidoService.crearPedido(
                200,
                cliente,
                restaurante
        );

        pedidoService.agregarProducto(200, hamburguesa, 1);
        pedidoService.confirmarPedido(200);

        Pago pagoPendiente = pagoService.registrarPago(200, MetodoPago.EFECTIVO);

        // No confirmamos el pago.
        // Intentamos generar una factura.
        Factura facturaPagoPendiente = facturaService.generarFactura(pagoPendiente.getId());

        if (facturaPagoPendiente != null) {
            System.out.println(
                    "Factura generada: " + facturaPagoPendiente.getId()
            );
        } else {
            System.out.println(
                    "No se generó factura porque el pago está pendiente."
            );
        }

        // Consultamos el historial del cliente.
        System.out.println("\n==== HISTORIAL DEL CLIENTE ====");

        historialView.mostrarHistorial(cliente);


        // ===== PRUEBA US-21: MARCAR PEDIDO COMO LISTO ===



        System.out.println("\n===== PRUEBA US-21 =====");

// 1. Creamos un pedido nuevo reutilizando los datos existentes.
        Pedido pedidoListo = pedidoService.crearPedido(
                300,
                cliente,
                restaurante
        );

// 2. Agregamos un producto.
        pedidoService.agregarProducto(300, hamburguesa, 1);

// 3. Confirmamos el pedido.
        boolean confirmadoListo = pedidoService.confirmarPedido(300);

        System.out.println("Pedido confirmado: " + confirmadoListo);

// 4. El restaurante acepta el pedido.
        boolean aceptado = pedidoService.aceptarPedido(
                300,
                restaurante.getId()
        );

        System.out.println("Pedido aceptado: " + aceptado);
        System.out.println("Estado actual: " + pedidoListo.getEstado());

// 5. El restaurante termina la preparación.
        boolean terminado = pedidoService.terminarPedido(300);

        System.out.println("Pedido terminado: " + terminado);
        System.out.println("Estado actual: " + pedidoListo.getEstado());

// 6. El restaurante marca el pedido como listo.
        boolean marcadoListo = pedidoService.marcarPedidoComoListo(300, restaurante.getId());

        System.out.println("¿Pedido marcado como listo?: " + marcadoListo);
        System.out.println("Estado final: " + pedidoListo.getEstado());

// 7. Consultamos la notificación enviada al cliente.
        System.out.println("\n--- NOTIFICACIONES DEL CLIENTE ---");

        for (NotificacionCliente notificacion :
                notificacionService.listarNotificacionesCliente()) {

            if (notificacion.getPedidoId() == 300) {
                System.out.println("Mensaje: " + notificacion.getMensaje());
            }
        }

// 8. Consultamos el código QR cuando el pedido está listo.
        String codigoQr = pedidoService.obtenerQr("300");

        System.out.println("\nCódigo QR disponible: " + (codigoQr != null));

// 9. Simulamos el escaneo del QR.
        EscanerQr primerResultado = pedidoService.escanearQr(
                codigoQr,
                pedidoListo.getRestaurante().getId()
        );

        System.out.println("¿Pedido entregado?: "
                + (primerResultado == EscanerQr.ENTREGADO));

        System.out.println("Estado después del escaneo: "
                + pedidoListo.getEstado());

// 10. Intentamos escanear nuevamente el mismo código.
        EscanerQr segundoResultado = pedidoService.escanearQr(
                codigoQr,
                pedidoListo.getRestaurante().getId()
        );

        System.out.println("¿Se permitió un segundo escaneo?: "
                + (segundoResultado == EscanerQr.ENTREGADO));
    }


}