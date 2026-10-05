package app.application;

import app.domain.Cliente;
import app.repository.ClienteRepository;
import app.service.ClienteService;
import app.view.ClienteView;

import java.util.Scanner;

public class Application {

    public static void main(String[] args) {

        // ==============================
        // CREACIÓN DE LOS COMPONENTES DE LA APP
        // ==============================

        // Repository: se encarga de almacenar y buscar clientes
        ClienteRepository clienteRepository = new ClienteRepository();

        // Service: contiene la lógica de registro e inicio de sesión
        ClienteService clienteService = new ClienteService(clienteRepository);

        // View: se encarga de interactuar con el usuario
        ClienteView clienteView = new ClienteView(clienteService);

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
                    // Ejecutamos el proceso de inicio de sesión y guardamos el cliente que devuelve la View.
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
