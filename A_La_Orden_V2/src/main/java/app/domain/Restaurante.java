
package app.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Restaurante {

    private int id;
    private String nombre;
    private String imagenUrl;
    private Double calificacionPromedio;
    private Double distanciaKm;
    private String categoria;

    // Productos asociados al menú del restaurante.
    private final List<Producto> menu;

    public Restaurante(
            int id,
            String nombre,
            String imagenUrl,
            Double calificacionPromedio,
            Double distanciaKm,
            String categoria) {

        this.id = id;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        this.calificacionPromedio = calificacionPromedio;
        this.distanciaKm = distanciaKm;
        this.categoria = categoria;

        this.menu = new ArrayList<>();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getImagenUrl() {
        return imagenUrl;
    }

    public void setImagenUrl(String imagenUrl) {
        this.imagenUrl = imagenUrl;
    }

    public Double getCalificacionPromedio() {
        return calificacionPromedio;
    }

    public void setCalificacionPromedio(Double calificacionPromedio) {
        this.calificacionPromedio = calificacionPromedio;
    }

    public Double getDistanciaKm() {
        return distanciaKm;
    }

    public void setDistanciaKm(Double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    // Devuelve una copia no modificable del menú.
    public List<Producto> getMenu() {
        return Collections.unmodifiableList(
                new ArrayList<>(menu)
        );
    }

    // Reemplaza el menú validando que todos los productos pertenezcan al restaurante.
    public void setMenu(List<Producto> menu) {

        if (menu == null) {
            throw new IllegalArgumentException(
                    "El menú no puede ser nulo."
            );
        }

        if (menu.contains(null)) {
            throw new IllegalArgumentException(
                    "El menú no puede contener productos nulos."
            );
        }

        for (Producto producto : menu) {
            if (producto.getRestauranteId() != this.id) {
                throw new IllegalArgumentException(
                        "El producto no pertenece a este restaurante."
                );
            }
        }

        this.menu.clear();
        this.menu.addAll(menu);
    }

    // Agrega un producto al menú si pertenece a este restaurante.
    public void agregarProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        if (producto.getRestauranteId() != this.id) {
            throw new IllegalArgumentException(
                    "El producto no pertenece a este restaurante."
            );
        }

        this.menu.add(producto);
    }


    // Reemplaza un producto existente dentro del menú.
    public void actualizarProducto(Producto producto) {

        if (producto == null) {
            throw new IllegalArgumentException(
                    "El producto no puede ser nulo."
            );
        }

        if (producto.getRestauranteId() != this.id) {
            throw new IllegalArgumentException(
                    "El producto no pertenece a este restaurante."
            );
        }

        for (int i = 0; i < menu.size(); i++) {
            if (menu.get(i).getId() == producto.getId()) {
                menu.set(i, producto);
                return;
            }
        }
    }

    // Elimina un producto del menú mediante su ID.
    public void eliminarProducto(int productoId) {

        menu.removeIf(producto -> producto.getId() == productoId);
    }
}