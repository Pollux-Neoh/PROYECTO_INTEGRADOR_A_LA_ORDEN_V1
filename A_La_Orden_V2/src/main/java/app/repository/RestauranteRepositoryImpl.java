package app.repository;

import app.domain.Restaurante;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// Implementación en memoria del repositorio de restaurantes.
public class RestauranteRepositoryImpl implements RestauranteRepository {

    private final List<Restaurante> listaRestaurantes;

    public RestauranteRepositoryImpl() {
        this.listaRestaurantes = new ArrayList<>();
        cargarDatosPrueba();
    }

    // Carga restaurantes de prueba para trabajar sin base de datos.
    private void cargarDatosPrueba() {
        listaRestaurantes.add(new Restaurante(
                1, "Donde Lola", "https://img.com/lola.jpg",
                4.8, 1.2, "Almuerzo Ejecutivo"
        ));

        listaRestaurantes.add(new Restaurante(
                2, "El Sazón Paisa", "https://img.com/sazon.jpg",
                4.5, 0.5, "Comida Típica"
        ));

        listaRestaurantes.add(new Restaurante(
                3, "La Esquina Gourmet", "https://img.com/gourmet.jpg",
                4.2, 3.0, "Almuerzo Ejecutivo"
        ));

        listaRestaurantes.add(new Restaurante(
                4, "Verde & Sano", "https://img.com/verde.jpg",
                4.9, 0.8, "Ensaladas"
        ));
    }

    @Override
    public List<Restaurante> obtenerTodos() {
        // Devolvemos una copia para proteger la lista interna.
        return new ArrayList<>(listaRestaurantes);
    }

    @Override
    public Optional<Restaurante> buscarPorId(int id) {
        return listaRestaurantes.stream()
                .filter(restaurante -> restaurante.getId() == id)
                .findFirst();
    }
}
