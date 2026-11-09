
package app.application;

import app.domain.*;
import app.domain.enums.MetodoPago;

import app.repository.ClienteRepository;
import app.repository.ClienteRepositoryMySQL;
import app.repository.ProductoRepository;
import app.repository.ProductoRepositoryMySQL;
import app.repository.RestauranteRepository;
import app.repository.RestauranteRepositoryMySQL;
import app.repository.PagoRepository;
import app.repository.PagoRepositoryMySQL;
import app.repository.FacturaRepository;
import app.repository.FacturaRepositoryMySQL;
import app.repository.NotificacionClienteRepository;
import app.repository.NotificacionClienteRepositoryMySQL;
import app.repository.NotificacionComercioRepository;
import app.repository.NotificacionComercioRepositoryMySQL;
import app.repository.PedidoRepositoryMySQL;

import app.service.OutputPort.PedidoOutport;
import app.service.ClienteService;
import app.service.ProductoService;
import app.service.RestauranteService;
import app.service.PedidoService;
import app.service.PagoService;
import app.service.FacturaService;
import app.service.NotificacionService;

import app.view.ClienteView;
import app.view.RestauranteView;
import app.view.HistorialView;
import app.view.AdministradorRestauranteView;

import org.mindrot.jbcrypt.BCrypt;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        // ==========================================
        // REPOSITORIOS MYSQL
        // ==========================================

        RestauranteRepository restauranteRepository =
                new RestauranteRepositoryMySQL();

        ProductoRepository productoRepository =
                new ProductoRepositoryMySQL();

        ClienteRepository clienteRepository =
                new ClienteRepositoryMySQL();

        PedidoOutport pedidoRepository =
                new PedidoRepositoryMySQL();

        PagoRepository pagoRepository =
                new PagoRepositoryMySQL(pedidoRepository);

        FacturaRepository facturaRepository =
                new FacturaRepositoryMySQL();

        NotificacionComercioRepository comercioNotificacionRepository =
                new NotificacionComercioRepositoryMySQL();

        NotificacionClienteRepository clienteNotificacionRepository =
                new NotificacionClienteRepositoryMySQL();


        // ==========================================
        // SERVICIOS
        // ==========================================

        RestauranteService restauranteService =
                new RestauranteService(restauranteRepository);

        ProductoService productoService =
                new ProductoService(
                        productoRepository,
                        restauranteRepository
                );

        ClienteService clienteService =
                new ClienteService(clienteRepository);


        // ==========================================
        // ADMINISTRADOR DE PRUEBA - US-19
        // ==========================================

        // Buscamos el restaurante que administrará el usuario.
        Restaurante restauranteDemo = restauranteRepository
                .buscarPorId(1)
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el restaurante de prueba."
                ));

        // Generamos el hash de la contraseña del administrador.
        String contraseniaAdminHash = BCrypt.hashpw(
                "Admin12345",
                BCrypt.gensalt(12)
        );

        // Creamos el administrador de prueba.
        AdministradorRestaurante administradorDemo =
                new AdministradorRestaurante(
                        10,
                        "Administrador Donde Lola",
                        "admins@lola.com",
                        contraseniaAdminHash,
                        "3001234567",
                        true,
                        restauranteDemo
                );


        // ==========================================
        // MÓDULO DE NOTIFICACIONES
        // ==========================================

        NotificacionService notificacionService =
                new NotificacionService(
                        comercioNotificacionRepository,
                        clienteNotificacionRepository
                );


        // ==========================================
        // MÓDULO DE PEDIDOS
        // ==========================================

        PedidoService pedidoService =
                new PedidoService(
                        pedidoRepository,
                        notificacionService
                );


        // ==========================================
        // MÓDULO DE PAGOS
        // ==========================================

        PagoService pagoService =
                new PagoService(
                        pagoRepository,
                        pedidoRepository
                );


        // ==========================================
        // MÓDULO DE FACTURACIÓN
        // ==========================================

        FacturaService facturaService =
                new FacturaService(
                        facturaRepository,
                        pagoRepository
                );


        // ==========================================
        // VISTAS
        // ==========================================

        // Vista de restaurantes conectada con pedidos y pagos.
        RestauranteView restauranteView =
                new RestauranteView(
                        restauranteService,
                        pedidoService,
                        pagoService,
                        facturaService
                );

        // Vista encargada de mostrar el historial.
        HistorialView historialView =
                new HistorialView(
                        facturaService,
                        pedidoRepository
                );

        // Vista del administrador del restaurante.
        AdministradorRestauranteView administradorView =
                new AdministradorRestauranteView(
                        administradorDemo,
                        pedidoService,
                        pagoService
                );

        // Vista de clientes.
        ClienteView clienteView =
                new ClienteView(
                        clienteService,
                        restauranteView,
                        historialView,
                        pedidoService
                );


        // ==========================================
        // MENÚ PRINCIPAL
        // ==========================================

        Scanner scanner = new Scanner(System.in);

        boolean continuar = true;

        while (continuar) {

            System.out.println("\n====================");
            System.out.println("---- A LA ORDEN ----");
            System.out.println("====================");

            System.out.println("\n1. Crear cuenta");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Administrador del restaurante");
            System.out.println("4. Salir");

            System.out.println("\nSeleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {

                case "1":
                    // Ejecutamos el proceso de creación de cuenta.
                    clienteView.mostrarRegistro();
                    break;

                case "2":
                    // Ejecutamos el proceso de inicio de sesión.
                    Cliente cliente = clienteView.mostrarLogin();

                    // Verificamos si el inicio de sesión fue exitoso.
                    if (cliente != null) {
                        clienteView.mostrarPantallaPrincipal(cliente);
                    }
                    break;

                case "3":
                    administradorView.mostrarMenu();
                    break;

                case "4":
                    // Finalizamos el ciclo del menú principal.
                    continuar = false;

                    System.out.println("\nGracias por usar A LA ORDEN.");
                    System.out.println("Hasta luego.");
                    break;

                default:
                    System.out.println("\nOpción no válida.");
                    System.out.println("Por favor, seleccione 1, 2, 3 o 4.");
            }
        }

        // Cerramos el Scanner al terminar la aplicación.
        scanner.close();
    }
}
