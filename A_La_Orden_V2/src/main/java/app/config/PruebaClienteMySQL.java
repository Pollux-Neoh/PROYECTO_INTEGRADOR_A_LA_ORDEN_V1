
package app.config;

import app.domain.Cliente;
import app.repository.ClienteRepository;
import app.repository.ClienteRepositoryMySQL;

import java.util.List;

public class PruebaClienteMySQL {

    public static void main(String[] args) {

        ClienteRepository repositorio = new ClienteRepositoryMySQL();

        String correoPrueba = "cliente.prueba.mysql@correo.com";

        System.out.println("===== REGISTRO DE CLIENTE =====");

        // Evitamos duplicar el correo si ejecutamos la prueba nuevamente.
        Cliente cliente = repositorio.buscarPorCorreo(correoPrueba);

        if (cliente == null) {

            cliente = new Cliente(
                    0,
                    "Cliente de prueba",
                    correoPrueba,
                    "HASH_DE_PRUEBA",
                    "3001234567",
                    true,
                    "Calle 10 # 20-30"
            );

            cliente = repositorio.guardar(cliente);

            System.out.println("Cliente registrado correctamente.");

        } else {
            System.out.println("El cliente de prueba ya estaba registrado.");
        }

        System.out.println("ID: " + cliente.getId());
        System.out.println("Nombre: " + cliente.getNombre());
        System.out.println("Correo: " + cliente.getCorreo());


        System.out.println("\n===== BÚSQUEDA POR ID =====");

        Cliente encontradoPorId =
                repositorio.buscarPorId(cliente.getId());

        if (encontradoPorId != null) {
            System.out.println(
                    "Cliente encontrado: " + encontradoPorId.getNombre()
            );
        } else {
            System.out.println("No se encontró el cliente.");
        }


        System.out.println("\n===== BÚSQUEDA POR CORREO =====");

        Cliente encontradoPorCorreo =
                repositorio.buscarPorCorreo(correoPrueba);

        if (encontradoPorCorreo != null) {
            System.out.println(
                    "Correo encontrado: " + encontradoPorCorreo.getCorreo()
            );
        } else {
            System.out.println("No se encontró el correo.");
        }


        System.out.println("\n===== LISTADO DE CLIENTES =====");

        List<Cliente> clientes = repositorio.listar();

        for (Cliente registro : clientes) {
            System.out.println(
                    registro.getId() + " | "
                            + registro.getNombre() + " | "
                            + registro.getCorreo()
            );
        }


        System.out.println("\n===== ACTUALIZACIÓN DE CONTRASEÑA =====");

        String nuevaContrasenia =
                "HASH_ACTUALIZADO_DE_PRUEBA";

        repositorio.actualizarContraseina(
                cliente,
                nuevaContrasenia
        );

        Cliente clienteActualizado =
                repositorio.buscarPorId(cliente.getId());

        if (clienteActualizado != null
                && nuevaContrasenia.equals(
                clienteActualizado.getContrasenia()
        )) {

            System.out.println(
                    "Contraseña actualizada y verificada en MySQL."
            );

        } else {
            System.out.println(
                    "No se pudo verificar la actualización."
            );
        }

        System.out.println("\n===== PRUEBAS FINALIZADAS =====");
    }
}
