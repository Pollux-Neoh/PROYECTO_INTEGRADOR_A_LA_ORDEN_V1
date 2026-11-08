
package app.config;

import java.sql.Connection;
import java.sql.SQLException;

public class PruebaConexionMySQL {

    public static void main(String[] args) {

        try (Connection conexion = ConexionMySQL.obtenerConexion()) {

            System.out.println("¡Conexión exitosa con MySQL!");

            System.out.println(
                    "Base de datos: " +
                            conexion.getCatalog()
            );

            System.out.println(
                    "Versión del servidor: " +
                            conexion.getMetaData().getDatabaseProductVersion()
            );

        } catch (SQLException e) {

            System.out.println("Error al conectar con MySQL.");
            System.out.println(e.getMessage());
        }
    }
}
