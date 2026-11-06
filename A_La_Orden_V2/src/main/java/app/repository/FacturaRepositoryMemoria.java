package app.repository;

import app.domain.Factura;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;

// Implementa el almacenamiento temporal de facturas en memoria.
public class FacturaRepositoryMemoria implements FacturaRepository {

    private final Map<Integer, Factura> facturas;

    public FacturaRepositoryMemoria() {
        facturas = new LinkedHashMap<>();
    }

    @Override
    public void guardar(Factura factura) {

        if (factura == null) {
            throw new IllegalArgumentException("La factura no puede ser nula."
            );
        }

        if (facturas.containsKey(factura.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe una factura con ese identificador."
            );
        }

        // Evitamos registrar más de una factura para el mismo pago.
        if (buscarPorPago(factura.getPagoId()) != null) {
            throw new IllegalArgumentException(
                    "El pago ya tiene una factura asociada."
            );
        }

        facturas.put(factura.getId(), factura);
    }

    @Override
    public Factura buscarPorId(int id) {
        return facturas.get(id);
    }

    @Override
    public Factura buscarPorPago(int pagoId) {

        for (Factura factura : facturas.values()) {

            if (factura.getPagoId() == pagoId) {
                return factura;
            }
        }
        return null;
    }

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

    @Override
    public List<Factura> listar() {
        return Collections.unmodifiableList(
                new ArrayList<>(facturas.values())
        );
    }
}
