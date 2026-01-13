import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

//En el mean creare el servidir HTTP usando los sockets.
public class Main {

    public static void main(String[] args) throws Exception {
        // Creamos el servidor en el puerto 8080
        ServerSocket server = new ServerSocket(8080);
        System.out.println("Servidor iniciado en puerto 8080");

        //Esto es para que escucha la conexion
        while (true) {
            Socket client = server.accept();
            handle(client);
        }
    }

    //Empezamos con los metodos

    //Este sera super util para el manejo de peticiones HTTP
    private static void handle(Socket client) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                OutputStream out = client.getOutputStream()
        ) {
            String requestLine = in.readLine();
            if (requestLine == null) return;

            String[] request = requestLine.split(" ");
            String method = request[0];
            String path = request[1];

            int contentLength = 0;
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            String body = "";
            if (contentLength > 0) {
                char[] buffer = new char[contentLength];
                in.read(buffer);
                body = new String(buffer);
            }

            if (method.equals("GET") && path.equals("/books")) {
                List<Book> books = BookService.getAll();
                send(out, 200, booksToJson(books));
            }
            else if (method.equals("GET") && path.matches("/books/\\d+")) {
                Book b = BookService.getById(Integer.parseInt(path.split("/")[2]));
                send(out, b != null ? 200 : 404, b != null ? bookToJson(b) : "{}");
            }
            else if (method.equals("POST") && path.equals("/books")) {
                Book b = parseBook(body);
                send(out, 201, bookToJson(BookService.create(b)));
            }
            else if (method.equals("PUT") && path.matches("/books/\\d+")) {
                Book b = parseBook(body);
                boolean ok = BookService.update(Integer.parseInt(path.split("/")[2]), b);
                send(out, ok ? 200 : 404, "{}");
            }
            else if (method.equals("DELETE") && path.matches("/books/\\d+")) {
                boolean ok = BookService.delete(Integer.parseInt(path.split("/")[2]));
                send(out, ok ? 200 : 404, "{}");
            }
            else {
                send(out, 404, "{}");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Transformamos un Objeto libro en JSON
    private static Book parseBook(String json) {
        json = json.replaceAll("[{}\"]", "");
        String[] fields = json.split(",");
        Book b = new Book();

        for (String f : fields) {
            String[] kv = f.split(":");
            if (kv[0].trim().equals("title")) b.title = kv[1];
            if (kv[0].trim().equals("author")) b.author = kv[1];
            if (kv[0].trim().equals("year")) b.year = Integer.parseInt(kv[1]);
        }
        return b;
    }

    //Convierte un libro a JSON
    private static String bookToJson(Book b) {
        return String.format(
                "{\"id\":%d,\"title\":\"%s\",\"author\":\"%s\",\"year\":%d}",
                b.id, b.title, b.author, b.year
        );
    }

    //Este para una lista de libros
    private static String booksToJson(List<Book> books) {
        String json = "[";
        for (Book b : books) json += bookToJson(b) + ",";
        if (!books.isEmpty()) json = json.substring(0, json.length() - 1);
        return json + "]";
    }

    //Esta es para la repuesta HTTP al cliente
    private static void send(OutputStream out, int status, String body) throws IOException {
        String response =
                "HTTP/1.1 " + status + " OK\r\n" +
                        "Content-Type: application/json\r\n" +
                        "Content-Length: " + body.length() + "\r\n\r\n" +
                        body;

        out.write(response.getBytes());
        out.flush();
    }
}