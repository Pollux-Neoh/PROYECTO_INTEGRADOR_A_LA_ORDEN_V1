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
import app.repository.ClienteRepositoryMemoria;
import app.repository.ProductoRepository;
import app.repository.ProductoRepositoryMemoria;
import app.service.ProductoService;
import app.repository.NotificacionComercioRepositoryMemoria;
import app.repository.NotificacionClienteRepositoryMemoria;



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

        // Módulo de productos.
        ProductoRepository productoRepository =
                new ProductoRepositoryMemoria();

        ProductoService productoService =
                new ProductoService(productoRepository);

        // Repositorio de clientes.
        ClienteRepository clienteRepository = new ClienteRepositoryMemoria();

        // =======================
        // ADMINISTRADOR DE PRUEBA - US-19
        // =======================

        //Buscamos el restaurante que administrará el usuario.
        Restaurante restauranteDemo = restauranteRepository
                .buscarPorId(2)
                .orElseThrow(() -> new IllegalStateException(
                        "No se encontró el restaurante de prueba."
                ));


// El repositorio genera automáticamente sus identificadores.

        Producto bandejaPaisa = productoService.registrarProducto(
                "Bandeja Paisa",
                "Plato típico Colombiano",
                new BigDecimal("25000"),
                true,
                "Platos principales"
        );

        Producto arepaQueso = productoService.registrarProducto(
                "Arepa paisa",
                "Plato típico Colombiano",
                new BigDecimal("8000"),
                true,
                "Acompañamientos"
        );

        Producto gaseosa = productoService.registrarProducto(
                "Gaseosa",
                "Bebida personal",
                new BigDecimal("5000"),
                true,
                "Bebidas"
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

// Servicio de notificaciones.
        NotificacionService notificacionService = new NotificacionService(
                new NotificacionComercioRepositoryMemoria(),
                new NotificacionClienteRepositoryMemoria()
        );

// Servicio de pedidos.
        PedidoService pedidoService = new PedidoService(
                pedidoRepository,
                notificacionService
        );

// Servicio de pagos.
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

        // Pedido de prueba para verificar la aceptación.
        // El repositorio genera automáticamente su identificador.
        Pedido pedidoAceptar = pedidoService.crearPedido(
                clienteDemo, restauranteDemo
        );

        if (pedidoAceptar != null) {

            int idPedidoAceptar = pedidoAceptar.getId();

            pedidoService.agregarProducto(
                    idPedidoAceptar, bandejaPaisa, 2
            );

            pedidoService.agregarProducto(
                    idPedidoAceptar, gaseosa, 1
            );

            if (pedidoService.confirmarPedido(idPedidoAceptar)) {
                pagoService.registrarPago(
                        idPedidoAceptar, MetodoPago.EFECTIVO
                );
            }
        }

        // Pedido de prueba para verificar el rechazo.
        // El repositorio genera automáticamente su identificador.
        Pedido pedidoRechazar = pedidoService.crearPedido(
                clienteDemo, restauranteDemo
        );

        if (pedidoRechazar != null) {

            int idPedidoRechazar = pedidoRechazar.getId();

            pedidoService.agregarProducto(
                    idPedidoRechazar, arepaQueso, 1
            );

            pedidoService.agregarProducto(
                    idPedidoRechazar, gaseosa, 2
            );

            if (pedidoService.confirmarPedido(idPedidoRechazar)) {
                pagoService.registrarPago(
                        idPedidoRechazar, MetodoPago.EFECTIVO
                );
            }
        }

        //NOTIFICACIONES TEMPORALES
        System.out.println("\n=== PRUEBA DE NOTIFICACIONES ===");

        System.out.println("Notificaciones comercio: "
                + notificacionService.listarNotificaciones().size());

        System.out.println("Notificaciones clientes: "
                + notificacionService.listarNotificacionesCliente().size());


        // Servicio encargado de generar y consultar facturas.
        FacturaService facturaService = new FacturaService(
                facturaRepository,
                pagoRepository
        );

        // Vista encargada de mostrar el historial.
        HistorialView historialView = new HistorialView(
                facturaService,
                pedidoRepository
        );

        AdministradorRestauranteView administradorView =
                new AdministradorRestauranteView(
                        administradorDemo,
                        pedidoService,
                        pagoService
                );


        // Módulo de Clientes.
        ClienteService clienteService = new ClienteService(clienteRepository);
        ClienteView clienteView = new ClienteView(
                clienteService,
                restauranteView,
                historialView,
                pedidoService
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
