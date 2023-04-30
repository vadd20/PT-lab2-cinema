package packages.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Getter
public class Film implements Creatable {
    @Setter private int id;
    private String name;
    private int year;
    private String genre;
    private int time;
    private String format;

    public void createByAdmin () {
        System.out.println("введи название, год, жанр, длительность, формат");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.next();
        int year = scanner.nextInt();
        String genre = scanner.next();
        int time = scanner.nextInt();
        String format = scanner.next();
        this.name = name;
        this.year = year;
        this.genre = genre;
        this.time = time;
        this.format = format;
    }
}
