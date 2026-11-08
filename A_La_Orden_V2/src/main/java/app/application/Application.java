package app.application;

import app.domain.*;
import app.repository.ClienteRepository;
import app.repository.RestauranteRepository;
import app.repository.RestauranteRepositoryImpl;
import app.service.ClienteService;
import app.service.RestauranteService;
import app.view.ClienteView;
import app.view.RestauranteView;
import app.domain.enums.MetodoPago;
import org.mindrot.jbcrypt.BCrypt;

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
import app.service.NotificacionService;
import app.view.AdministradorRestauranteView;

import java.math.BigDecimal;

public class Application {

    public static void main(String[] args) {

        // ==============================
        // CREACIÓN DE LOS COMPONENTES DE LA APP
        // ==============================

        // Módulo de Restaurantes (US-03 / US-04)
        RestauranteRepository restauranteRepository = new RestauranteRepositoryImpl();
        RestauranteService restauranteService =
                new RestauranteService(restauranteRepository);

        // =======================
        // ADMINISTRADOR DE PRUEBA - US-19
        // =======================

        //Buscamos el restaurante que administrará el usuario.
        Restaurante restauranteDemo = restauranteRepository
                .buscarPorId(2)
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el restaurante de prueba."
                ));

        // Cargamos productos de prueva para El Sazón Paisa.
        Producto bandejaPaisa = new Producto(
                101,"Bandeja Paisa", "Plato típico Colombiano",
                new BigDecimal("25000"), true, "Platos principales"
        );

        Producto arepaQueso = new Producto(
                102, "Arepa paisa", "Plato típico Colombiano",
                new BigDecimal("8000"), true, "Acompañamientos"
        );

        Producto gaseosa = new Producto(
                103,"Gaseosa", "Bedida personal",
                new BigDecimal("5000"), true, "Bedidas"
        );


        restauranteDemo.agregarProducto(bandejaPaisa);
        restauranteDemo.agregarProducto(arepaQueso);
        restauranteDemo.agregarProducto(gaseosa);

// Generamos el hash de la contraseña del administrador.
        String contraseniaAdminHash = BCrypt.hashpw(
                "Admin12345",
                BCrypt.gensalt(12)
        );

// Creamos el administrador con su contraseña protegida.
        AdministradorRestaurante administradorDemo =
                new AdministradorRestaurante(
                        10,
                        "Administrador El Sazón",
                        "admins@sazon.com",
                        contraseniaAdminHash,
                        "3001234567",
                        true,
                        restauranteDemo
                );


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
        NotificacionService notificacionService = new NotificacionService();
        PedidoService pedidoService = new PedidoService(
                pedidoRepository,
                notificacionService
        );

        // Servicio encargado de gestionar los pagos.
        PagoService pagoService = new PagoService(
                pagoRepository,
                pedidoRepository
        );

        // Vista de restaurantes conectada con pedidos y pagos.
        RestauranteView restauranteView = new RestauranteView(
                restauranteService,
                pedidoService,
                pagoService
        );


        // ==============================
        // PEDIDOS DE PRUEBA - US-19
        // ==============================

        // Repositorio de clientes.
        ClienteRepository clienteRepository = new ClienteRepository();

// Generamos el hash de la contraseña del cliente de prueba.
        String contraseniaClienteHash = BCrypt.hashpw(
                "Cliente123",
                BCrypt.gensalt(12)
        );

// Cliente utilizado para las pruebas.
        Cliente clienteDemo = new Cliente(
                100,
                "Camilo Cliente",
                "cliente@prueba.com",
                contraseniaClienteHash,
                "3001112233",
                true,
                "Medellín"
        );

// Registramos el cliente de prueba en el repositorio.
        clienteRepository.guardar(clienteDemo);

        // Pedido 1001: se utilizará para probar la aceptación.
        Pedido pedidoAceptar = pedidoService.crearPedido(
                1001, clienteDemo, restauranteDemo
        );

        if (pedidoAceptar != null) {

            pedidoService.agregarProducto(1001, bandejaPaisa, 2);
            pedidoService.agregarProducto(1001, gaseosa, 1);

            if (pedidoService.confirmarPedido(1001)) {
                pagoService.registrarPago(1001, MetodoPago.EFECTIVO);
            }
        }

        // Pedido 1002: se utilizará para probar el rechazo.
        Pedido pedidoRechazar = pedidoService.crearPedido(
                1002, clienteDemo, restauranteDemo
        );

        if (pedidoRechazar != null) {

            pedidoService.agregarProducto(1002, arepaQueso, 1);
            pedidoService.agregarProducto(1002, gaseosa, 2);

            if (pedidoService.confirmarPedido(1002)) {
                pagoService.registrarPago(1002, MetodoPago.EFECTIVO);
            }
        }



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

        AdministradorRestauranteView administradorView =
                new  AdministradorRestauranteView(
                        administradorDemo,
                        pedidoService,
                        pagoService
                );



        // Módulo de Clientes.
        ClienteService clienteService = new ClienteService(clienteRepository);
        ClienteView clienteView = new ClienteView(
                clienteService,
                restauranteView,
                historialView
        );

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
            System.out.println("3. Administrador del restaurante");
            System.out.println("4. Salir");

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
                    administradorView.mostrarMenu();
                    break;

                case "4":
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
