import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//Creamos esta clase que considero necesaria para la conexión con la base de datos
public class Database {

    //Esto es para la conecion quded bueno en general no cambiara
    private static final String URL = "jdbc:mysql://localhost:3306/library_db";
    private static final String USER = "root";
    private static final String PASSWORD = "password";

    //Creare un metodo para conexión
    //Esta devolvera una coneccion activa a la base de datos
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
