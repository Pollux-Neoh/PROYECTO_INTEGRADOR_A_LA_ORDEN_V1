package app.domain;

public class NotificacionComercio {

    private final int restauranteId;
    private final int pedidoId;
    private final String mensaje;

    public NotificacionComercio(int restauranteId, int pedidoId, String mensaje) {
        this.restauranteId = restauranteId;
        this.pedidoId = pedidoId;
        this.mensaje = mensaje;
    }

    public int getRestauranteId() {
        return restauranteId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public String getMensaje() {
        return mensaje;
    }
}
