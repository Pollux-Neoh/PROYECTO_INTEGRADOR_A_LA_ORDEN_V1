package app.repository;

import app.domain.Cliente;

import java.util.List;

// Define las operaciones de almacenamiento de los clientes.
public interface ClienteRepository {

    // Guarda un cliente y devuelve el objeto guardado.
    Cliente guardar(Cliente cliente);

    // Busca un cliente por su identificador.
    Cliente buscarPorId(int id);

    // Busca un cliente por correo.
    Cliente buscarPorCorreo(String correo);

    // Consulta todos los clientes.
    List<Cliente> listar();

    // Actualiza la contraseña de un cliente.
    void actualizarContraseina(Cliente cliente, String nuevaContrasenia);
}
