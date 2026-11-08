package app.repository;

import app.domain.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    private List<Cliente> clientes;

    public ClienteRepository() {
        clientes = new ArrayList<>();
    }

    // Guardar un cliente
    public void guardar(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException("El cliente no puede ser nulo.");
        }

        // Evitamos registrar dos clientes con el mismo correo.
        if (buscarPorCorreo(cliente.getCorreo()) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con ese correo.");
        }

        clientes.add(cliente);
    }

    // Buscar cliente por correo
    public Cliente buscarPorCorreo(String correo) {

        if (correo == null) {
            return null;
        }

        for (Cliente cliente : clientes) {
            if (correo.equals(cliente.getCorreo())) {
                return cliente;
            }
        }

        return null;
    }

    // Listar clientes
    public List<Cliente> listar() {
        return new ArrayList<>(clientes);
    }

    // Actualizar contraseña del cliente
    public void actualizarContraseina(Cliente cliente, String nuevaContrasenia) {

        if (cliente == null || nuevaContrasenia == null) {
            throw new IllegalArgumentException("El cliente y la contraseña son obligatorios.");
        }

        // El servicio ya debe enviar la contraseña cifrada con BCrypt.
        cliente.setContrasenia(nuevaContrasenia);
    }
}
