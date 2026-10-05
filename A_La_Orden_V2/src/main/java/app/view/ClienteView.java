package app.view;


import app.domain.Cliente;
import app.service.ClienteService;

import java.util.Scanner;

public class ClienteView {

    private Scanner scanner;
    private ClienteService clienteService;

    public ClienteView(ClienteService clienteService) {
        scanner = new Scanner(System.in);
        this.clienteService = clienteService;
    }

    public void mostrarRegistro(){

        System.out.println("\n--- CREAR CUENTA ---\n");

        System.out.println("Nombre: ");
        String nombre = scanner.nextLine();

        System.out.println("Correo: ");
        String correo = scanner.nextLine();

        System.out.println("Telefono: ");
        String telefono = scanner.nextLine();

        System.out.println("Contraseña: ");
        String contrasenia = scanner.nextLine();

        // Datos internos que el sistema establece automáticamente
        int id = 1;
        boolean estado = true;
        String direccion = "";

        // Enviamos los datos al service para registar el cliente
        Cliente clienteRegistrado = clienteService.registrarCliente(
                id,
                nombre,
                correo,
                contrasenia,
                telefono,
                estado,
                direccion
        );

        // Verificamos si el registro fue exitoso
        if (clienteRegistrado != null) {

            System.out.println("\n¡Cuenta creada exitosamente!");
            System.out.println("Bienvenido, " +  clienteRegistrado.getNombre());
        } else {

            System.out.println("\nNo fue posible crear la cuenta.");
        }
    }

    public Cliente mostrarLogin(){

        System.out.println("\n--- INICIAR SESIÓN ---\n");

        // Pedimos las credenciales al usuario
        System.out.println("Correo: ");
        String correo = scanner.nextLine();

        System.out.println("Contraseña: ");
        String contrasenia = scanner.nextLine();

        //Enviamos las credenciales al Service
        Cliente cliente = clienteService.iniciarSesion(
                correo,
                contrasenia
        );

        // Verificamos el resultado del login
        if (cliente != null) {
            System.out.println("\n¡Inicio de sesión exitoso!");
            System.out.println("Bienvenido, " +  cliente.getNombre());

            // Devolvemos el cliente para que Application pueda continuar hacia la pantalla principal
            return cliente;
        } else {

            System.out.println("\nNo fue posible iniciar sesión.");

            // Indicamos que el inicio de sesión falló
            return null;
        }
    }

    public void mostrarPantallaPrincipal(Cliente cliente){

        // Variable que controla si el cliente permanece dentro de la pantalla principal
        boolean continuar = true;

        while (continuar) {

        System.out.println("\n================");
        System.out.println("---- PANTALLA PRINCIPAL ----");
        System.out.println("================");

        // Mostramos el nombre del cliente que inició sesión
        System.out.println("\nBienvenido, " +  cliente.getNombre());

        System.out.println("\n1. Recuperar contraseña");
        System.out.println("2. Cerrar sesión");

        System.out.println("\nSeleccione una opción: ");

        String opcion = scanner.nextLine();

        switch (opcion) {

            case "1":
                System.out.println("\n === RECUPERAR CONTRASEÑA ===");

                // Pedimos el correo de la cuenta que quiere recuperar la contraseña.
                System.out.println("Ingrese su correo: ");
                String correo = scanner.nextLine();

                // Pedimos la nueva contraseña.
                System.out.println("Ingrese la nueva contraseña: ");
                String nuevaContraseina = scanner.nextLine();

                // Pedimos nuevamente la contraseña para confirmar que el usuario la escribió correctamente.
                System.out.println("Confirme la nueva contraseña: ");
                String confirmarContraseina = scanner.nextLine();

                //Comparamos la nueva contraseña con su confirmación.
                if (!nuevaContraseina.equals(confirmarContraseina)) {

                    //Si son diferentes, informamos el error al usuario.
                    System.out.println("\nLas contraseñas no coinciden.");
                    System.out.println("No fue posible recuperar la contraseña.");

                    // Salimos de este case sin actualizar la contraseña.
                    break;
                }

                //Enviamos los datos al service para que realice las validaciones y actualice la contraseña.
                boolean recuperacionExitosa = clienteService.recuperarContrasenia(
                        correo,
                        nuevaContraseina
                );

                // Informamos el resultado al usuario.
                if (recuperacionExitosa) {

                    System.out.println("\n¡Contraseña recuperada exitosamente!");
                } else {

                    System.out.println("\nNo fue posible recuperar la contraseña.");
                }
                break;
            case "2":
                // Cambiamos la variable para salir de la pantalla principal,
                continuar = false;

                System.out.println("\nSesión cerrada correctamente.");
                break;

            default:
                //Retroalimentación para una opción inexistente
                System.out.println("\nOpcion no valida.");
                System.out.println("Por favor, seleccione 1 o 2");

        }
        }
    }
}
