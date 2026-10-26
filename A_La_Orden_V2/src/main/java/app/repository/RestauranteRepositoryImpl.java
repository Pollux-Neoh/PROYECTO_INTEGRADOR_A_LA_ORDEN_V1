package app.repository;

import app.domain.Producto;
import app.domain.Restaurante;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// La palabra 'implements' obliga a esta clase a cumplir con la interfaz RestauranteRepository
public class RestauranteRepositoryImpl implements RestauranteRepository {

    private List<Restaurante> listaRestaurantes;


    @Override
    public List<Restaurante> obtenerTodos() {
        // Devolvemos la lista completa de restaurantes
        return listaRestaurantes;
    }

    @Override
    public Optional<Restaurante> buscarPorId(int id) {
        // Usamos Streams para filtrar la lista por el ID recibido

        return listaRestaurantes.stream()
                // Convertimos el int id del restaurante a long para comparar
                .filter(r -> (long) r.getId() == id)
                // Retorna un Optional con el restaurante si lo encuentra
                .findFirst();
    }
}
