package app.repository;

import app.domain.Cliente;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

// Implementa el almacenamiento temporal de clientes en memoria.
public class ClienteRepositoryMemoria implements ClienteRepository {

    private final Map<Integer, Cliente> clientes;

    private int siguienteId;

    public ClienteRepositoryMemoria() {
        clientes = new LinkedHashMap<>();
        siguienteId = 1;
    }

    // Guarda un cliente y le asigna un ID si todavía no tiene uno.
    @Override
    public Cliente guardar(Cliente cliente) {

        if (cliente == null) {
            throw new IllegalArgumentException(
                    "El cliente no puede ser nulo."
            );
        }

        // Evitamos registrar dos clientes con el mismo correo.
        Cliente clienteExistente = buscarPorCorreo(cliente.getCorreo());

        if (clienteExistente != null
                && clienteExistente.getId() != cliente.getId()) {

            throw new IllegalArgumentException(
                    "Ya existe un cliente con ese correo."
            );
        }

        // Validamos que el ID no sea negativo.
        if (cliente.getId() < 0) {
            throw new IllegalArgumentException(
                    "El ID del cliente no puede ser negativo."
            );
        }

        // Si el cliente no tiene ID, generamos uno automáticamente.
        if (cliente.getId() == 0) {

            while (clientes.containsKey(siguienteId)) {

                if (siguienteId == Integer.MAX_VALUE) {
                    throw new IllegalStateException(
                            "No hay identificadores disponibles."
                    );
                }

                siguienteId++;
            }

            cliente.setId(siguienteId);

            if (siguienteId < Integer.MAX_VALUE) {
                siguienteId++;
            }

        } else {

            // Evitamos sobrescribir otro cliente con el mismo ID.
            Cliente clienteConMismoId = clientes.get(cliente.getId());

            if (clienteConMismoId != null
                    && clienteConMismoId != cliente) {

                throw new IllegalArgumentException(
                        "Ya existe un cliente con ese ID."
                );
            }

            // Mantenemos el contador por encima de los IDs manuales.
            if (cliente.getId() >= siguienteId
                    && cliente.getId() < Integer.MAX_VALUE) {

                siguienteId = cliente.getId() + 1;
            }
        }

        clientes.put(cliente.getId(), cliente);

        return cliente;
    }

    // Busca un cliente por su identificador.
    @Override
    public Cliente buscarPorId(int id) {
        return clientes.get(id);
    }

    // Busca un cliente por correo.
    @Override
    public Cliente buscarPorCorreo(String correo) {

        if (correo == null) {
            return null;
        }

        for (Cliente cliente : clientes.values()) {

            if (correo.equals(cliente.getCorreo())) {
                return cliente;
            }
        }

        return null;
    }

    // Consulta todos los clientes.
    @Override
    public List<Cliente> listar() {

        return Collections.unmodifiableList(
                new ArrayList<>(clientes.values())
        );
    }

    // Actualiza la contraseña del cliente.
    @Override
    public void actualizarContraseina(
            Cliente cliente,
            String nuevaContrasenia
    ) {

        if (cliente == null || nuevaContrasenia == null) {
            throw new IllegalArgumentException(
                    "El cliente y la contraseña son obligatorios."
            );
        }

        // El servicio debe enviar la contraseña cifrada con BCrypt.
        cliente.setContrasenia(nuevaContrasenia);
    }
}