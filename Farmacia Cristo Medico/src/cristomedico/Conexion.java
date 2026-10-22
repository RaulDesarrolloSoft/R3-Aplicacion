package cristomedico;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {
    private static final String URL =
        "jdbc:mysql://localhost:3306/cristo_medico?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";     // cambia si es necesario
    private static final String PASS = "4444";         // tu contraseña de MySQL

    public static Connection getConexion() {
        Connection con = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection(URL, USER, PASS);
        } catch (ClassNotFoundException | SQLException e) {
            System.out.println("Error de conexión: " + e.getMessage());
        }
        return con;
    }
}