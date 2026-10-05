package app.domain;



public class Producto {

    // Atributos del producto

    private int id;
    private String nombre;
    private String descripcion;
    private double precio;
    private String categoria;
    private boolean disponible; //Agotado o no


    //Constructores crear nuevos productos


    public Producto(int id, String nombre,
                    String descripcion,
                    double precio,
                    String categoria,
                    boolean disponible)
    {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
        this.categoria = categoria;
        this.disponible = disponible;
    }

    //GETTERS SETTERS comunicacion con otras clases


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

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }


}




