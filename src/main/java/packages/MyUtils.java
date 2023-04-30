package packages;

import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.TableCinema;
import packages.DB.TableHall;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

@Component
public class MyUtils {

    private ApplicationContext applicationContext;
    MyUtils(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
    public void getAndSetSessionData() throws SQLException {
        Scanner scanner = new Scanner(System.in);
        int sessionId;
        try {
            sessionId = scanner.nextInt();
        } catch (InputMismatchException exception) {
            System.out.println("Вы ввели неверное число. Попробуйте еще раз");
            getAndSetSessionData();
            return;
        }

        TableCinema tableCinema = applicationContext.getBean(TableCinema.class);
        tableCinema.getCinemaData(sessionId);

        TableHall tableHall = applicationContext.getBean(TableHall.class);
        tableHall.getHallData(sessionId);
    }

    public static ArrayList<ArrayList<String>> createEmptyArrayOfPlaces (int rows, int columns) {
        ArrayList<ArrayList<String>> places = new ArrayList<>();
        for (int i = 0; i < rows; ++i) {
            ArrayList<String> temp = new ArrayList<>();
            for (int j = 0; j < columns; ++j) {
                temp.add("-");
            }
            places.add(temp);
        }
        return places;
    }
}

