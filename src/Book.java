//Empecemos creando la clase Book
//Esta es importante para la base de datos
public class Book {

    //Creare los atributos publicos
    public int id;
    public String title;
    public String author;
    public int year;

    //Constructores

    //Este esta vacio necesario para objetos sin nada
    public Book(){}

    //Este pues con todos los datos
    public Book(int id, String title, String author, int year){
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
    }
}
