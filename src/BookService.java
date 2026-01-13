import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//Consisero necesario crear pues la logica de acceso a la base de datos
//Bueno resumiendo un CRUD

public class BookService {

    //Creamos los siguentes metodos

    //Obtener todos los libros
    public static List<Book> getAll() throws SQLException {
        List<Book> books = new ArrayList<>();
        String sql = "SELECT * FROM books";

        try (Connection c = Database.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {

            //Esta parte necesaria para crear los libros y recorren los resultados
            while (rs.next()) {
                books.add(new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year")
                ));
            }
        }
        return books;
    }

    //Buscamos un libro por su id
    public static Book getById(int id) throws SQLException {
        String sql = "SELECT * FROM books WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Book(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getInt("year")
                );
            }
        }
        return null;
    }

    //Ahora necesito este metodo para crear un libro
    public static Book create(Book b) throws SQLException {
        String sql = "INSERT INTO books (title, author, year) VALUES (?, ?, ?)";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, b.title);
            ps.setString(2, b.author);
            ps.setInt(3, b.year);
            ps.executeUpdate();

            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            b.id = rs.getInt(1);
        }
        return b;
    }

    //Realizamos el metodo para actualizar
    public static boolean update(int id, Book b) throws SQLException {
        String sql = "UPDATE books SET title=?, author=?, year=? WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, b.title);
            ps.setString(2, b.author);
            ps.setInt(3, b.year);
            ps.setInt(4, id);

            return ps.executeUpdate() > 0;
        }
    }

    //Este que es para eliminar por ID.
    public static boolean delete(int id) throws SQLException {
        String sql = "DELETE FROM books WHERE id=?";
        try (Connection c = Database.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        }
    }
}
