package packages.DB.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.DbUtil;
import packages.users.Clients.SimpleClient;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

@Component
public class TableClientImpl implements TableUser {

    private DataSource dataSource;
    private ApplicationContext applicationContext;

    @Autowired
    void TableClient(DataSource dataSource, ApplicationContext applicationContext) throws SQLException {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
        initDb();
    }

    @Override
    public void initDb() throws SQLException {
        String createClientTable = "" +
                "CREATE TABLE IF NOT EXISTS clients (" +
                "   id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL," +
                "   login text NOT NULL," +
                "   password text NOT NULL," +
                "   name text NOT NULL," +
                "   number text NOT NULL," +
                "   email text NOT NULL," +
                "   budget integer NOT NULL," +
                "   type text NOT NULL," +
                "   tickets integer NOT NULL" +
                ")";
        DbUtil.applyDdl(createClientTable, dataSource);
    }

    @Override
    public Boolean checkLoginPassword(String login, String password) throws SQLException {
        String selectQuery = "SELECT id FROM clients WHERE login = ? and password = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            return rs.next();
        }
    }

    @Override
    public int getIdByLoginPassword(String login, String password) throws SQLException {
        String selectQuery = "SELECT id FROM clients WHERE login = ? and password = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, login);
            preparedStatement.setString(2, password);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return rs.getInt(1);
        }
    }

    @Override
    public ArrayList<String> getUserDataFromDb(int id) throws SQLException {
        String selectQuery = "SELECT id, login, password, name, number, email, budget, tickets " +
                "FROM clients WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {

            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            ArrayList<String> data = new ArrayList<>();
            rs.next();

            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                data.add(rs.getString(i));
            }
            return data;
        }
    }

    public String checkStatus(int id) throws SQLException {
        String selectQuery = "SELECT type FROM clients WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return rs.getString(1);
        }
    }

    public void reduceBudgetAndIncreaseTickets(int id, int newBudget, int tickets) throws SQLException {
        String updateQuery = "UPDATE clients SET budget = ?, tickets = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, newBudget);
            preparedStatement.setInt(2, tickets);
            preparedStatement.setInt(3, id);
            preparedStatement.executeUpdate();
        }
    }


    public void changeStatus(int id, String newStatus) throws SQLException {
        String updateQuery = "UPDATE clients SET type = ? WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, newStatus);
            preparedStatement.setInt(2, id);
            preparedStatement.executeUpdate();
        }
    }

    public void signInAsClient() throws SQLException {
        System.out.println("введите логин, пароль, имя, номер, email, бюджет");
        Scanner scanner = new Scanner(System.in);
        String login = scanner.next();
        String password = scanner.next();
        String name = scanner.next();
        String number = scanner.next();
        String email = scanner.next();
        int budget = scanner.nextInt();

        String selectQuery = "SELECT * FROM clients WHERE login = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setString(1, login);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                System.out.println("пользователь с таким логином уже существует. введите другие данные");
                signInAsClient();
                return;
            }
        }

        SimpleClient simpleClient = applicationContext.getBean(SimpleClient.class);
        simpleClient.createNewClient(login, password, name, number, email, budget);
        addNewSimpleClient(simpleClient);
    }

    public void addNewSimpleClient(SimpleClient client) throws SQLException {
        String insertQuery = "INSERT INTO clients (login, password, name, number, email, budget, type, tickets) " +
                "values (?, ?, ?, ?, ?, ?, ?, ?)";
        String selectQuery = "SELECT id FROM clients ORDER BY id DESC LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
             Statement statement = connection.createStatement()) {
            preparedStatement.setString(1, client.getLogin());
            preparedStatement.setString(2, client.getPassword());
            preparedStatement.setString(3, client.getName());
            preparedStatement.setString(4, client.getNumber());
            preparedStatement.setString(5, client.getEmail());
            preparedStatement.setInt(6, client.getBudget());
            preparedStatement.setString(7, client.getStatus());
            preparedStatement.setInt(8, client.getTickets());
            preparedStatement.executeUpdate();

            ResultSet rs = statement.executeQuery(selectQuery);
            rs.next();
            client.setId(rs.getInt(1));
        }
    }
}
