package packages;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.users.TableAdminImpl;
import packages.DB.users.TableClientImpl;
import packages.DB.users.TableUser;
import packages.users.Admin;
import packages.users.Clients.Client;
import packages.users.Clients.FriendClient;
import packages.users.Clients.SimpleClient;
import packages.users.Clients.VIPClient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

@Component
public class Entering {

    private ApplicationContext applicationContext;

    @Autowired
    public Entering(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    public void start() throws SQLException {
        System.out.println("Чтобы войти в аккаунт, введите 1");
        System.out.println("Чтобы зарегистрироваться как клиент, введите 2");
        System.out.println("Чтобы выйти, введите любой другой символ");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();
        switch (option) {
            case "1" -> chooseUser();
            case "2" -> {
                TableClientImpl tableClient = applicationContext.getBean(TableClientImpl.class);
                tableClient.signInAsClient();
                tryLoginAsClient();
            }
        }
    }

    public void chooseUser() throws SQLException {
        System.out.println("Чтобы войти как админ, введите 1");
        System.out.println("Чтобы войти как клиент, введите 2");
        System.out.println("Чтобы вернуться, введите 3");

        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();
        switch (option) {
            case "1" -> {
                tryLoginAsAdmin();
            }
            case "2" -> {
                tryLoginAsClient();
            }
            case "3" -> {
                start();
            }
            default -> {
                System.out.println("Вы ввели неверное число. Попробуйте еще раз");
                chooseUser();
            }
        }
    }

    private void tryLoginAsAdmin() throws SQLException {
        TableUser tableAdmin = applicationContext.getBean(TableAdminImpl.class);
        System.out.println("Введите логин и пароль");
        int attempts = 0;
        while (attempts < 3) {
            Scanner scanner = new Scanner(System.in);
            String login = scanner.next();
            String password = scanner.next();
            if (tableAdmin.checkLoginPassword(login, password)) {
                Admin admin = applicationContext.getBean(Admin.class);
                stringToAdmin(tableAdmin.getUserDataFromDb(tableAdmin.getIdByLoginPassword(login, password)), admin);
                manageObjects(admin);
                break;
            } else if (attempts != 2) {
                System.out.println("Неверный логин/пароль. Попробуйте еще раз");
            } else {
                System.out.println("Попытки закончились. Выберите пользователя заново");
                chooseUser();
            }
            ++attempts;
        }
    }

    private static void stringToAdmin(ArrayList<String> data, Admin admin) {
        admin.createAdminFromDb(
                Integer.parseInt(data.get(0)),
                data.get(1),
                data.get(2),
                data.get(3)
        );
    }

    private void manageObjects(Admin admin) throws SQLException {
        System.out.println("Чтобы создать объект - введите 1");
        System.out.println("Чтобы изменить существующий - введите 2");
        System.out.println("Чтобы удалить - введите 3");
        System.out.println("Чтобы показать статистику - введите 4");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();
        switch (option) {
            case "1" -> {
                createObject(admin);
                if (isToContinue("выбрать действие")) {
                    manageObjects(admin);
                }
            }
            case "2" -> {
                updateObject(admin);
                if (isToContinue("выбрать действие")) {
                    manageObjects(admin);
                }
            }
            case "3" -> {
                deleteObject(admin);
                if (isToContinue("выбрать действие")) {
                    manageObjects(admin);
                }
            }
            case "4" -> {
                admin.showStats();
                if (isToContinue("выбрать действие")) {
                    manageObjects(admin);
                }
            }
            default -> {
                System.out.println("Вы ввели неверное число. Попробуйте еще раз");
                manageObjects(admin);
            }
        }
    }

    private void createObject(Admin admin) throws SQLException {
        System.out.println("Чтобы создать кинотеатр, введите - 1");
        System.out.println("Чтобы создать зал, введите - 2");
        System.out.println("Чтобы создать сеанс, введите - 3");
        System.out.println("Чтобы создать фильм, введите - 4");

        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();
        switch (option) {
            case "1" -> {
                admin.createCinema();
                if (isToContinue("создать объект")) {
                    createObject(admin);
                }
            }
            case "2" -> {
                admin.createHall();
                if (isToContinue("создать объект")) {
                    createObject(admin);
                }
            }
            case "3" -> {
                admin.createSession();
                if (isToContinue("создать объект")) {
                    createObject(admin);
                }
            }
            case "4" -> {
                admin.createFilm();
                if (isToContinue("создать объект")) {
                    createObject(admin);
                }
            }
            default -> {
                System.out.println("Вы ввели неверное число. Попробуйте еще раз");
                createObject(admin);
            }
        }
    }

    private void updateObject(Admin admin) throws SQLException {
        System.out.println("Чтобы изменить кинотеатр, введите - 1");
        System.out.println("Чтобы изменить зал, введите - 2");
        System.out.println("Чтобы изменить сеанс, введите - 3");
        System.out.println("Чтобы изменить фильм, введите - 4");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();
        switch (option) {
            case "1" -> {
                admin.updateCinema();
                if (isToContinue("обновить объект")) {
                    updateObject(admin);
                }
            }
            case "2" -> {
                admin.updateHall();
                if (isToContinue("обновить объект")) {
                    updateObject(admin);
                }
            }
            case "3" -> {
                admin.updateSession();
                if (isToContinue("обновить объект")) {
                    updateObject(admin);
                }
            }
            case "4" -> {
                admin.updateFilm();
                if (isToContinue("обновить объект")) {
                    updateObject(admin);
                }
            }
            default -> {
                System.out.println("Вы ввели неверное число. Попробуйте еще раз");
                updateObject(admin);
            }
        }
    }

    private void deleteObject(Admin admin) throws SQLException {
        System.out.println("Чтобы удалить кинотеатр, введите - 1");
        System.out.println("Чтобы удалить зал, введите - 2");
        System.out.println("Чтобы удалить сеанс, введите - 3");
        System.out.println("Чтобы удалить фильм, введите - 4");
        Scanner scanner = new Scanner(System.in);
        String option = scanner.nextLine();
        switch (option) {
            case "1" -> {
                admin.removeCinema();
                if (isToContinue("удалить объект")) {
                    deleteObject(admin);
                }
            }
            case "2" -> {
                admin.removeHall();
                if (isToContinue("удалить объект")) {
                    deleteObject(admin);
                }
            }
            case "3" -> {
                admin.removeSession();
                if (isToContinue("удалить объект")) {
                    deleteObject(admin);
                }
            }
            case "4" -> {
                admin.removeFilm();
                if (isToContinue("удалить объект")) {
                    deleteObject(admin);
                }
            }
            default -> {
                System.out.println("Вы ввели неверное число. Попробуйте еще раз");
                deleteObject(admin);
            }
        }
    }
    public static Boolean isToContinue(String action) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Если хотите еще " + action + ", введите yes");
        String answer = scanner.next();
        return answer.equals("yes");
    }


