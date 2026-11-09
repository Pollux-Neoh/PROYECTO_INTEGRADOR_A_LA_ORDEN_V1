
package app.config;

import app.domain.NotificacionCliente;
import app.domain.NotificacionComercio;
import app.repository.NotificacionClienteRepositoryMySQL;
import app.repository.NotificacionComercioRepositoryMySQL;

public class PruebaNotificacionesMySQL {

    public static void main(String[] args) {

        NotificacionClienteRepositoryMySQL clienteRepository =
                new NotificacionClienteRepositoryMySQL();

        NotificacionComercioRepositoryMySQL comercioRepository =
                new NotificacionComercioRepositoryMySQL();

        System.out.println("===== GUARDAR NOTIFICACION CLIENTE =====");

        NotificacionCliente notificacionCliente =
                new NotificacionCliente(
                        1,
                        1,
                        "Tu pedido ha sido confirmado correctamente."
                );

        clienteRepository.guardar(notificacionCliente);

        System.out.println("Notificación del cliente guardada.");

        System.out.println("\n===== GUARDAR NOTIFICACION COMERCIO =====");

        NotificacionComercio notificacionComercio =
                new NotificacionComercio(
                        2,
                        1,
                        "Tienes un nuevo pedido confirmado."
                );

        comercioRepository.guardar(notificacionComercio);

        System.out.println("Notificación del comercio guardada.");

        System.out.println("\n===== LISTAR NOTIFICACIONES CLIENTE =====");

        clienteRepository.listar().forEach(notificacion -> {

            System.out.println(
                    "Cliente: " + notificacion.getClienteId()
                            + " | Pedido: " + notificacion.getPedidoId()
                            + " | Mensaje: " + notificacion.getMensaje()
            );
        });

        System.out.println("\n===== LISTAR NOTIFICACIONES COMERCIO =====");

        comercioRepository.listar().forEach(notificacion -> {

            System.out.println(
                    "Restaurante: " + notificacion.getRestauranteId()
                            + " | Pedido: " + notificacion.getPedidoId()
                            + " | Mensaje: " + notificacion.getMensaje()
            );
        });

        System.out.println("\n===== PRUEBAS FINALIZADAS =====");
    }
}
