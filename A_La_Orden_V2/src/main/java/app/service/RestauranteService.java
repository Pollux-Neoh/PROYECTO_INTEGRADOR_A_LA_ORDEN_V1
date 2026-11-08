package app.service;

import app.domain.Restaurante;
import app.repository.RestauranteRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class RestauranteService {

    private final RestauranteRepository restauranteRepository;

    // Recibimos el repositorio mediante inyección de dependencias.
    public RestauranteService(RestauranteRepository restauranteRepository) {

        if (restauranteRepository == null) {
            throw new IllegalArgumentException(
                    "El repositorio de restaurantes es obligatorio."
            );
        }

        this.restauranteRepository = restauranteRepository;
    }

    // US-03: Obtener restaurantes ordenados por cercanía.
    public List<Restaurante> obtenerRestaurantesCercanos() {

        List<Restaurante> listaOriginal =
                restauranteRepository.obtenerTodos();

        if (listaOriginal == null || listaOriginal.isEmpty()) {
            return Collections.emptyList();
        }

        return listaOriginal.stream()
                .filter(restaurante -> restaurante != null)
                .sorted(Comparator.comparing(
                        Restaurante::getDistanciaKm,
                        Comparator.nullsLast(Double::compareTo)
                ))
                .collect(Collectors.toList());
    }

    // US-03: Buscar restaurantes por nombre o categoría.
    public List<Restaurante> buscarRestaurantes(String filtro) {

        if (filtro == null || filtro.trim().isEmpty()) {
            return obtenerRestaurantesCercanos();
        }

        String filtroMinuscula = filtro.trim().toLowerCase();

        return obtenerRestaurantesCercanos().stream()
                .filter(restaurante ->
                        (restaurante.getNombre() != null
                                && restaurante.getNombre()
                                .toLowerCase()
                                .contains(filtroMinuscula))
                                ||
                                (restaurante.getCategoria() != null
                                        && restaurante.getCategoria()
                                        .toLowerCase()
                                        .contains(filtroMinuscula))
                )
                .collect(Collectors.toList());
    }

    // Buscar un restaurante por su identificador.
    public Optional<Restaurante> obtenerRestaurantePorId(int id) {

        if (id <= 0) {
            return Optional.empty();
        }

        return restauranteRepository.buscarPorId(id);
    }
}