    private void tryLoginAsClient() throws SQLException {
        TableClientImpl tableClient = applicationContext.getBean(TableClientImpl.class);
        System.out.println("Введите логин и пароль");
        int attempts = 0;
        while (attempts < 3) {
            Scanner scanner = new Scanner(System.in);
            String login = scanner.next();
            String password = scanner.next();
            if (tableClient.checkLoginPassword(login, password)) {
                chooseClientStatus(tableClient, tableClient.getIdByLoginPassword(login, password));
                break;
            } else if (attempts != 2) {
                System.out.println("Неверный логин/пароль. Попробуйте еще раз");
            } else {
                System.out.println("Попытки закончились");
                chooseUser();
            }
            ++attempts;
        }
    }

    public void chooseClientStatus(TableClientImpl tableClient, int id) throws SQLException {
        String status = tableClient.checkStatus(id);
        if (status.equals("simple")) {
            Client simpleClient = applicationContext.getBean(SimpleClient.class);
            bookTicketInAvailableSession(tableClient, id, simpleClient);
        }
        if (status.equals("friend")) {
            FriendClient friendClient = applicationContext.getBean(FriendClient.class);
            bookTicketInAvailableSession(tableClient, id, friendClient);
        }
        if (status.equals("VIP")) {
            VIPClient vipClient = applicationContext.getBean(VIPClient.class);
            bookTicketInAvailableSession(tableClient, id, vipClient);
        }
    }

    private void bookTicketInAvailableSession(TableClientImpl tableClient, int id, Client client)
            throws SQLException {
        stringToClient(tableClient.getUserDataFromDb(id), client);
        client.chooseAvailableSessions();
        client.chooseSession();
        client.buyTicket();
        if (isToContinue("забронировать билет")) {
            bookTicketInAvailableSession(tableClient, id, client);
        } else {
            chooseUser();
        }
    }

    private static void stringToClient(ArrayList<String> data, Client client) {
        client.createClientFromDb(
                Integer.parseInt(data.get(0)),
                data.get(1),
                data.get(2),
                data.get(3),
                data.get(4),
                data.get(5),
                Integer.parseInt(data.get(6)),
                Integer.parseInt(data.get(7))
        );
    }
}
