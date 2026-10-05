package app.service;

import app.domain.Cliente;
import app.repository.ClienteRepository;
import app.validators.ClienteValidator;

public class ClienteService {

    private ClienteRepository clienteRepository;

    public ClienteService(ClienteRepository clienteRepository) {
        this.clienteRepository = clienteRepository;
    }

    public Cliente registrarCliente(
            int id,
            String nombre,
            String correo,
            String contrasenia,
            String telefono,
            boolean estado,
            String direccion
    ) {

        // Validamos el formato del correo
        if (!ClienteValidator.validarCorreo(correo)) {
            System.out.println("El formato del correo no es válido");
            return null;
        }

        // Validamos la seguridad básica de la contraseña
        if (!ClienteValidator.validarContrasenia(contrasenia)) {
            System.out.println("La contraseña debe tener mínimo 8 caracteres.");
            return null;
        }

        // Verificamos si el correo ya está registrado
        Cliente clienteExistente = clienteRepository.buscarPorCorreo(correo);

        if (clienteExistente != null) {
            System.out.println("El correo ya está registrado");
            return null;
        }

        // Crear el cliente
        Cliente cliente = new Cliente(
                id,
                nombre,
                correo,
                contrasenia,
                telefono,
                estado,
                direccion
        );

        // Guardar el cliente
        clienteRepository.guardar(cliente);

        return cliente;

    }

    public Cliente iniciarSesion(String correo, String contrasenia) {

        // Buscamos el cliente por su correo
        Cliente cliente = clienteRepository.buscarPorCorreo(correo);

        // Si no existe un cliente con ese correo
        if (cliente == null) {
            System.out.println("El correo o la contraseña son incorrectos.");
            return null;
        }

        // Comparamos la contraseña ingresada con la contraseña registrada
        if (!cliente.getContrasenia().equals(contrasenia)) {
            System.out.println("El correo o la contraseña son incorrectos.");
            return null;
        }

        // Si las credenciales coinciden, devolvemos el cliente
        return cliente;
    }

    public boolean recuperarContrasenia(String correo, String nuevaContrasenia) {

        // Buscamos el cliente utilizando el correo ingresado
        Cliente cliente = clienteRepository.buscarPorCorreo(correo);

        // Si no existe un cliente con ese correo, no podemos actualizar la contraseña.
        if (cliente == null) {
            System.out.println("No existe una cuenta registrada con ese correo.");
            return false;
        }

        // Validamos que la nueva contraseña cumpla con la regla establecida
        if (!ClienteValidator.validarContrasenia(nuevaContrasenia)) {
            System.out.println("La contraseña debe tener mínimo 8 caracteres.");

            return false;
        }
        // Sitodo está correcto, enviamos el cliente y la nueva contraseña al Repository.
        clienteRepository.actualizarContraseina(
                cliente,
                nuevaContrasenia
        );
        System.out.println("Contraseña actualizada correctamente");

        return true;
    }
}
