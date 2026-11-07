package app.repository;

import app.domain.Pedido;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class PedidoRepository {

    private final Map<Integer, Pedido> pedidos;

    public PedidoRepository() {
        pedidos = new LinkedHashMap<>();
    }

    public void guardar(Pedido pedido) {
        pedidos.put(pedido.getId(), pedido);
    }

    public Pedido buscarPorId(int id) {
        return pedidos.get(id);
    }

    public boolean eliminar(int id) {
        return pedidos.remove(id) != null;
    }

    public List<Pedido> listar() {
        return Collections.unmodifiableList(new ArrayList<>(pedidos.values()));
    }
}
