
package app.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class ConexionMySQL {

    private static final Properties propiedades = new Properties();

    static {
        try (FileInputStream archivo =
                     new FileInputStream("A_La_Orden_V2/db.properties")) {

            propiedades.load(archivo);

        } catch (IOException e) {
            throw new ExceptionInInitializerError(
                    "No se pudo cargar db.properties: " + e.getMessage()
            );
        }
    }

    public static Connection obtenerConexion() throws SQLException {

        String url = propiedades.getProperty("db.url");
        String usuario = propiedades.getProperty("db.user");
        String contrasenia = propiedades.getProperty("db.password");

        return DriverManager.getConnection(
                url,
                usuario,
                contrasenia
        );
    }
}