package app.view;

import app.domain.Cliente;
import app.domain.DetalleFactura;
import app.domain.Factura;
import app.domain.Pedido;
import app.repository.PedidoRepository;
import app.service.FacturaService;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

// Presenta el historial de factural del cliente.
public class HistorialView {

    private final FacturaService facturaService;
    private final PedidoRepository pedidoRepository;

    public HistorialView(FacturaService facturaService, PedidoRepository pedidoRepository) {

        this.facturaService = facturaService;
        this.pedidoRepository = pedidoRepository;
    }

    public void mostrarHistorial(Cliente cliente) {

        System.out.println("\n===== HISTORIAL DE FACTURAS =====");

        if (cliente == null) {
            System.out.println("No se pudo identificar el cliente");
            return;
        }

        //Buscamos los pedidos registrados.
        List<Pedido> pedidos = pedidoRepository.listar();

        boolean tieneFacturas = false;

        DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

        // Recorremos los pedidos para identificar los del cliente.
        for (Pedido pedido : pedidos) {

            if (pedido.getCliente() != null
                    && pedido.getCliente().getId() == cliente.getId()) {

                // Consultamos las facturas asociadas al pedido.
                List<Factura> facturas =
                        facturaService.listarFacturasPorPedido(pedido.getId());

                for (Factura factura : facturas) {

                    tieneFacturas = true;

                    System.out.println("\n-----------------");
                    System.out.println("Factura: " + factura.getNumeroFactura());
                    System.out.println("Fecha: " + factura.getFechaGeneracion().format(formatoFecha));
                    System.out.println("Cliente: " + factura.getNombreCliente());
                    System.out.println("Pedido: " + factura.getPedidoId());

                    System.out.println("\nProductos:");

                    for (DetalleFactura detalle : factura.getDetalles()){

                        System.out.println(
                                detalle.getNombreProducto()
                                + " | Cantidad: " + detalle.getCantidad()
                                + " | Subtotal: " + detalle.getSubtotal()
                        );
                    }

                    System.out.println("\nTotal: " + factura.getTotal());
                    System.out.println("------------------");
                }
            }
        }
        if (!tieneFacturas) {
            System.out.println("Todavia no tienes factura registradas.");
        }
    }
}
