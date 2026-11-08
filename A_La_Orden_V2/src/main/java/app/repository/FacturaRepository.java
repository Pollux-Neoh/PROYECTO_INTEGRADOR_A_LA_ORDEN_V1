package app.repository;

import app.domain.Factura;

import java.util.List;

// Define las operaciones de almacenamiento de las facturas.
public interface FacturaRepository {

    // Guarda una factura y devuelve la factura registrada.
    Factura guardar(Factura factura);

    // Busca una factura asociada al ID del pago.
    Factura buscarPorPago(int pagoId);

    // Busca una factura por su identificador.
    Factura buscarPorId(int id);

    // Consulta las facturas asociadas a un pedido.
    List<Factura> listarPorPedido(int pedidoId);

    // Consulta todas las facturas registradas.
    List<Factura> listar();
}