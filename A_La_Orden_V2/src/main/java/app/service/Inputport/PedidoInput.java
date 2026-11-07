package app.service.Inputport;

public interface PedidoInput {
    String obtenerQr(String idPedido);
    boolean escanearQr(String codigoQr);
}
