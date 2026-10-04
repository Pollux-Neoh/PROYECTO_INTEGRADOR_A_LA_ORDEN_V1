package app.domain;

import app.domain.enums.RolUsuario;

public class Cliente extends User {

    private String direccion;

    public Cliente() {
        super();
        this.rolUsuario = RolUsuario.CLIENTE;
    }


    public Cliente(int id, String nombre, String correo, String contrasenia,
                   String telefono, boolean estado, String direccion) {

        super(id, nombre, correo, contrasenia, telefono, estado, RolUsuario.CLIENTE);
        this.direccion = direccion;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}

