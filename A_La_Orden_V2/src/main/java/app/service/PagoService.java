package app.service;

import app.domain.Pago;
import app.domain.Pedido;
import app.domain.enums.EstadoPago;
import app.domain.enums.MetodoPago;
import app.repository.PagoRepository;
import app.repository.PedidoRepository;
import app.validators.PagoValidator;

import java.util.List;



// Coordina las operaciones relacionadas con los pagos.
public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoRepository pedidoRepository;

    // Controla la generación de identificadores de pago.
    private int siguienteId;

    public PagoService(PagoRepository pagoRepository,
                       PedidoRepository pedidoRepository) {

        if (pagoRepository == null || pedidoRepository == null) {
            throw new IllegalArgumentException(
                    "Los repositorios son obligatorios."
            );
        }

        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.siguienteId = 1;
    }

    // Registra la selección del método de pago.
    public Pago registrarPago(int pedidoId, MetodoPago metodoPago) {

        // Verifica que se haya seleccionado un método.
        if (metodoPago == null) {
            return null;
        }

        // Busca el pedido.
        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        // Comprueba que el pedido pueda recibir un pago.
        if (!PagoValidator.validarPedidoParaPago(pedido)) {
            return null;
        }

        // Consultamos los pagos anteriores.
        List<Pago> pagosAnteriores =
                pagoRepository.listarPorPedido(pedidoId);

        // Evitamos registrar pagos duplicados.
        for (Pago pagoAnterior : pagosAnteriores) {
            if (pagoAnterior.getEstado() != EstadoPago.CANCELADO) {
                return null;
            }
        }

        // Validamos el monto del pedido.
        if (!PagoValidator.validarMonto(pedido, pedido.getSubtotal())) {
            return null;
        }

        // Creamos el nuevo pago.
        Pago nuevoPago = new Pago(
                siguienteId,
                pedido,
                metodoPago,
                pedido.getSubtotal()
        );

        // Guardamos el pago.
        pagoRepository.guardar(nuevoPago);
        siguienteId++;

        return nuevoPago;
    }

    // Consulta un pago mediante su identificador.
    public Pago buscarPorId(int pagoId) {
        return pagoRepository.buscarPorId(pagoId);
    }

    // Cancela un pago pendiente.
    public boolean cancelarPago(int pagoId) {

        Pago pago = pagoRepository.buscarPorId(pagoId);

        if (pago == null || !pago.cancelar()) {
            return false;
        }

        pagoRepository.actualizar(pago);

        return true;
    }

    // Consulta los pagos asociados a un pedido.
    public List<Pago> listarPagosPorPedido(int pedidoId) {
        return pagoRepository.listarPorPedido(pedidoId);
    }


    // Registra y confirma un pago simulado con tarjeta.
    public Pago registrarPagoTarjeta(
            int pedidoId,
            String numeroTarjeta,
            String titular,
            String fechaVencimiento,
            String cvv) {

        // Validamos los datos de la tarjeta.
        if (!PagoValidator.validarDatosTarjeta(
                numeroTarjeta, titular, fechaVencimiento, cvv)) {
            return null;
        }

        // Registramos el pago con tarjeta.
        Pago pago = registrarPago(pedidoId, MetodoPago.TARJETA);

        if (pago == null) {
            return null;
        }

        // Simulamos la aprobación de la transacción.
        if (!pago.confirmar()) {
            return null;
        }

        pagoRepository.actualizar(pago);

        return pago;
    }


}