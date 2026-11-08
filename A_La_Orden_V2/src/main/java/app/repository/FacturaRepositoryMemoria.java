package app.repository;

import app.domain.Factura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Implementa el almacenamiento temporal de facturas en memoria.
public class FacturaRepositoryMemoria implements FacturaRepository {

    private final Map<Integer, Factura> facturas;
    private int siguienteId;

    public FacturaRepositoryMemoria() {
        facturas = new LinkedHashMap<>();
        siguienteId = 1;
    }

    // Guarda una factura y devuelve la factura registrada.
    @Override
    public Factura guardar(Factura factura) {

        if (factura == null) {
            throw new IllegalArgumentException(
                    "La factura no puede ser nula."
            );
        }

        if (factura.getId() != 0) {
            throw new IllegalArgumentException(
                    "La factura nueva no debe tener un identificador asignado."
            );
        }

        if (buscarPorPago(factura.getPagoId()) != null) {
            throw new IllegalArgumentException(
                    "Ya existe una factura asociada a ese pago."
            );
        }

        // Generamos el identificador de la factura.
        Factura facturaRegistrada =
                factura.conIdentificadorGenerado(siguienteId);

        facturas.put(facturaRegistrada.getId(), facturaRegistrada);
        siguienteId++;

        return facturaRegistrada;
    }

    // Busca una factura mediante el ID del pago.
    @Override
    public Factura buscarPorPago(int pagoId) {

        for (Factura factura : facturas.values()) {
            if (factura.getPagoId() == pagoId) {
                return factura;
            }
        }

        return null;
    }

    // Busca una factura mediante su identificador.
    @Override
    public Factura buscarPorId(int id) {
        return facturas.get(id);
    }

    // Consulta las facturas asociadas a un pedido.
    @Override
    public List<Factura> listarPorPedido(int pedidoId) {

        List<Factura> resultado = new ArrayList<>();

        for (Factura factura : facturas.values()) {
            if (factura.getPedidoId() == pedidoId) {
                resultado.add(factura);
            }
        }

        return Collections.unmodifiableList(resultado);
    }

    // Consulta todas las facturas registradas.
    @Override
    public List<Factura> listar() {
        return Collections.unmodifiableList(
                new ArrayList<>(facturas.values())
        );
    }
}