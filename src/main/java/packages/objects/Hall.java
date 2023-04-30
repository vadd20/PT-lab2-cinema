package packages.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.TableCinema;
import packages.MyUtils;
import packages.objects.Cinema;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

@Getter
@Component
public class Hall implements Creatable {
    @Setter private int id;
    private int cinema_id;
    private int rows;
    private int columns;
    private int price;
    private String type;
    private ArrayList<ArrayList<String>> places;

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

        this.places = MyUtils.createEmptyArrayOfPlaces(rows, columns);
    }

    public void createHallFromDb (int id, int rows, int columns, int price, String type, int cinema_id, ArrayList<ArrayList<String>> places) {
        this.id = id;
        this.rows = rows;
        this.columns = columns;
        this.price = price;
        this.type = type;
        this.cinema_id = cinema_id;
        this.places = places;
    }

    public void showPlaces () {
        System.out.printf("%2s", " ");
        for (int i = 0; i < columns; ++i) {
            System.out.printf("%2s", i + 1);
        }
        System.out.println();
        for (int i = 0; i < rows; ++i) {
            System.out.print(i + 1 + " ");
            for (int j = 0; j < columns; ++j) {
                System.out.printf("%2s", places.get(i).get(j));
            }
            System.out.println();
        }
    }

    public Boolean checkAvailability (int budget) {
        if (!this.existsFreePlace()) {
            System.out.println("В этом зале нет мест.");
            return false;
        }

        if (budget < this.getPrice()) {
            System.out.println("У вас не хватает бюджета.");
            return false;
        }

        System.out.println("Введите ряд и место.");
        this.showPlaces();
        return true;
    }
    public void reservePlace(int row, int column) {
        places.get(row).set(column, "*");
    }

    public Boolean existsFreePlace() {
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                if (this.places.get(i).get(j).equals("-")) {
                    return true;
                }
            }
        }
        return false;
    }


}
