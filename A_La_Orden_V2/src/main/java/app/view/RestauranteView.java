package app.view;

import app.domain.Restaurante;
import app.service.RestauranteService;

import java.util.List;
import java.util.Scanner;


public class RestauranteView {

    private Scanner scanner;
    private RestauranteService restauranteService;

    public RestauranteView(RestauranteService restauranteService) {
        this.scanner = new Scanner(System.in);
        this.restauranteService = restauranteService;
    }

    //Menú principal de la funcionalidad US-03 mostrar Menu
    public void mostrarMenuRestaurantes() {
        int opcion = -1;

        while (opcion != 0) {
            System.out.println("\n--- DESCUBRIMIENTO DE RESTAURANTES (US-03) ---");
            System.out.println("1. Ver restaurantes cercanos");
            System.out.println("2. Buscar restaurantes (por nombre o categoría)");
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
}

