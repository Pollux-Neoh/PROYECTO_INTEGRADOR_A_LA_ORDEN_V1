package app.repository;

import app.domain.Producto;
import app.domain.Restaurante;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

// La palabra 'implements' obliga a esta clase a cumplir con la interfaz RestauranteRepository
public class RestauranteRepositoryImpl implements RestauranteRepository {

    private List<Restaurante> listaRestaurantes;

    // En el constructor inicializamos una lista con datos ficticios de prueba
    public RestauranteRepositoryImpl() {
        listaRestaurantes = new ArrayList<>();
        cargarDatosPrueba();
    }

    private void cargarDatosPrueba() {
        // 1. Creamos las instancias de los restaurantes
        Restaurante rest1 = new Restaurante(1, "Donde Lola", "https://img.com/lola.jpg", 4.8, 1.2, "Almuerzo Ejecutivo");
        Restaurante rest2 = new Restaurante(2, "El Sazón Paisa", "https://img.com/sazon.jpg", 4.5, 0.5, "Comida Típica");
        Restaurante rest3 = new Restaurante(3, "La Esquina Gourmet", "https://img.com/gourmet.jpg", 4.2, 3.0, "Almuerzo Ejecutivo");
        Restaurante rest4 = new Restaurante(4, "Verde & Sano", "https://img.com/verde.jpg", 4.9, 0.8, "Ensaladas");

        // 2. Cargamos menú/productos de prueba a "Donde Lola" (ID 1)
        rest1.agregarProducto(new Producto(1, "Ejecutivo de Res", "Carne asada, arroz, frijoles, tajada y ensalada", 18000.0, "Platos Principales", true));
        rest1.agregarProducto(new Producto(2, "Sancocho Trifásico", "Res, cerdo y pollo con arroz y aguacate", 22000.0, "Platos Principales", true));
        rest1.agregarProducto(new Producto(3, "Jugo Natural de Maracuyá", "En agua o en leche", 5000.0, "Bebidas", true));
        rest1.agregarProducto(new Producto(4, "Postre de Natas", "Postre artesanal de la casa", 6500.0, "Postres", false)); // Agotado

// 3. Cargamos productos a "El Sazón Paisa" (ID 2)
        rest2.agregarProducto(new Producto(5, "Bandeja Paisa", "Chicharrón, carne molida, huevo, chorizo y morcilla", 28000.0, "Típicos", true));
        rest2.agregarProducto(new Producto(6, "Limonada Cerezada", "Bebida refrescante de la casa", 7000.0, "Bebidas", true));

        // 4. Guardamos los restaurantes en la lista general
        listaRestaurantes.add(rest1);
        listaRestaurantes.add(rest2);
        listaRestaurantes.add(rest3);
        listaRestaurantes.add(rest4);
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
                // Convertimos el int id del restaurante a long para comparar
                .filter(r -> (long) r.getId() == id)
                // Retorna un Optional con el restaurante si lo encuentra
                .findFirst();
    }
}
