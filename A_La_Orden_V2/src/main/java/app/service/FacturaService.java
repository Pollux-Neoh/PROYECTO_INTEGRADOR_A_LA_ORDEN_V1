package app.service;

import app.domain.Cliente;
import app.domain.DetalleFactura;
import app.domain.DetallePedido;
import app.domain.Factura;
import app.domain.Pago;
import app.domain.Pedido;
import app.repository.FacturaRepository;
import app.repository.PagoRepository;
import app.validators.FacturaValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Coordina la generación y consulta facturas.
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;

    private int siguienteId;
    public FacturaService(FacturaRepository facturaRepository,
                          PagoRepository pagoRepository) {

        if (facturaRepository == null || pagoRepository == null) {
            throw new IllegalArgumentException(
                    "Los repositorios son obligatorios."
            );
        }

        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
        this.siguienteId = 1;
    }

    // Genera una factura a partir de un pago confirmado.
    public Factura generarFactura(int pagoId){

        // Buscamos el pago.
        Pago pago = pagoRepository.buscarPorId(pagoId);

        // Validamos que el pago pueda generar una factura.
        if (!FacturaValidator.validarPagoParaFacturar(pago)) {
            return null;
        }

        // Evitamos generar una segunda factura para el mismo pago.
        if (facturaRepository.buscarPorPago(pagoId) != null){
            return null;
        }

        // Obtenemos el pedido asociado al pago.
        Pedido pedido = pago.getPedido();

        if (!FacturaValidator.validarPedidoParaFacturar(pedido)) {
            return null;
        }
        // Validamos que el monto del pago coincida con el subtotal.
        if (!FacturaValidator.validarMonto(
                pago.getMonto(),
                pedido.getSubtotal())) {
            return null;
        }

        // Obtenemos el cliente asociado al pedido.
        Cliente cliente = pedido.getCliente();

        if (cliente == null) {
            return null;
        }
        // Construimos los detalles históricos de la factura.
        List<DetalleFactura> detalles = new ArrayList<>();

        for (DetallePedido detallePedido : pedido.getDetalles()){

            DetalleFactura detalleFactura = new DetalleFactura(
                    detallePedido.getProducto().getId(),
                    detallePedido.getProducto().getNombre(),
                    detallePedido.getCantidad(),
                    detallePedido.getProducto().getPrecio()
            );
            detalles.add(detalleFactura);
        }

        // Generamos el número de factura.
        String numeroFactura = String.format(
                "FAC-%06d",
                siguienteId
        );

        // Creamos la factura.
        Factura factura = new Factura(
                siguienteId,
                numeroFactura,
                LocalDateTime.now(),
                pedido.getId(),
                pago.getId(),
                cliente,
                detalles
        );

        // Comprobamos nuevamente que el total sea consistente.
        if (!FacturaValidator.validarMonto(
                pago.getMonto(),
                factura.getTotal())) {
            return null;
        }

        // Guardamos la factura.
        facturaRepository.guardar(factura);

        siguienteId++;

        return factura;
    }

    // Consulta una factura por su identificador.
    public Factura buscarPorId(int facturaId){
        return facturaRepository.buscarPorId(facturaId);
    }

    // Consulta una factura asociada a un pago.
    public Factura buscarPorPago(int pagoId){
        return facturaRepository.buscarPorPago(pagoId);
    }

    // Consulta las factura asociadas a un pedido.
    public List<Factura> listarFacturasPorPedido(int pedidoId){
        return facturaRepository.listarPorPedido(pedidoId);
    }

    // Consulta todas las facturas.
    public List<Factura> listarFacturas(){
        return facturaRepository.listar();
    }
}
