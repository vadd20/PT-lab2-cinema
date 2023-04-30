package packages.DB.users;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.DB.DbUtil;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

@Component
public class TableAdminImpl implements TableUser {
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
                "CREATE TABLE IF NOT EXISTS admins (" +
                "   id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL," +
                "   login text NOT NULL," +
                "   password text NOT NULL," +
                "   name text NOT NULL" +
                ")";
        DbUtil.applyDdl(createClientTable, dataSource);
    }

    @Override
    public Boolean checkLoginPassword(String login, String password) throws SQLException {
        String selectQuery = "SELECT id FROM admins WHERE login = ? and password = ?";
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
        String selectQuery = "SELECT id FROM admins WHERE login = ? and password = ?";
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
        String selectQuery = "SELECT id, login, password, name FROM admins WHERE id = ?";
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


}
