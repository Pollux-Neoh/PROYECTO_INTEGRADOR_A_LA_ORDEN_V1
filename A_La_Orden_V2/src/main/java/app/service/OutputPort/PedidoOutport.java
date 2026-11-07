package app.service.OutputPort;

import app.domain.Pedido;

import java.util.Optional;

public interface PedidoOutport {
    Pedido guardar(Pedido pedido);
    Optional<Pedido> buscarPorId(String id);
}
