package app.domain;

import app.domain.enums.RolUsuario;

public abstract class User {
    //Atributos protegidos solo puede acceder las clases hijas
    protected int id;
    protected String nombre;
    protected String correo;
    protected String contrasenia;
    protected String telefono;
    protected boolean estado;
    protected RolUsuario rolUsuario;


    //---------------------------
//Creacion del constructor
    public User(){

    }
    public User(int id, String nombre, String correo, String contrasenia, String telefono, boolean estado, RolUsuario rolUsuario) {
        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasenia = contrasenia;
        this.telefono = telefono;
        this.estado = estado;
        this.rolUsuario = rolUsuario;
    }


    //---------------------------
// GETTERS AND SETTERS


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

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public RolUsuario getRolUsuario() {
        return rolUsuario;
    }

    public void setRolUsuario(RolUsuario rolUsuario) {
        this.rolUsuario = rolUsuario;
    }


    //---------------------------
    //METODOS DEL USER

    public boolean login(){
        System.out.println("Iniciando login: " + this.correo);
        return false;
    }

    public void logout(){

        System.out.println("Cerrando sesion: " + this.nombre);
    }

    public void actualizarDatos(){

        System.out.println("Actualizando datos del usuario ID: " + this.id);
    }


}
