package app.application;

import app.domain.Cliente;
import app.domain.Pedido;
import app.domain.Producto;
import app.domain.Restaurante;
import app.domain.enums.EscanerQr;
import app.domain.enums.EstadoPedido;
import app.repository.PedidoRepository;
import app.service.PedidoService;

import java.math.BigDecimal;

public class PruebaEscaneoQr {

    public static void main(String[] args) {

        Cliente cliente = new Cliente(1, "Camilo", "camilo@gmail.com",
                "123456", "3001234567", true, "Medellín");
        Restaurante restaurante = new Restaurante(1, "Restaurante de prueba",
                "", 4.5, 2.0, "Comida rápida");
        Producto producto = new Producto(1, "Hamburguesa", "Hamburguesa especial",
                new BigDecimal("35000"), true, "Comida rápida");

        PedidoService service = new PedidoService(new PedidoRepository());

        // Preparación: pedido confirmado con QR
        Pedido pedido = service.crearPedido(cliente, restaurante);
        service.agregarProducto(pedido.getId(), producto, 1);
        service.confirmarPedido(pedido.getId());
        String qr = service.obtenerQr(String.valueOf(pedido.getId()));

        System.out.println("Estado inicial: " + pedido.getEstado());
        System.out.println("QR generado: " + qr);

        if (qr == null) {
            System.out.println("PRUEBA DETENIDA: el pedido no tiene QR. "
                    + "Falta generarlo al confirmar (US-11).");
            return;
        }

        // Casos de prueba
        verificar("QR vacío", EscanerQr.QR_INVALIDO,
                service.escanearQr("", 1));
        verificar("QR inexistente", EscanerQr.QR_INVALIDO,
                service.escanearQr("NO-EXISTE", 1));
        verificar("Restaurante equivocado", EscanerQr.RESTAURANTE_INCORRECTO,
                service.escanearQr(qr, 2));
        verificar("QR correcto", EscanerQr.ENTREGADO,
                service.escanearQr(qr, 1));
        verificar("Estado final del pedido", EstadoPedido.ENTREGADO,
                pedido.getEstado());
        verificar("Escanear dos veces", EscanerQr.PEDIDO_NO_ACTIVO,
                service.escanearQr(qr, 1));

        // Pedido aceptado (EN_PROCESO) sí se puede entregar
        Pedido pedido2 = service.crearPedido(cliente, restaurante);
        service.agregarProducto(pedido2.getId(), producto, 1);
        service.confirmarPedido(pedido2.getId());
        String qr2 = service.obtenerQr(String.valueOf(pedido2.getId()));
        service.aceptarPedido(pedido2.getId(), 1);
        verificar("Pedido EN_PROCESO", EscanerQr.ENTREGADO,
                service.escanearQr(qr2, 1));

// Pedido rechazado no se puede entregar
        Pedido pedido3 = service.crearPedido(cliente, restaurante);
        service.agregarProducto(pedido3.getId(), producto, 1);
        service.confirmarPedido(pedido3.getId());
        String qr3 = service.obtenerQr(String.valueOf(pedido3.getId()));
        service.rechazarPedido(pedido3.getId(), 1);
        verificar("Pedido RECHAZADO", EscanerQr.PEDIDO_NO_ACTIVO,
                service.escanearQr(qr3, 1));
        // NUEVO: el QR es único por pedido
        verificar("QR único por pedido", true, !qr.equals(qr2) && !qr2.equals(qr3));
        Pedido pedido4 = service.crearPedido(cliente, restaurante);
        service.agregarProducto(pedido4.getId(), producto, 1);
        service.confirmarPedido(pedido4.getId());
        service.aceptarPedido(pedido4.getId(), 1);
        verificar("QR visible EN_PROCESO", true,
                service.obtenerQr(String.valueOf(pedido4.getId())) != null); verificar("buscarPorId encuentra el pedido", true,
                service.buscarPorId(pedido.getId()) != null);


}

    private static void verificar(String caso, Object esperado, Object obtenido) {
        String resultado = esperado.equals(obtenido) ? "OK   " : "FALLÓ";
        System.out.println(resultado + " | " + caso
                + " | esperado: " + esperado + " | obtenido: " + obtenido);
    }

}