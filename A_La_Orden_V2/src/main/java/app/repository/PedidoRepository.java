package app.repository;

import app.domain.Pedido;
import app.service.OutputPort.PedidoOutport;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class PedidoRepository implements PedidoOutport {

    private final Map<Integer, Pedido> pedidos;

    public PedidoRepository() {
        pedidos = new LinkedHashMap<>();
    }

    @Override
    public Pedido guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
        return pedido;
    }

    public Pedido buscarPorId(int id) {
        return pedidos.get(id);
    }

    @Override
    public Optional<Pedido> buscarPorId(String id) {
        try {
            return Optional.ofNullable(buscarPorId(Integer.parseInt(id)));
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

    public List<Pedido> listar() {
        return Collections.unmodifiableList(new ArrayList<>(pedidos.values()));
    }
}
