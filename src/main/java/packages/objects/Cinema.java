package packages.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.Scanner;

@Getter
@Component
public class Cinema implements Creatable {
    @Setter private int id;
    private String name;
    private String address;
    private int numberOfFreeHalls;

    @Override
    public void createByAdmin() {
        System.out.println("введи название, адрес и количество свободных залов");
        Scanner scanner = new Scanner(System.in);
        String name = scanner.nextLine();
        String address = scanner.nextLine();
        int numberOfFreeHalls = scanner.nextInt();
        this.name = name;
        this.address = address;
        this.numberOfFreeHalls = numberOfFreeHalls;
    }
}
