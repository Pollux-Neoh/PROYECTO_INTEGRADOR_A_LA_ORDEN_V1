package app.service;

import app.domain.Producto;
import app.domain.Restaurante;
import app.repository.ProductoRepository;
import app.repository.RestauranteRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class ProductoService {

    private final ProductoRepository productoRepository;
    private final RestauranteRepository restauranteRepository;

    public ProductoService(
            ProductoRepository productoRepository,
            RestauranteRepository restauranteRepository
    ) {

        if (productoRepository == null) {
            throw new IllegalArgumentException(
                    "El repositorio de productos no puede ser nulo."
            );
        }

        if (restauranteRepository == null) {
            throw new IllegalArgumentException(
                    "El repositorio de restaurantes no puede ser nulo."
            );
        }

        this.productoRepository = productoRepository;
        this.restauranteRepository = restauranteRepository;
    }

    // Registra un producto nuevo asociado a un restaurante existente.
    public Producto registrarProducto(
            int restauranteId,
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean disponible,
            String categoria
    ) {

        Restaurante restaurante = restauranteRepository
                .buscarPorId(restauranteId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "El restaurante seleccionado no existe."
                ));

        if (restauranteId <= 0) {
            throw new IllegalArgumentException(
                    "El ID del restaurante debe ser mayor que cero."
            );
        }

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre del producto es obligatorio."
            );
        }

        if (precio == null || precio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "El precio debe ser mayor que cero."
            );
        }

        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "La categoría es obligatoria."
            );
        }

        Producto producto = Producto.crearNuevo(
                restauranteId,
                nombre,
                descripcion,
                precio,
                disponible,
                categoria
        );

        Producto productoGuardado = productoRepository.guardar(producto);

        restaurante.agregarProducto(productoGuardado);

        return productoGuardado;
    }

    // Busca un producto por su ID.
    public Optional<Producto> buscarPorId(int id) {
        return productoRepository.buscarPorId(id);
    }

    // Obtiene todos los productos.
    public List<Producto> obtenerTodos() {
        return productoRepository.obtenerTodos();
    }

    // Actualiza un producto existente.
    public Producto actualizarProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        Producto productoExistente = productoRepository
                .buscarPorId(producto.getId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "No existe el producto que se desea actualizar."
                ));

        if (productoExistente.getRestauranteId() != producto.getRestauranteId()) {
            throw new IllegalArgumentException(
                    "No se puede cambiar el restaurante de un producto existente."
            );
        }

        Restaurante restaurante = restauranteRepository
                .buscarPorId(producto.getRestauranteId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "El restaurante asociado al producto no existe."
                ));

        Producto productoActualizado = productoRepository.actualizar(producto);

        restaurante.actualizarProducto(productoActualizado);

        return productoActualizado;
    }

    // Elimina un producto del repositorio y del menú del restaurante.
    public boolean eliminarProducto(int id) {

        Optional<Producto> productoEncontrado =
                productoRepository.buscarPorId(id);

        if (!productoEncontrado.isPresent()) {
            return false;
        }

        Producto producto = productoEncontrado.get();

        boolean eliminado = productoRepository.eliminar(id);

        if (eliminado) {
            restauranteRepository.buscarPorId(producto.getRestauranteId())
                    .ifPresent(restaurante ->
                            restaurante.eliminarProducto(id)
                    );
        }

        return eliminado;
    }
}

