package app.service;

import app.domain.Producto;
import app.repository.ProductoRepository;
import java.math.BigDecimal;

import java.util.List;
import java.util.Optional;

public class ProductoService {

    private final ProductoRepository productoRepository;

    public ProductoService(ProductoRepository productoRepository) {

        if (productoRepository == null) {
            throw new IllegalArgumentException(
                    "El repositorio de productos no puede ser nulo."
            );
        }

        this.productoRepository = productoRepository;
    }

    // Registra un producto nuevo con ID automático.
    public Producto registrarProducto(
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean disponible,
            String categoria
    ) {

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
                nombre,
                descripcion,
                precio,
                disponible,
                categoria
        );

        return productoRepository.guardar(producto);
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

        if (!productoRepository.buscarPorId(producto.getId()).isPresent()) {
            throw new IllegalArgumentException(
                    "No existe el producto que se desea actualizar."
            );
        }

        return productoRepository.actualizar(producto);
    }

    // Elimina un producto por su ID.
    public boolean eliminarProducto(int id) {
        return productoRepository.eliminar(id);
    }
}
