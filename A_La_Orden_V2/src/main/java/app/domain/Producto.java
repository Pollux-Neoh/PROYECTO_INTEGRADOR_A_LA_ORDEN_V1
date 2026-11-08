package app.domain;

import java.math.BigDecimal;

public class Producto {

    private final int id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean disponible;
    private String categoria;

    // Constructor para productos que ya tienen un identificador.
    public Producto(
            int id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean disponible,
            String categoria
    ) {
        this(id, nombre, descripcion, precio, disponible, categoria, false);
    }

    // Constructor interno que permite crear temporalmente un producto con ID 0.
    private Producto(
            int id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean disponible,
            String categoria,
            boolean permitirIdTemporal
    ) {
        if (permitirIdTemporal) {
            if (id != 0) {
                throw new IllegalArgumentException(
                        "El identificador temporal debe ser 0."
                );
            }
        } else if (id <= 0) {
            throw new IllegalArgumentException(
                    "El ID del producto debe ser mayor que cero."
            );
        }

        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.disponible = disponible;
        this.categoria = categoria;
    }

    // Crea un producto nuevo que todavía no tiene ID en la base de datos.
    public static Producto crearNuevo(
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean disponible,
            String categoria
    ) {
        return new Producto(
                0,
                nombre,
                descripcion,
                precio,
                disponible,
                categoria,
                true
        );
    }

    // Devuelve una copia del producto con el ID generado por la base de datos.
    public Producto conIdentificadorGenerado(int idGenerado) {

        if (this.id != 0) {
            throw new IllegalStateException(
                    "El producto ya tiene un identificador asignado."
            );
        }

        if (idGenerado <= 0) {
            throw new IllegalArgumentException(
                    "El ID generado debe ser mayor que cero."
            );
        }

        return new Producto(
                idGenerado,
                nombre,
                descripcion,
                precio,
                disponible,
                categoria
        );
    }

    // Reconstruye un producto recuperado de la base de datos.
    public static Producto restaurar(
            int id,
            String nombre,
            String descripcion,
            BigDecimal precio,
            boolean disponible,
            String categoria
    ) {
        return new Producto(
                id,
                nombre,
                descripcion,
                precio,
                disponible,
                categoria
        );
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public BigDecimal getPrecio() {
        return precio;
    }

    public void setPrecio(BigDecimal precio) {
        this.precio = precio;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }
}