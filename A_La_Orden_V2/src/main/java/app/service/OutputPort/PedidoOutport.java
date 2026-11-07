package app.service.OutputPort;

import app.domain.Pedido;
import java.util.List;
import java.util.Optional;

public interface PedidoOutport {
    Pedido guardar(Pedido pedido);
    Pedido buscarPorId(int id);
    Optional<Pedido> buscarPorId(String id);
    Optional<Pedido> buscarPorCodigoQr(String codigoQr);
    List<Pedido> listar();
}