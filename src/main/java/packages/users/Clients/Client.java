package packages.users.Clients;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.sql.SQLException;

public interface Client {
    void createClientFromDb (int id, String login, String password, String name, String number, String email, int budget, int tickets);

    // метод isExist. проверяет, существует ли юзер в бд. если нет - зарегистируйся
    //
    void chooseAvailableSessions () throws SQLException;
    int chooseSession () throws SQLException;
    void buyTicket(int sessionId) throws SQLException;
}

