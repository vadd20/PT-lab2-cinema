package packages.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.sql.SQLException;
import java.util.Scanner;

@Getter
@Component
public class Hall implements Creatable {
    @Setter
    private int id;
    private int cinema_id;
    private int rows;
    private int columns;
    private int price;
    private String type;

    public void createByAdmin() throws SQLException {
        System.out.println("введи количество рядов, количество место, цену, тип зала, и id кинотеатра");
        Scanner scanner = new Scanner(System.in);
        int rows = scanner.nextInt();
        int columns = scanner.nextInt();
        int price = scanner.nextInt();
        String type = scanner.next();
        int cinema_id = scanner.nextInt();

        this.rows = rows;
        this.columns = columns;
        this.type = type;
        this.price = price;
        this.cinema_id = cinema_id;

    }

    public void createHallFromDb(int id, int rows, int columns, int price, String type, int cinema_id) {
        this.id = id;
        this.rows = rows;
        this.columns = columns;
        this.price = price;
        this.type = type;
        this.cinema_id = cinema_id;
    }

    public Boolean checkAvailability(int budget) {
        if (budget < this.getPrice()) {
            System.out.println("У вас не хватает бюджета.");
            return false;
        }
        return true;
    }



}
