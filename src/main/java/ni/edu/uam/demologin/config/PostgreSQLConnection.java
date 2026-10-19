package ni.edu.uam.demologin.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PostgreSQLConnection implements IConnection {

    private static final String URL = "jdbc:postgresql://localhost:5432/JavaFX_JDBC";

    private static final String usuario = "postgres";
    private static final String password = "1234";

    @Override
    public Connection getConnection()  {
        try {
            return DriverManager.getConnection(
                    URL
                    , usuario
                    , password);
        }
        catch (SQLException ex) {
            throw new RuntimeException(
                    "No se pudo conectar a la base de datos: " + ex.getMessage(), ex);
        }
    }
}
