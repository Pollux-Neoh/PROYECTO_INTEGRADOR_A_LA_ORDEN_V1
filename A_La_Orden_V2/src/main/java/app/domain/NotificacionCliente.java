
package app.domain;

public class NotificacionCliente {



    private final int clienteId;
    private final int pedidoId;
    private final String mensaje;


    //Constructor
    public NotificacionCliente(int clienteId, int pedidoId, String mensaje) {
        this.clienteId = clienteId;
        this.pedidoId = pedidoId;
        this.mensaje = mensaje;
    }


    //Getters
    public int getClienteId() {
        return clienteId;
    }

    public int getPedidoId() {
        return pedidoId;
    }

    public String getMensaje() {
        return mensaje;
    }
}

