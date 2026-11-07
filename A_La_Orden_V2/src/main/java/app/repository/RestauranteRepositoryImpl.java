package app.repository;

import app.domain.Producto;
import app.domain.Restaurante;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// La palabra 'implements' obliga a esta clase a cumplir con la interfaz RestauranteRepository
public class RestauranteRepositoryImpl implements RestauranteRepository {

    private List<Restaurante> listaRestaurantes;

    // Constructor para inicializar la lista y cargar los datos de prueba
    public RestauranteRepositoryImpl() {
        listaRestaurantes = new ArrayList<>();
        cargarDatosPrueba();
    }

    private void cargarDatosPrueba() {
        listaRestaurantes.add(new Restaurante(1, "Donde Lola", "https://img.com/lola.jpg", 4.8, 1.2, "Almuerzo Ejecutivo"));
        listaRestaurantes.add(new Restaurante(2, "El Sazón Paisa", "https://img.com/sazon.jpg", 4.5, 0.5, "Comida Típica"));
        listaRestaurantes.add(new Restaurante(3, "La Esquina Gourmet", "https://img.com/gourmet.jpg", 4.2, 3.0, "Almuerzo Ejecutivo"));
        listaRestaurantes.add(new Restaurante(4, "Verde & Sano", "https://img.com/verde.jpg", 4.9, 0.8, "Ensaladas"));
    }

    @Override
    public List<Restaurante> obtenerTodos() {
        // Devolvemos la lista completa de restaurantes
        return listaRestaurantes;
    }

    @Override
    public Optional<Restaurante> buscarPorId(int id) {
        // Usamos Streams para filtrar la lista por el ID recibido
        return listaRestaurantes.stream()
                .filter(r -> r.getId() == id)
                .findFirst();
    }
}
