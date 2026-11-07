package app.domain;

import app.domain.enums.RolUsuario;

public class AdministradorRestaurante extends User {

    // Restaurante que administra este usuario.
    private final Restaurante restaurante;

    public AdministradorRestaurante(
            int id,
            String nombre,
            String correo,
            String contrasenia,
            String telefono,
            boolean estado,
            Restaurante restaurante
    ) {

        super(
                id,
                nombre,
                correo,
                contrasenia,
                telefono,
                estado,
                RolUsuario.ADMIN_RESTAURANTE
        );

        if (restaurante == null) {
            throw new IllegalArgumentException(
                    "El restaurante asignado es obligatorio."
            );
        }

        this.restaurante = restaurante;
    }

    public Restaurante getRestaurante() {
        return restaurante;
    }
}
