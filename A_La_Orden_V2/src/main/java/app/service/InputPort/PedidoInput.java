package app.service.InputPort;

public interface PedidoInput {
    String obtenerQr(String idPedido);
    boolean escanearQr(String codigoQr);
}
