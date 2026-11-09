package app.config;

import app.domain.Factura;
import app.repository.FacturaRepositoryMySQL;
import app.repository.PagoRepositoryMySQL;
import app.repository.PedidoRepositoryMySQL;
import app.service.FacturaService;

import java.util.List;

public class PruebaFacturaMySQL {

    public static void main(String[] args) {

        PedidoRepositoryMySQL pedidoRepository =
                new PedidoRepositoryMySQL();

        PagoRepositoryMySQL pagoRepository =
                new PagoRepositoryMySQL(pedidoRepository);

        FacturaRepositoryMySQL facturaRepository =
                new FacturaRepositoryMySQL();

        FacturaService facturaService =
                new FacturaService(facturaRepository, pagoRepository);

        int pagoId = 2;

        System.out.println("===== GENERAR FACTURA =====");

        Factura factura = facturaService.generarFactura(pagoId);

        if (factura == null) {
            System.out.println(
                    "No fue posible generar la factura."
            );
            return;
        }

        System.out.println("Factura generada correctamente.");
        System.out.println("ID: " + factura.getId());
        System.out.println("Número: " + factura.getNumeroFactura());
        System.out.println("Fecha: " + factura.getFechaGeneracion());
        System.out.println("Cliente: " + factura.getNombreCliente());
        System.out.println("Total: $" + factura.getTotal());

        System.out.println("\n===== DETALLES DE FACTURA =====");

        factura.getDetalles().forEach(detalle -> {
            System.out.println(
                    detalle.getNombreProducto()
                            + " | Cantidad: " + detalle.getCantidad()
                            + " | Precio: $" + detalle.getPrecioUnitario()
                            + " | Subtotal: $" + detalle.getSubtotal()
            );
        });

        System.out.println("\n===== BUSCAR POR ID =====");

        Factura facturaPorId =
                facturaService.buscarPorId(factura.getId());

        System.out.println(
                facturaPorId != null
                        ? facturaPorId.getNumeroFactura()
                        : "Factura no encontrada."
        );

        System.out.println("\n===== BUSCAR POR PAGO =====");

        Factura facturaPorPago =
                facturaService.buscarPorPago(pagoId);

        System.out.println(
                facturaPorPago != null
                        ? facturaPorPago.getNumeroFactura()
                        : "Factura no encontrada."
        );

        System.out.println("\n===== LISTAR POR PEDIDO =====");

        List<Factura> facturasPedido =
                facturaService.listarFacturasPorPedido(1);

        System.out.println(
                "Facturas encontradas: " + facturasPedido.size()
        );

        System.out.println("\n===== LISTAR TODAS =====");

        List<Factura> todasLasFacturas =
                facturaService.listarFacturas();

        for (Factura f : todasLasFacturas) {
            System.out.println(
                    f.getNumeroFactura()
                            + " | Pedido: " + f.getPedidoId()
                            + " | Total: $" + f.getTotal()
            );
        }

        System.out.println("\n===== PRUEBAS FINALIZADAS =====");
    }
}