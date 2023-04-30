package packages.DB.users;

import packages.users.Clients.FriendClient;
import packages.users.Clients.SimpleClient;
import packages.users.Clients.VIPClient;

import java.sql.SQLException;
import java.util.ArrayList;

public interface TableUser {
    void initDb() throws SQLException;
    Boolean checkLoginPassword(String login, String password) throws SQLException;
    int getIdByLoginPassword (String login, String password) throws SQLException;
    ArrayList<String> getUserDataFromDb (int id) throws SQLException;
}
