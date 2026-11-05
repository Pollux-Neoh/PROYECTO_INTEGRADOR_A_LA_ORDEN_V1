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

    public PagoService(PagoRepository pagoRepository, PedidoRepository pedidoRepository) {

        if (pagoRepository == null || pedidoRepository == null) {
            throw new IllegalArgumentException(
                    "Los respositorios son obligatorios."
            );
        }

        this.pagoRepository = pagoRepository;
        this.pedidoRepository = pedidoRepository;
        this.siguienteId = 1;
    }

    // Registra la selección del mét de pago en efectivo.
    public Pago registrarPagoEfectivo(int pedidoId) {

        Pedido pedido = pedidoRepository.buscarPorId(pedidoId);

        // Comprueba que el pedido pueda recibir un pago.
        if (!PagoValidator.validarPedidoParaPago(pedido)) {
            return null;
        }

        // Consultamos los pagos que ya existen para este pedido.
        List<Pago> pagosAnteriores = pagoRepository.listarPorPedido(pedidoId);

        // Evitamos registrar otro pago si ya existe uno pendiente o confirmado.
        for (Pago pagoAnterior : pagosAnteriores) {

            if (pagoAnterior.getEstado() != EstadoPago.CANCELADO){
                return null;
            }
        }

        // Obtiene el valor que debe pagar el cliente.
        if ((!PagoValidator.validarMonto(pedido, pedido.getSubtotal()))) {
            return null;
        }

        // Crea el intento de pago y lo almacena como pendiente.
        Pago nuevoPago = new Pago(
                siguienteId,
                pedido,
                MetodoPago.EFECTIVO,
                pedido.getSubtotal()
        );

        pagoRepository.guardar(nuevoPago);
        siguienteId++;

        return nuevoPago;
    }

    // Consulta un pago mediante su identificador.
    public Pago buscarPorId(int pagoId){
        return pagoRepository.buscarPorId(pagoId);
    }

    // Cancela un pago pendiente.
    public boolean cancelarPago(int pagoId){

        // Buscamos el pago por su identificador.
        Pago pago = pagoRepository.buscarPorId(pagoId);

        // Verificamos que exista y que pueda cancelarse.
        if (pago == null || !pago.cancelar()) {
            return false;
        }

        // Actualizamos el pago en el repositorio.
        pagoRepository.actualizar(pago);

        return true;
    }

    // Consulta todos los pagos asociados a un pedido.
    public List<Pago> listarPagosPorPedido(int pedidoId) {
        return pagoRepository.listarPorPedido(pedidoId);
    }
}