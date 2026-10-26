package app.repository;

import app.domain.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

    private List<Cliente> clientes;

    public ClienteRepository() {

        clientes = new ArrayList<>();
    }

    public void guardar(Cliente cliente) {
        clientes.add(cliente);
    }

    public Cliente buscarPorCorreo(String correo) {

        for (Cliente cliente : clientes) {
            if (cliente.getCorreo().equals(correo)) {
                return cliente;
            }
        }
        return null;
    }

    public List<Cliente> listar() {
        return clientes;
    }

    public void actualizarContraseina(Cliente cliente, String nuevaContrasenia) {

        //Actualizamos la contraseña del cliente recibido
        cliente.setContrasenia(nuevaContrasenia);
    }
}
