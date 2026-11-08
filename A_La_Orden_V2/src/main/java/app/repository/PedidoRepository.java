package app.repository;

import app.domain.Pedido;
import app.service.OutputPort.PedidoOutport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Implementación en memoria del repositorio de pedidos.
public class PedidoRepository implements PedidoOutport {

    private final Map<Integer, Pedido> pedidos;

    // Mantiene la numeración que utilizábamos en las pruebas.
    private int siguienteId;

    public PedidoRepository() {
        pedidos = new LinkedHashMap<>();
        siguienteId = 1003;
    }

    @Override
    public Pedido guardar(Pedido pedido) {

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "El pedido no puede ser nulo."
            );
        }

        Pedido pedidoGuardado = pedido;

        // Si el pedido todavía no tiene ID, el repositorio lo genera.
        if (pedido.getId() == 0) {

            while (pedidos.containsKey(siguienteId)) {

                if (siguienteId == Integer.MAX_VALUE) {
                    throw new IllegalStateException(
                            "No hay identificadores disponibles."
                    );
                }

                siguienteId++;
            }

            pedidoGuardado =
                    pedido.conIdentificadorGenerado(siguienteId);

            if (siguienteId < Integer.MAX_VALUE) {
                siguienteId++;
            }

        } else if (pedido.getId() < 0) {

            throw new IllegalArgumentException(
                    "El ID del pedido no puede ser negativo."
            );
        }

        // Si el ID ya existe, actualiza el pedido.
        pedidos.put(pedidoGuardado.getId(), pedidoGuardado);

        return pedidoGuardado;
    }

    @Override
    public Pedido buscarPorId(int id) {
        return pedidos.get(id);
    }

    @Override
    public Optional<Pedido> buscarPorId(String id) {

        try {
            return Optional.ofNullable(
                    buscarPorId(Integer.parseInt(id))
            );

        } catch (NumberFormatException exception) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Pedido> buscarPorCodigoQr(String codigoQr) {

        if (codigoQr == null || codigoQr.trim().isEmpty()) {
            return Optional.empty();
        }

        for (Pedido pedido : pedidos.values()) {

            if (codigoQr.equals(pedido.getCodigoQr())) {
                return Optional.of(pedido);
            }
        }

        return Optional.empty();
    }

    public boolean eliminar(int id) {
        return pedidos.remove(id) != null;
    }

    @Override
    public List<Pedido> listar() {

        return Collections.unmodifiableList(
                new ArrayList<>(pedidos.values())
        );
    }
}
