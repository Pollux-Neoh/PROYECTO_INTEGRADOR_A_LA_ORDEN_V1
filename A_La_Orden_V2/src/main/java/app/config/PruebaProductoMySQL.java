package app.config;

import app.domain.Producto;
import app.repository.ProductoRepository;
import app.repository.ProductoRepositoryMySQL;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public class PruebaProductoMySQL {

    public static void main(String[] args) {

        ProductoRepository repositorio = new ProductoRepositoryMySQL();

        System.out.println("===== PRODUCTOS REGISTRADOS =====");

        List<Producto> productos = repositorio.obtenerTodos();

        for (Producto producto : productos) {
            System.out.println(
                    producto.getId() + " | "
                            + producto.getNombre() + " | $"
                            + producto.getPrecio()
            );
        }


        System.out.println("\n===== BÚSQUEDA POR ID =====");

        Optional<Producto> resultado = repositorio.buscarPorId(1);

        if (resultado.isPresent()) {
            Producto producto = resultado.get();
            System.out.println("Producto encontrado: " + producto.getNombre());
            System.out.println("Precio: $" + producto.getPrecio());
        } else {
            System.out.println("No se encontró el producto.");
        }

        System.out.println("\n===== REGISTRO DE PRUEBA =====");

        Producto nuevoProducto = Producto.crearNuevo(
                2,
                "Jugo natural de prueba",
                "Producto creado para probar JDBC",
                new BigDecimal("6500.00"),
                true,
                "Bebidas"
        );

        Producto guardado = repositorio.guardar(nuevoProducto);

        System.out.println("Producto registrado correctamente.");
        System.out.println("ID generado: " + guardado.getId());
        System.out.println("Nombre: " + guardado.getNombre());

        System.out.println("\n===== ACTUALIZACIÓN DE PRODUCTO =====");

        Producto productoActualizado = Producto.restaurar(
                guardado.getId(),
                guardado.getRestauranteId(),
                "Jugo natural de mango",
                "Jugo natural preparado con mango",
                new BigDecimal("7500.00"),
                true,
                "Bebidas"
        );

        Producto actualizado = repositorio.actualizar(productoActualizado);

        System.out.println("Producto actualizado correctamente.");
        System.out.println("ID: " + actualizado.getId());
        System.out.println("Nombre: " + actualizado.getNombre());
        System.out.println("Nuevo precio: $" + actualizado.getPrecio());

        System.out.println("\n===== ELIMINACIÓN DE PRODUCTO =====");

        boolean eliminado = repositorio.eliminar(actualizado.getId());

        if (eliminado) {
            System.out.println("Producto eliminado correctamente.");
        } else {
            System.out.println("No se pudo eliminar el producto.");
        }

        System.out.println("\n===== VERIFICAR ELIMINACIÓN =====");

        Optional<Producto> productoEliminado =
                repositorio.buscarPorId(actualizado.getId());

        if (productoEliminado.isEmpty()) {
            System.out.println("Confirmado: el producto ya no existe en MySQL.");
        } else {
            System.out.println("El producto todavía existe.");
        }
    }
}

