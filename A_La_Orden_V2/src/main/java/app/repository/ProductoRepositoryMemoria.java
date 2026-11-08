package app.repository;

import app.domain.Producto;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

// Implementación temporal del repositorio de productos en memoria.
public class ProductoRepositoryMemoria implements ProductoRepository {

    private final Map<Integer, Producto> productos;

    private int siguienteId;

    public ProductoRepositoryMemoria() {
        productos = new LinkedHashMap<>();
        siguienteId = 1;
    }

    @Override
    public Producto guardar(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        if (producto.getId() < 0) {
            throw new IllegalArgumentException(
                    "El ID no puede ser negativo."
            );
        }

        if (producto.getId() == 0) {

            while (productos.containsKey(siguienteId)) {
                siguienteId++;
            }

            producto = producto.conIdentificadorGenerado(siguienteId);
            siguienteId++;

        } else {

            Producto existente = productos.get(producto.getId());

            if (existente != null && existente != producto) {
                throw new IllegalArgumentException(
                        "Ya existe un producto con ese ID."
                );
            }

            if (producto.getId() >= siguienteId
                    && producto.getId() < Integer.MAX_VALUE) {
                siguienteId = producto.getId() + 1;
            }
        }

        productos.put(producto.getId(), producto);

        return producto;
    }

    @Override
    public Optional<Producto> buscarPorId(int id) {
        return Optional.ofNullable(productos.get(id));
    }

    @Override
    public List<Producto> obtenerTodos() {
        return new ArrayList<>(productos.values());
    }

    @Override
    public Producto actualizar(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        if (!productos.containsKey(producto.getId())) {
            throw new IllegalArgumentException(
                    "No existe un producto con ese ID."
            );
        }

        productos.put(producto.getId(), producto);

        return producto;
    }

    @Override
    public boolean eliminar(int id) {
        return productos.remove(id) != null;
    }
}