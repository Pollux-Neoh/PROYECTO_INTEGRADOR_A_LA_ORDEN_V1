package app.service;


import app.domain.Restaurante;
import app.repository.RestauranteRepository;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class RestauranteService {

    //Dependencia del repository, lo necesitamos para los datos crudos
    private RestauranteRepository restauranteRepository;

    //Inyectamos constructor de repositorio
    public RestauranteService(RestauranteRepository restauranteRepository) {
        this.restauranteRepository = restauranteRepository;
    }

    //Criterio US-03 Mostrar restaurantes ordenados por cercania
    public List<Restaurante> obtenerRestaurantesCercanos() {

        //1. Lista original del repo
        List<Restaurante> ListaOriginal = restauranteRepository.obtenerTodos();

        //2. Usamos stream para obtener de menor a mayor por propiedad
        return ListaOriginal.stream()
                .sorted(Comparator.comparingDouble(Restaurante::getDistanciaKm))
                .collect(Collectors.toList());
    }

    //Criterio US-03 Filtrar o Buscar por nombre o categoria

    public List<Restaurante> buscarRestaurantes(String filtro){
        //Si no ingresa texto devuelve la lista completa por cercania

        if (filtro == null || filtro.trim().isEmpty()){
            return obtenerRestaurantesCercanos();
        }

    // Pasamos el texto a minúsculas para ignorar mayúsculas/minúsculas en la búsqueda
    String filtroMiniscula = filtro.toLowerCase().trim();


    // Partimos de la lista QUE YA ESTÁ ORDENADA por cercanía
        return obtenerRestaurantesCercanos().stream()
                .filter(r -> r.getNombre().toLowerCase().contains(filtroMiniscula) ||
                        r.getCategoria().toLowerCase().contains(filtroMiniscula))
                           .collect(Collectors.toList());
    }
}
