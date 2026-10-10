package app.view;

import app.domain.Producto;
import app.domain.Restaurante;
import app.service.RestauranteService;

import java.util.Map;
import java.util.List;
import java.util.Scanner;
import java.util.Optional;
import java.util.stream.Collectors;


public class RestauranteView {

    private Scanner scanner;
    private RestauranteService restauranteService;

    public RestauranteView(RestauranteService restauranteService) {
        this.scanner = new Scanner(System.in);
        this.restauranteService = restauranteService;
    }

    //Menú principal de la funcionalidad US-03 y US-04
    public void mostrarMenuRestaurantes() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n--- DESCUBRIMIENTO DE RESTAURANTES ---");
            System.out.println("1. Ver restaurantes cercanos");
            System.out.println("2. Buscar restaurantes (por nombre o categoría)");
            System.out.println("3. Ver carta/menú de un restaurante");
            System.out.println("0. Volver al menú principal");
            System.out.print("Seleccione una opción: ");

            if (scanner.hasNextInt()) {
                opcion = scanner.nextInt();
                scanner.nextLine(); // Consumimos el salto de línea pendiente
            } else {
                scanner.nextLine(); // Limpiamos la entrada si no es un número
                System.out.println("Por favor, ingrese un número válido.");
                continue;
            }

            switch (opcion) {
                case 1:
                    // Criterio 1: Carga restaurantes ordenados por cercanía
                    List<Restaurante> cercanos = restauranteService.obtenerRestaurantesCercanos();
                    imprimirListado(cercanos);
                    break;

                case 2:
                    // Criterio 3: Permite buscar/filtrar
                    System.out.print("\nIngrese el nombre o categoría a buscar: ");
                    String terminoBusqueda = scanner.nextLine();

                    List<Restaurante> filtrados = restauranteService.buscarRestaurantes(terminoBusqueda);
                    imprimirListado(filtrados);
                    break;

                case 3:
                    // ⬇️ LÓGICA US-04
                    System.out.print("\nIngrese el ID del restaurante para ver su menú: ");
                    if (scanner.hasNextInt()) {
                        int idRestaurante = scanner.nextInt();
                        scanner.nextLine();
                        mostrarMenuRestaurante(idRestaurante);
                    } else {
                        scanner.nextLine();
                        System.out.println("ID no válido.");
                    }
                    break;

                case 0:
                    System.out.println("Regresando al menú principal...");
                    break;

                default:
                    System.out.println("Opción no válida.");
            }
        }
    }



    //Metodo auxiliar para dar formato visual a la lista de restaurantes
    //Criterio 2: Muestra nombre, imagen y calificación promedio.

    private void imprimirListado(List<Restaurante> lista) {
        if (lista.isEmpty()) {
            System.out.println("\nNo se encontraron restaurantes que coincidan con la búsqueda.");
            return;
        }

        System.out.println("\n=============================================");
        System.out.println("          RESTAURANTES DISPONIBLES           ");
        System.out.println("=============================================");

        for (Restaurante r : lista) {
            System.out.println("Nombre:       " + r.getNombre());
            System.out.println("Categoría:    " + r.getCategoria());
            System.out.println("Calificación: ⭐ " + r.getCalificacionPromedio());
            System.out.println("Distancia:    📍 a " + r.getDistanciaKm() + " km de ti");
            System.out.println("Imagen:       🖼️ " + r.getImagenUrl());
            System.out.println("---------------------------------------------");
        }
    }

    //Metodo Mostrar Restaurantes

    public void mostrarMenuRestaurante(int idRestaurante) {
        Optional<Restaurante> restauranteOptional = restauranteService.obtenerRestaurantePorId(idRestaurante);

        if (restauranteOptional.isEmpty()) {
            System.out.println("No existe el restaurante con el id: " + idRestaurante);
            return;
        }

        Restaurante restaurante = restauranteOptional.get();
        List<Producto> productos = restaurante.getMenu();

        System.out.println("\n=============================================");
        System.out.println("          MENÚ: " + restaurante.getNombre().toUpperCase());
        System.out.println("=============================================");

        if (productos == null|| productos.isEmpty()) {
            System.out.println("El restaurante aun no tiene productos");
            return;
        }

        //Agrupacion por categoria mediante Streams
        Map<String, List<Producto>> productosPorCategoria = productos.stream()
                .collect(Collectors.groupingBy(Producto::getCategoria));

        for (Map.Entry<String, List<Producto>> entry : productosPorCategoria.entrySet()) {
            System.out.println("\n--- CATEGORÍA: " + entry.getKey().toUpperCase() + " ---");

            for (Producto prod : entry.getValue()) {
                String estado = prod.isDisponible() ? "DISPONIBLE" : "[AGOTADO]";
                System.out.printf("- %s | $%.2f | Estado: %s%n", prod.getNombre(), prod.getPrecio(), estado);
                System.out.println("  Descripción: " + prod.getDescripcion());
            }
        }
        System.out.println("=============================================\n");
    }
}

