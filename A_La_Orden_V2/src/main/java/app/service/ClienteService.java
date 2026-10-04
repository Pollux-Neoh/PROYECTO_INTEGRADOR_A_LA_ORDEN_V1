package app.service;

import app.domain.Cliente;
import app.repository.ClienteRepository;
import app.validators.ClienteValidator;

public class ClienteService {

    private ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente registrarCliente(int id, String nombre, String correo,String contrasenia,String telefono, boolean estado, String direcccion) {

        //Validamos el formato del correo
        if (!ClienteValidator.validarCorreo(correo)) {
            System.out.println("El formato del correo no es válido");
            return null;
        }

        //Validamos la seguridad básica de la contraseña
        if (!ClienteValidator.validarContrasenia(contrasenia)) {
            System.out.println("La contraseña debe tener mínimo 8 caracteres.");
            return null;
        }

        //Verificar si el correo ya está registrado
        Cliente clienteExistente = clienteRepository.buscarPorCorreo(correo);

        if (clienteExistente != null) {
            System.out.println("El correo ya esta registrado");
            return null;
        }

        //Crear el cliente
        Cliente cliente = new Cliente(
                id,
                nombre,
                correo,
                contrasenia,
                telefono,
                estado,
                direcccion
        );

        //Guardar el cliente
        clienteRepository.guardar(cliente);

        return cliente;

    }
}
