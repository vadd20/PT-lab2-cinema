package packages.objects;

import lombok.Getter;
import lombok.Setter;

import java.util.Scanner;

@Getter
public class Film implements Creatable {
    @Setter private int id;
    private String name;
    private int year;
    private String genre;
    private int time;
    private String format;

    public void createByAdmin() {
        System.out.println("Введите название, год, жанр, длительность, формат");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        int year = scanner.nextInt();
        scanner.nextLine();
        String genre = scanner.nextLine();
        int time = scanner.nextInt();
        String format = scanner.next();
        this.name = name;
        this.year = year;
        this.genre = genre;
        this.time = time;
        this.format = format;
    }
}
