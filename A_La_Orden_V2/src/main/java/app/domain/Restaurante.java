package app.domain;


import java.util.ArrayList;
import java.util.List;

public class Restaurante {

    //Definir atributos
    private int id;
    private String nombre;
    private String imagenUrl;
    private Double CalificacionPromedio;
    private Double distanciaKm;
    private String categoria;


    //El menu del restaurante
    private List<Producto> menu;

    //constructor
    public Restaurante(int id, String nombre,
                       String imagenUrl,
                       Double calificacionPromedio,
                       Double distanciaKm,
                       String categoria) {
        this.id = id;
        this.nombre = nombre;
        this.imagenUrl = imagenUrl;
        CalificacionPromedio = calificacionPromedio;
        this.distanciaKm = distanciaKm;
        this.categoria = categoria;

        //Inicializar la lista vacia necesario
        this.menu = new ArrayList<>();
    }


    // GETTERS SETTERS


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
        return CalificacionPromedio;
    }

    public void setCalificacionPromedio(Double calificacionPromedio) {
        CalificacionPromedio = calificacionPromedio;
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



    //METODOS DEL MENU

    public List<Producto> getMenu() {
        return menu;
    }

    public void setMenu(List<Producto> menu) {
        this.menu = menu;
    }

    public void agregarProducto(Producto producto) {
        this.menu.add(producto);
    }

}
