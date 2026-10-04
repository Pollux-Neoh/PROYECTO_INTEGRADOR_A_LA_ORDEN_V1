package app.view;

import app.repository.ClienteRepository;
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

        //Datos internos que el sistema establece automáticamente
        int id = 1;
        boolean estado = true;
        String direccion = "";

        //Enviamos los datos al service para registar el cliente
        Cliente clienteRegistrado = clienteService.registrarCliente(
                id,
                nombre,
                correo,
                contrasenia,
                telefono,
                estado,
                direccion
        );

        //Verficamos si el registro fue exitoso
        if (clienteRegistrado != null) {

            System.out.println("\n¡Cuenta creada exitosamente!");
            System.out.println("Bienvenido, " +  clienteRegistrado.getNombre());
        } else {

            System.out.println("\nNofue posible crear la cuenta.");
        }
    }
}
