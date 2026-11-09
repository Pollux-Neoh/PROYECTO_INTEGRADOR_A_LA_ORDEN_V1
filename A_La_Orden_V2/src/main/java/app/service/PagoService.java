package app.service;

import app.domain.Pago;
import app.domain.Pedido;
import app.domain.enums.EstadoPago;
import app.domain.enums.MetodoPago;
import app.repository.PagoRepository;
import app.service.OutputPort.PedidoOutport;
import app.validators.PagoValidator;

import java.util.List;

public class PagoService {

    private final PagoRepository pagoRepository;
    private final PedidoOutport pedidoRepository;

    public PagoService(PagoRepository pagoRepository, PedidoOutport pedidoRepository) {

        if (pagoRepository == null || pedidoRepository == null) {
            throw new IllegalArgumentException("Los repositorios son obligatorios.");
        }

        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
    }

    // Confirma un pago y persiste el cambio en MySQL.
    public boolean confirmarPago(Pago pago) {

        if (pago == null || !pago.confirmar()) {
            return false;
        }

        pagoRepository.actualizar(pago);

        return true;
    }

    // Registra un pago para un pedido.
    public Pago registrarPago(int pedidoId, MetodoPago metodoPago) {

        if (metodoPago == null) {
            return null;
        }

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        if (!PagoValidator.validarPedidoParaPago(pedido)) {
            return null;
        }

        List<Pago> pagosAnteriores = pagoRepository.listarPorPedido(pedidoId);

        for (Pago pagoAnterior : pagosAnteriores) {
            if (pagoAnterior.getEstado() != EstadoPago.CANCELADO) {
                return null;
            }
        }

        if (!PagoValidator.validarMonto(pedido, pedido.getSubtotal())) {
            return null;
        }

        Pago nuevoPago = Pago.crearNuevo(
                pedido,
                metodoPago,
                pedido.getSubtotal()
        );

        return pagoRepository.guardar(nuevoPago);
    }

    // Busca un pago por su identificador.
    public Pago buscarPorId(int pagoId) {
        return pagoRepository.buscarPorId(pagoId);
    }

    // Cancela un pago existente.
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

    // Registra y confirma un pago realizado con tarjeta.
    public Pago registrarPagoTarjeta(
            int pedidoId,
            String numeroTarjeta,
            String titular,
            String fechaVencimiento,
            String cvv
    ) {

        if (!PagoValidator.validarDatosTarjeta(
                numeroTarjeta,
                titular,
                fechaVencimiento,
                cvv
        )) {
            return null;
        }

        Pago pago = registrarPago(pedidoId, MetodoPago.TARJETA);

        if (pago == null) {
            return null;
        }

        if (!pago.confirmar()) {
            return null;
        }

        pagoRepository.actualizar(pago);

        return pago;
    }
}