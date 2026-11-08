package app.service;

import app.domain.Cliente;
import app.domain.DetalleFactura;
import app.domain.DetallePedido;
import app.domain.Factura;
import app.domain.Pago;
import app.domain.Pedido;
import app.domain.Producto;
import app.repository.FacturaRepository;
import app.repository.PagoRepository;
import app.validators.FacturaValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

// Coordina la generación y consulta de facturas.
public class FacturaService {

    private final FacturaRepository facturaRepository;
    private final PagoRepository pagoRepository;

    public FacturaService(
            FacturaRepository facturaRepository,
            PagoRepository pagoRepository) {

        if (facturaRepository == null || pagoRepository == null) {
            throw new IllegalArgumentException(
                    "Los repositorios son obligatorios."
            );
        }

        this.facturaRepository = facturaRepository;
        this.pagoRepository = pagoRepository;
    }

    // Genera una factura a partir de un pago confirmado.
    public Factura generarFactura(int pagoId) {

        // Validamos el identificador del pago.
        if (pagoId <= 0) {
            return null;
        }

        // Buscamos el pago.
        Pago pago = pagoRepository.buscarPorId(pagoId);

        // Validamos que el pago y su pedido puedan generar una factura.
        if (!FacturaValidator.validarPagoParaFacturar(pago)) {
            return null;
        }

        // Evitamos generar una segunda factura para el mismo pago.
        if (facturaRepository.buscarPorPago(pagoId) != null) {
            return null;
        }

        // Obtenemos el pedido asociado al pago.
        Pedido pedido = pago.getPedido();

        // Obtenemos el cliente asociado al pedido.
        Cliente cliente = pedido.getCliente();

        if (cliente == null || cliente.getId() <= 0) {
            return null;
        }

        // Construimos los detalles históricos de la factura.
        List<DetalleFactura> detalles = new ArrayList<>();

        for (DetallePedido detallePedido : pedido.getDetalles()) {

            if (detallePedido == null) {
                return null;
            }

            Producto producto = detallePedido.getProducto();

            if (producto == null) {
                return null;
            }

            DetalleFactura detalleFactura = new DetalleFactura(
                    producto.getId(),
                    producto.getNombre(),
                    detallePedido.getCantidad(),
                    detallePedido.getPrecioUnitario()
            );

            detalles.add(detalleFactura);
        }

        // Creamos una factura nueva sin asignar un ID.
        Factura factura = Factura.crearNueva(
                LocalDateTime.now(),
                pedido.getId(),
                pago.getId(),
                cliente,
                detalles
        );

        // Comprobamos que el monto del pago coincida
        // con el total calculado de la factura.
        if (!FacturaValidator.validarMonto(
                pago.getMonto(),
                factura.getTotal())) {
            return null;
        }

        // El repositorio asigna el ID y devuelve la factura registrada.
        return facturaRepository.guardar(factura);
    }

    // Consulta una factura por su identificador.
    public Factura buscarPorId(int facturaId) {
        return facturaRepository.buscarPorId(facturaId);
    }

    // Consulta una factura asociada a un pago.
    public Factura buscarPorPago(int pagoId) {
        return facturaRepository.buscarPorPago(pagoId);
    }

    // Consulta las facturas asociadas a un pedido.
    public List<Factura> listarFacturasPorPedido(int pedidoId) {
        return facturaRepository.listarPorPedido(pedidoId);
    }

    // Consulta todas las facturas.
    public List<Factura> listarFacturas() {
        return facturaRepository.listar();
    }
}
