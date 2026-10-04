package app.application;

import app.domain.Cliente;
import app.repository.ClienteRepository;
import app.service.ClienteService;
import app.view.ClienteView;

public class Application {

    public static void main(String[] args) {

        System.out.println("--- A LA ORDEN ---");
        System.out.println("Aplicación iniciada correctamente.");

        //Creamos el repository que almacenará los clientes
        ClienteRepository clienteRepository = new ClienteRepository();

        //Creamos el service y le entregamos el repository
        ClienteService clienteService = new ClienteService(clienteRepository);

        //Creamos la view y le entregamos el service
        ClienteView clienteView = new ClienteView(clienteService);

        //Mostramos el formulario de registro
        clienteView.mostrarRegistro();

       /* //Registo de un cliente de prueba
        Cliente cliente = clienteService.registrarCliente(
                1,
                "Juan",
                "juan@gmail.com",
                "12345678",
                "3196681601",
        );
        //Intento de registrar otro cliente con el mismo correo
        Cliente segundoCliente = clienteService.registrarCliente(
                2,
                "Pedro",
                "juan@gmail.com",
                "87654321",
                "3017666401",
        ); */
    }
}
