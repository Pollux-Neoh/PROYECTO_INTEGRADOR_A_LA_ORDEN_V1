package app.service.InputPort;

import app.domain.enums.EscanerQr;

public interface PedidoInput {
    String obtenerQr(String idPedido);
    EscanerQr escanearQr(String codigoQr, int restauranteId);
}
