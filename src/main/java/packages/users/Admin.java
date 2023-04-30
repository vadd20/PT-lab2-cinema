package packages.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.*;
import packages.objects.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

@Component
public class Admin {
    private ApplicationContext applicationContext;

    @Autowired
    public Admin(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    private int id;
    private String login;
    private String password;
    private String name;
    public void createAdminFromDb(int id, String login, String password, String name) {
        this.id = id;
        this.login = login;
        this.password = password;
        this.name = name;
    }

    public void createCinema () throws SQLException {
        Creatable cinema = new Cinema();
        cinema.createByAdmin();

        InsertableToDb tableCinema = applicationContext.getBean(TableCinema.class);
        tableCinema.insertToDbByAdmin(cinema);
    }
    //создаем инферфейс для объектов. создаем интерфейс для таблиц объектов

    public void createHall () throws SQLException {
        Creatable hall = new Hall();
        hall.createByAdmin();

        TableCinema tableCinema = applicationContext.getBean(TableCinema.class);
        if (!tableCinema.checkAvailableHalls(((Hall)hall).getCinema_id())) {
            System.out.println("Свободных залов нет или кинотеатр с таким id отсутствует");
            return;
        }
        InsertableToDb tableHall = applicationContext.getBean(TableHall.class);
        tableHall.insertToDbByAdmin(hall);
    }

    public void createSession () throws SQLException {
        Creatable session = new Session();
        session.createByAdmin();

        InsertableToDb tableSession = applicationContext.getBean(TableSession.class);
        tableSession.insertToDbByAdmin(session);
    }

    public void createFilm () throws SQLException {
        Creatable film = new Film();
        film.createByAdmin();

        InsertableToDb tableFilm = applicationContext.getBean(TableFilm.class);
        tableFilm.insertToDbByAdmin(film);
    }

    public void showStats () throws SQLException {
        TablePlaces tablePlaces = applicationContext.getBean(TablePlaces.class);
        tablePlaces.showStatsAndEarnings();
    }

    public void updateCinema () throws SQLException {
        ArrayList <String> data = new ArrayList<>(4);
        Scanner scanner = new Scanner(System.in);
        UpdatableInDb tableCinema = applicationContext.getBean(TableCinema.class);

        System.out.println("Выберите id кинотеатра, который хотите изменить");
        ((TableCinema)tableCinema).show();
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Введите новое название, адрес, количество свободных залов");
        String name = scanner.nextLine();
        String address = scanner.nextLine();
        int numOfFreeHalls = scanner.nextInt();
        data.add(String.valueOf(id));
        data.add(name);
        data.add(address);
        data.add(String.valueOf(numOfFreeHalls));
        tableCinema.updateInDbByAdmin(data);
    }

    public void updateFilm () throws SQLException {
        ArrayList <String> data = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        UpdatableInDb tableFilm = applicationContext.getBean(TableFilm.class);

        System.out.println("Выберите id фильма, который хотите изменить");
        ((TableFilm)tableFilm).show();
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Введите новое название, год, жанр, продолжительность, формат");
        String name = scanner.nextLine();
        int year = scanner.nextInt();
        String genre = scanner.next();
        int time = scanner.nextInt();
        String format = scanner.next();

        data.add(String.valueOf(id));
        data.add(name);
        data.add(String.valueOf(year));
        data.add(genre);
        data.add(String.valueOf(time));
        data.add(format);
        tableFilm.updateInDbByAdmin(data);
    }

    public void updateHall () throws SQLException {
        ArrayList <String> data = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        UpdatableInDb tableHall = applicationContext.getBean(TableHall.class);

        System.out.println("Выберите id зала, который хотите изменить");
        ((TableHall)tableHall).show();
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Введите новое кол-во мест, рядов, цену, тип, id кинотеатра");
        int rows = scanner.nextInt();
        int columns = scanner.nextInt();
        int price = scanner.nextInt();
        String type = scanner.next();
        int cinema_id = scanner.nextInt();

        data.add(String.valueOf(id));
        data.add(String.valueOf(rows));
        data.add(String.valueOf(columns));
        data.add(String.valueOf(price));
        data.add(type);
        data.add(String.valueOf(cinema_id));
        tableHall.updateInDbByAdmin(data);
    }

    public void updateSession () throws SQLException {
        ArrayList <String> data = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);
        UpdatableInDb tableSession = applicationContext.getBean(TableSession.class);

        System.out.println("Выберите id сессии, которую хотите изменить");
        ((TableSession)tableSession).show();
        int id = scanner.nextInt();
        scanner.nextLine();

        System.out.println("Введите новое id зала, фильма, время");
        int hall_id = scanner.nextInt();
        int film_id = scanner.nextInt();
        scanner.nextLine();
        String time = scanner.next();

        data.add(String.valueOf(id));
        data.add(String.valueOf(hall_id));
        data.add(String.valueOf(film_id));
        data.add(time);
        tableSession.updateInDbByAdmin(data);
    }



    public void removeFilm () throws SQLException {
        RemovableFromDb tableFilm = applicationContext.getBean(TableFilm.class);
        ((TableFilm)tableFilm).show();

        System.out.println("Введите id фильма, который хотите удалить");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        tableFilm.removeFromDb(id);
    }

    public void removeCinema () throws SQLException {
        RemovableFromDb tableCinema = applicationContext.getBean(TableCinema.class);
        ((TableCinema)tableCinema).show();

        System.out.println("Введите id фильма, который хотите удалить");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        tableCinema.removeFromDb(id);
    }

    public void removeHall () throws SQLException {
        RemovableFromDb tableHall = applicationContext.getBean(TableHall.class);
        ((TableHall)tableHall).show();

        System.out.println("Введите id фильма, который хотите удалить");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        tableHall.removeFromDb(id);
    }

    public void removeSession () throws SQLException {
        RemovableFromDb tableSession = applicationContext.getBean(TableSession.class);
        ((TableSession)tableSession).show();

        System.out.println("Введите id фильма, который хотите удалить");
        Scanner scanner = new Scanner(System.in);
        int id = scanner.nextInt();
        tableSession.removeFromDb(id);
    }
}
