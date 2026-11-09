
package app.config;

import app.domain.Pago;
import app.domain.Pedido;
import app.domain.enums.MetodoPago;
import app.repository.PagoRepositoryMySQL;
import app.repository.PedidoRepositoryMySQL;
import app.service.PagoService;

import java.util.List;

public class PruebaPagoMySQL {

    public static void main(String[] args) {

        PedidoRepositoryMySQL pedidoRepository =
                new PedidoRepositoryMySQL();

        PagoRepositoryMySQL pagoRepository =
                new PagoRepositoryMySQL(pedidoRepository);

        PagoService pagoService =
                new PagoService(pagoRepository, pedidoRepository);

        int pedidoId = 1;

        System.out.println("===== CREAR PAGO CONFIRMADO PARA FACTURA =====");

        Pago nuevoPago = pagoService.registrarPago(
                1,
                MetodoPago.TARJETA
        );

        if (nuevoPago == null) {
            System.out.println("No fue posible registrar el pago.");
            return;
        }

        if (!nuevoPago.confirmar()) {
            System.out.println("No fue posible confirmar el pago.");
            return;
        }

        pagoRepository.actualizar(nuevoPago);

        System.out.println("Pago confirmado correctamente.");
        System.out.println("ID: " + nuevoPago.getId());
        System.out.println("Estado: " + nuevoPago.getEstado());
        System.out.println("Monto: $" + nuevoPago.getMonto());

        System.out.println("===== CONSULTAR PEDIDO =====");

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (pedido == null) {
            System.out.println("No existe el pedido de prueba.");
            return;
        }

        System.out.println("Pedido: " + pedido.getId());
        System.out.println("Restaurante: " + pedido.getRestaurante().getNombre());
        System.out.println("Subtotal: $" + pedido.getSubtotal());

        System.out.println("\n===== CONSULTAR PAGOS ANTERIORES =====");

        List<Pago> pagosAnteriores =
                pagoService.listarPagosPorPedido(pedidoId);

        System.out.println("Pagos encontrados: " + pagosAnteriores.size());

        for (Pago pagoAnterior : pagosAnteriores) {
            System.out.println(
                    "Pago ID: " + pagoAnterior.getId()
                            + " | Estado: " + pagoAnterior.getEstado()
            );

            if (pagoAnterior.getEstado() !=
                    app.domain.enums.EstadoPago.CANCELADO) {

                System.out.println(
                        "El pedido ya tiene un pago activo. "
                                + "No se creará otro."
                );
                return;
            }
        }

        System.out.println("\n===== REGISTRAR PAGO =====");

        Pago pago = pagoService.registrarPago(
                pedidoId,
                MetodoPago.EFECTIVO
        );

        if (pago == null) {
            System.out.println("No fue posible registrar el pago.");
            return;
        }

        System.out.println("Pago registrado correctamente.");
        System.out.println("ID generado: " + pago.getId());
        System.out.println("Método: " + pago.getMetodoPago());
        System.out.println("Monto: $" + pago.getMonto());
        System.out.println("Estado: " + pago.getEstado());

        System.out.println("\n===== BUSCAR PAGO POR ID =====");

        Pago pagoRecuperado =
                pagoService.buscarPorId(pago.getId());

        System.out.println(
                "Pago recuperado: " + pagoRecuperado.getId()
        );

        System.out.println("\n===== CANCELAR PAGO =====");

        boolean cancelado =
                pagoService.cancelarPago(pago.getId());

        System.out.println("¿Cancelación exitosa? " + cancelado);

        Pago pagoActualizado =
                pagoService.buscarPorId(pago.getId());

        System.out.println(
                "Estado actualizado: " + pagoActualizado.getEstado()
        );

        System.out.println("\n===== LISTAR TODOS LOS PAGOS =====");

        List<Pago> todosLosPagos = pagoRepository.listar();

        for (Pago pagoListado : todosLosPagos) {
            System.out.println(
                    "Pago ID: " + pagoListado.getId()
                            + " | Pedido: " + pagoListado.getPedido().getId()
                            + " | Estado: " + pagoListado.getEstado()
            );
        }

        System.out.println("\n===== PRUEBAS FINALIZADAS =====");
    }
}
