package app.domain.enums;

public enum EstadoPedido {
    PENDIENTE_CONFIRMACION,
    CONFIRMADO,
    ENTREGADO,
    EN_PROCESO,
    RECHAZADO,
    TERMINADO;


        public boolean esActivo() {
            return this == CONFIRMADO
                    || this == EN_PROCESO
                    || this == TERMINADO;
        }

    TERMINADO,
    LISTO
}
