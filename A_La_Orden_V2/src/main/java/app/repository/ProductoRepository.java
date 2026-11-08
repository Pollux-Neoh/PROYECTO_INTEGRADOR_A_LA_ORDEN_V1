package app.repository;

import app.domain.Producto;

import java.util.List;
import java.util.Optional;

public interface ProductoRepository {

    // Guarda un producto y devuelve el producto almacenado.
    Producto guardar(Producto producto);

    // Busca un producto por su identificador.
    Optional<Producto> buscarPorId(int id);

    // Consulta todos los productos.
    List<Producto> obtenerTodos();

    // Actualiza un producto existente.
    Producto actualizar(Producto producto);

    // Elimina un producto por su identificador.
    boolean eliminar(int id);
}