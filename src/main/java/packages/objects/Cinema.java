package packages.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import packages.DB.UpdatableInDb;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
@Getter
@Component
public class Cinema implements Creatable, Updatable {
    @Setter private int id;
    private String name;
    private int maxCapacity;
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
        this.maxCapacity = 1000;
        this.address = address;
        this.numberOfFreeHalls = numberOfFreeHalls;
    }


    public void createCinemaFromDb(int id, String name, int maxCapacity, String address, int numberOfFreeHalls) {
        this.id = id;
        this.name = name;
        this.maxCapacity = maxCapacity;
        this.address = address;
        this.numberOfFreeHalls = numberOfFreeHalls;
    }

    @Override
    public void updateByAdmin() throws SQLException {

    }
}
