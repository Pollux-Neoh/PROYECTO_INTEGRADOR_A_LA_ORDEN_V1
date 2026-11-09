
package app.config;

import app.domain.Restaurante;
import app.domain.Producto;
import app.repository.RestauranteRepository;
import app.repository.RestauranteRepositoryMySQL;

import java.util.List;
import java.util.Optional;

public class PruebaRestauranteMySQL {

    public static void main(String[] args) {

        RestauranteRepository repositorio =
                new RestauranteRepositoryMySQL();

        System.out.println("===== RESTAURANTES EN MYSQL =====");

        List<Restaurante> restaurantes =
                repositorio.obtenerTodos();

        for (Restaurante restaurante : restaurantes) {

            System.out.println("\nRestaurante: " + restaurante.getNombre());
            System.out.println("ID: " + restaurante.getId());
            System.out.println("Categoría: " + restaurante.getCategoria());

            System.out.println("Menú:");

            for (Producto producto : restaurante.getMenu()) {

                System.out.println(
                        "- " + producto.getNombre()
                                + " | Precio: $" + producto.getPrecio()
                );
            }
        }

        System.out.println("\n===== BÚSQUEDA POR ID =====");

        Optional<Restaurante> resultado =
                repositorio.buscarPorId(2);

        if (resultado.isPresent()) {

            Restaurante restaurante = resultado.get();

            System.out.println(
                    "Restaurante encontrado: " + restaurante.getNombre()
            );

            System.out.println(
                    "Productos en su menú: " + restaurante.getMenu().size()
            );

        } else {
            System.out.println("No se encontró el restaurante.");
        }
    }
}
