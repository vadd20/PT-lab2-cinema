package packages.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Getter
public class Session implements Creatable {
    @Setter
    private int id;
    private int hall_id;
    private int film_id;
    private String time;

    public void createByAdmin() {
        System.out.println("введи id зала, id фильма и время");
        Scanner scanner = new Scanner(System.in);
        int hall_id = scanner.nextInt();
        int film_id = scanner.nextInt();
        scanner.next();
        String time = scanner.nextLine();
        this.hall_id = hall_id;
        this.film_id = film_id;
        this.time = time;
    }
}
