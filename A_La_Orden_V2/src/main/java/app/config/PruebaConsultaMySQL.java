
package app.config;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PruebaConsultaMySQL {

    public static void main(String[] args) {

        String sql = "SHOW TABLES";

        try (
                Connection conexion = ConexionMySQL.obtenerConexion();
                Statement sentencia = conexion.createStatement();
                ResultSet resultado = sentencia.executeQuery(sql)
        ) {

            System.out.println("Conexión exitosa.");
            System.out.println("Tablas de la base de datos a_la_orden:");

            boolean hayTablas = false;

            while (resultado.next()) {

                hayTablas = true;

                String nombreTabla = resultado.getString(1);

                System.out.println("- " + nombreTabla);
            }

            if (!hayTablas) {
                System.out.println("Todavía no hay tablas creadas.");
            }

        } catch (Exception e) {

            System.out.println("Error al consultar las tablas.");
            System.out.println(e.getMessage());
        }
    }
}
