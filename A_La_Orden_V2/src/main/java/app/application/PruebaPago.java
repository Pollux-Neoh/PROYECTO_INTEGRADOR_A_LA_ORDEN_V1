
package app.application;

import app.domain.Cliente;
import app.domain.Pago;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.Restaurante;

import app.repository.PagoRepository;
import app.repository.PedidoRepository;
import app.repository.PagoRepositoryMemoria;

import app.service.PagoService;
import app.service.PedidoService;

import java.math.BigDecimal;

public class PruebaPago {

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

        // 3. Creamos un producto.
        Producto producto = new Producto(
                1,
                "Hamburguesa",
                "Hamburguesa especial",
                new BigDecimal("35000"),
                true,
                "Comida rápida"
        );

        // 4. Creamos los repositorios.
        PedidoRepository pedidoRepository = new PedidoRepository();
        PagoRepository pagoRepository = new PagoRepositoryMemoria();

        // 5. Creamos los servicios.
        PedidoService pedidoService =
                new PedidoService(pedidoRepository);

        PagoService pagoService =
                new PagoService(pagoRepository, pedidoRepository);

        // 6. Creamos el pedido.
        Pedido pedido = pedidoService.crearPedido(
                100,
                cliente,
                restaurante
        );

        // 7. Agregamos el producto.
        boolean agregado = pedidoService.agregarProducto(
                pedido.getId(),
                producto,
                1
        );

        // 8. Confirmamos el pedido.
        boolean confirmado = pedidoService.confirmarPedido(
                pedido.getId()
        );

        // 9. Registramos el pago en efectivo.
        Pago pago = pagoService.registrarPagoEfectivo(
                pedido.getId()
        );

        // 10. Mostramos los resultados.
        System.out.println("===== PRUEBA US-08 =====");

        // Intentamos registrar un segundo pago mientras el primero está pendiente.
        Pago segundoPagoPendiente = pagoService.registrarPagoEfectivo(100);

        System.out.println(
                "Segundo pago mientras existe uno pendiente: " + segundoPagoPendiente
        );

        System.out.println("Producto agregado: " + agregado);
        System.out.println("Pedido confirmado: " + confirmado);

        if (pago != null) {
            System.out.println("ID del pago: " + pago.getId());
            System.out.println("Método: " + pago.getMetodoPago());
            System.out.println("Monto: $" + pago.getMonto());
            System.out.println("Estado: " + pago.getEstado());
        } else {
            System.out.println("No fue posible registrar el pago.");
        }

        // Intentamos registrar un pago para un pedido inexistente.
        Pago pagoInexistente = pagoService.registrarPagoEfectivo(999);

        System.out.println(
                "Pago para pedido inexistente: " + pagoInexistente
        );

        // Creamos un segundo pedido para probar el pago
        // sin confirmar previamente el pedido.
        Pedido pedidoSinConfirmar = pedidoService.crearPedido(200, cliente, restaurante);

        // Agregamos un producto, pero NO confirmamos el pedido.
        pedidoService.agregarProducto(200, producto, 1);

        // Intentamos registrar el pago sin confirmar el pedido.
        Pago pagoSinConfirmar = pagoService.registrarPagoEfectivo(200);

        System.out.println(
                "Pago para pedido sin confirmar: " + pagoSinConfirmar
        );


        // Simulamos que el pago fue confirmado.
        // Esto es únicamente para probar la validación.
        pago.confirmar();

        // Intentamos cancelar un pago que ya fue confirmado.
        boolean cancelarPagoConfirmado = pagoService.cancelarPago(pago.getId());

        System.out.println(
                "¿Se pudo cancelar un pago confirmado?: " + cancelarPagoConfirmado
        );

        // Verificamos el estado del pago original.
        System.out.println("Estado del pago original: " + pago.getEstado());

        // Verificamos el estado del pago guardado en el repositorio.
        System.out.println(
                "Estado del pago en repositorio: "
                        + pagoService.buscarPorId(1).getEstado()
        );

        // Consultamos los pagos registrados para el pedido 100.
        System.out.println(
                "Cantidad de pagos del pedido 100: "
                        + pagoService.listarPagosPorPedido(100).size()
        );

        // Mostramos el estado de cada pago encontrado.
        for (Pago pagoRegistrado : pagoService.listarPagosPorPedido(100)) {
            System.out.println(
                    "Pago encontrado: ID " + pagoRegistrado.getId()
                            + " | Estado: " + pagoRegistrado.getEstado()
            );
        }

        // Intentamos registrar otro pago para el mismo pedido.
        Pago pagoDuplicado = pagoService.registrarPagoEfectivo(100);

        System.out.println(
                "Segundo pago para pedido con pago confirmado: " + pagoDuplicado
        );
        // ===== PRUEBA DE CANCELACIÓN Y NUEVO INTENTO =====

        // Creamos un pedido independiente.
        Pedido pedidoCancelacion = pedidoService.crearPedido(
                300,
                cliente,
                restaurante
        );

        //Agregamos un producto y confirmamos el pedido.
        pedidoService.agregarProducto(300, producto, 1);
        pedidoService.confirmarPedido(300);

        // Registramos el primer pago.
        Pago pagoParaCancelar = pagoService.registrarPagoEfectivo(300);

        System.out.println("Primer pago del pedido 300: " + pagoParaCancelar.getEstado());

        // Cancelamos el primer pago.
        boolean cancelado = pagoService.cancelarPago(pagoParaCancelar.getId());

        System.out.println("¿Pago cancelado? " + cancelado);
        System.out.println("Estado después de cancelar: " + pagoParaCancelar.getEstado());

        // Intentamos cancelar nuevamente un pago que ya está cancelado.
        boolean cancelarPagoNuevamente = pagoService.cancelarPago(pagoParaCancelar.getId());

        System.out.println(
                "¿Se pudo cancelar nuevamente?: " +  cancelarPagoNuevamente
        );

        // Intentamos registrar un nuevo pago.
        Pago nuevoPago = pagoService.registrarPagoEfectivo(300);

        System.out.println(
                "¿Se permitió un nuevo pago?: " + (nuevoPago != null)
        );

        if (nuevoPago != null) {
            System.out.println("Estado del nuevo pago: " + nuevoPago.getEstado());
        }
    }
}