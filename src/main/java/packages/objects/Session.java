package packages.objects;

import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.TableHall;
import packages.DB.TableSession;
import packages.MyUtils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

@Getter
@Component
public class Session implements Creatable {

    @Setter private int id;
    private int hall_id;
    private int film_id;
    private String time;
    private int rows;
    private int columns;
    private ArrayList<ArrayList<String>> places;

    public void createByAdmin() throws SQLException {
        System.out.println("введи id зала, id фильма и время");
        Scanner scanner = new Scanner(System.in);
        int hall_id = scanner.nextInt();
        int film_id = scanner.nextInt();
        scanner.nextLine();
        String time = scanner.nextLine();
        this.hall_id = hall_id;
        this.film_id = film_id;
        this.time = time;

        this.rows = TableHall.getHallRowAndColumn(hall_id).get(0);
        this.columns = TableHall.getHallRowAndColumn(hall_id).get(1);

        this.places = MyUtils.createEmptyArrayOfPlaces(rows, columns);
    }
    public void createSessionFromDb (int id, int hall_id, int film_id, String time,
    ArrayList<ArrayList<String>> places, int rows, int columns) {
        this.id = id;
        this.hall_id = hall_id;
        this.film_id = film_id;
        this.time = time;
        this.places = places;
        this.rows = rows;
        this.columns = columns;
    }

    public void reservePlace(int row, int column) {
        places.get(row).set(column, "*");
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

    public Boolean checkAvailability () {
        if (!this.existsFreePlace()) {
            System.out.println("В этом зале нет мест.");
            return false;
        }

        System.out.println("Введите ряд и место.");
        this.showPlaces();
        return true;
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
