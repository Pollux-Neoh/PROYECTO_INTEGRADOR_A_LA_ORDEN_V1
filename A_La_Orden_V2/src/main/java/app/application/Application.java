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

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        // ==============================
        // CREACIÓN DE LOS COMPONENTES DE LA APP
        // ==============================



        // Módulo de Clientes
        ClienteRepository clienteRepository = new ClienteRepository();
        ClienteService clienteService = new ClienteService(clienteRepository);
        ClienteView clienteView = new ClienteView(clienteService);

        // Módulo de Restaurantes (US-03)
        RestauranteRepository restauranteRepository = new RestauranteRepositoryImpl();
        RestauranteService restauranteService = new RestauranteService(restauranteRepository);
        RestauranteView restauranteView = new RestauranteView(restauranteService);

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
            System.out.println("3. Explorar restaurantes");
            System.out.println("4. Salir");

            System.out.println("\nSeleccione una opción: ");

            String opcion = scanner.nextLine();

            switch (opcion) {

                case "1":
                    // Ejecutamos el proceso de creación de cuenta
                    clienteView.mostrarRegistro();
                    break;

                case "2":
                    // Ejecutamos el proceso de inicio de sesión y guardamos el cliente que devuelve la View.
                    Cliente cliente = clienteView.mostrarLogin();

                    // Verificamos si el inicio de sesión fue exitoso
                    if (cliente != null) {

                        clienteView.mostrarPantallaPrincipal(cliente);
                    }
                    break;

                case "3":
                    // Ejecutamos el proceso de inicio de sesión
                    //clienteView.mostrarLogin();
                    break;

                case "4":
                    // US-03: Menú de descubrimiento de restaurantes
                    restauranteView.mostrarMenuRestaurantes();
                    break;

                case "5":
                    // Cambiamos la variable para terminar el ciclo
                    continuar = false;

                    System.out.println("\nGracias por usar A LA ORDEN.");
                    System.out.println("Hasta luego.");
                    break;

                default:
                    // Si el usuario escribe una opción que no existe
                    System.out.println("\nOpción no válida.");
                    System.out.println("Por favor, seleccione 1, 2 o 3.");
                    System.out.println("Por favor, seleccione 1, 2, 3 o 4.");
            }
        }

        // Cerramos el Scanner al terminar la aplicación
        scanner.close();
    }
}
