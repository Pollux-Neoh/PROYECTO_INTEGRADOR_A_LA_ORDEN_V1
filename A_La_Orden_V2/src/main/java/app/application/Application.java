package app.application;

import app.domain.Cliente;
import app.repository.ClienteRepository;
import app.repository.RestauranteRepository;
import app.repository.RestauranteRepositoryImpl;
import app.service.ClienteService;
import app.service.RestauranteService;
import app.view.ClienteView;
import app.view.RestauranteView;

import java.util.Scanner;

import app.repository.PedidoRepository;
import app.repository.PagoRepository;
import app.repository.PagoRepositoryMemoria;
import app.repository.FacturaRepository;
import app.repository.FacturaRepositoryMemoria;
import app.service.FacturaService;
import app.view.HistorialView;
import app.service.PedidoService;
import app.service.PagoService;

public class Application {

    public static void main(String[] args) {

        // ==============================
        // CREACIÓN DE LOS COMPONENTES DE LA APP
        // ==============================

        // Módulo de Restaurantes (US-03 / US-04)
        RestauranteRepository restauranteRepository = new RestauranteRepositoryImpl();
        RestauranteService restauranteService = new RestauranteService(restauranteRepository);
        RestauranteView restauranteView = new RestauranteView(restauranteService);

        // ==============================
        // MÓDULO DE PEDIDOS, PAGOS Y FACTURACIÓN
        // ==============================

        // Repositorio de pedidos.
        PedidoRepository pedidoRepository = new PedidoRepository();

        // Repositorio de pagos.
        PagoRepository pagoRepository = new PagoRepositoryMemoria();

        // Repositorio de facturas.
        FacturaRepository facturaRepository = new FacturaRepositoryMemoria();

        // Servicios encargados de pedidos y pagos.
        PedidoService pedidoService = new PedidoService(pedidoRepository);

        PagoService pagoService = new PagoService(
                pagoRepository,
                pedidoRepository
        );

        // Servicio encargado de generar y consultar facturas.
        FacturaService facturaService = new FacturaService(
                facturaRepository,
                pagoRepository
        );

        // Vista encargada de mostrar el historial.
        HistorialView historialView = new  HistorialView(
                facturaService,
                pedidoRepository
        );



        // Módulo de Clientes (Le inyectamos restauranteView para navegar desde la pantalla principal)
        ClienteRepository clienteRepository = new ClienteRepository();
        ClienteService clienteService = new ClienteService(clienteRepository);
        ClienteView clienteView = new ClienteView(clienteService, restauranteView, historialView);

        // Scanner para controlar las opciones del menú principal
        Scanner scanner = new Scanner(System.in);

        // Variable que controla si la aplicación continúa ejecutándose
        boolean continuar = true;

        // ==============================
        // MENÚ PRINCIPAL
        // ==============================

        while (continuar) {

            System.out.println("\n====================");
            System.out.println("---- A LA ORDEN ----");
            System.out.println("====================");

            System.out.println("\n1. Crear cuenta");
            System.out.println("2. Iniciar sesión");
            System.out.println("3. Salir");

            System.out.println("\nSeleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {

                case "1":
                    // Ejecutamos el proceso de creación de cuenta
                    clienteView.mostrarRegistro();
                    break;

                case "2":
                    // Ejecutamos el proceso de inicio de sesión y guardamos el cliente devuelto.
                    Cliente cliente = clienteView.mostrarLogin();

                    // Verificamos si el inicio de sesión fue exitoso
                    if (cliente != null) {
                        clienteView.mostrarPantallaPrincipal(cliente);
                    }
                    break;

                case "3":
                    // Cambiamos la variable para terminar el ciclo
                    continuar = false;

                    System.out.println("\nGracias por usar A LA ORDEN.");
                    System.out.println("Hasta luego.");
                    break;

                default:
                    // Si el usuario escribe una opción que no existe
                    System.out.println("\nOpción no válida.");
                    System.out.println("Por favor, seleccione 1, 2 o 3.");
            }
        }

        // Cerramos el Scanner al terminar la aplicación
        scanner.close();
    }
}
