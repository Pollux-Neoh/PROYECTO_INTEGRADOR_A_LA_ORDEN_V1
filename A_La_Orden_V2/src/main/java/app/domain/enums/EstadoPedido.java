package app.domain.enums;

public enum EstadoPedido {

    PENDIENTE_CONFIRMACION,
    CONFIRMADO,
    EN_PROCESO,
    TERMINADO,
    LISTO,
    ENTREGADO,
    RECHAZADO;

    public boolean esActivo() {
        return this == CONFIRMADO
                || this == EN_PROCESO
                || this == TERMINADO
                || this == LISTO;
    }
}
