package packages.DB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.objects.Cinema;
import packages.objects.Creatable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

@Component
public class TableCinema implements InsertableToDb, UpdatableInDb, RemovableFromDb {
    private DataSource dataSource;
    private ApplicationContext applicationContext;

    @Autowired
    public TableCinema(DataSource dataSource, ApplicationContext applicationContext) throws SQLException {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
        initDb();
    }

    private void initDb() throws SQLException {
        String createCinemaTable = "" +
                "CREATE TABLE IF NOT EXISTS Cinemas (\n" +
                "   id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,\n" +
                "   name text NOT NULL,\n" +
                "   address text NOT NULL,\n" +
                "   \"number of free halls\" integer NOT NULL\n" +
                ")";
        DbUtil.applyDdl(createCinemaTable, dataSource);
    }

    @Override
    public void insertToDbByAdmin(Creatable cinema) throws SQLException {
        String insertQuery = "INSERT INTO cinemas (name, address, \"number of free halls\") values (?, ?, ?)";
        String selectQuery = "SELECT id FROM cinemas ORDER BY id DESC LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedUpdateStatement = connection.prepareStatement(insertQuery);
             Statement statement = connection.createStatement()) {
            preparedUpdateStatement.setString(1, ((Cinema) cinema).getName());
            preparedUpdateStatement.setString(2, ((Cinema) cinema).getAddress());
            preparedUpdateStatement.setInt(3, ((Cinema) cinema).getNumberOfFreeHalls());
            preparedUpdateStatement.execute();

            ResultSet rs = statement.executeQuery(selectQuery);
            rs.next();
            ((Cinema) cinema).setId(rs.getInt(1));
        }
    }


    public Boolean checkAvailableHalls(int id) throws SQLException {
        String selectQuery = "SELECT \"number of free halls\" FROM cinemas WHERE id = ?";
        String updateQuery = "UPDATE cinemas SET \"number of free halls\" = GREATEST(\"number of free halls\" - 1, 0)" +
                " WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery);
             PreparedStatement preparedUpdateStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            if (rs.next()) {
                if (rs.getInt(1) >= 0) {
                    preparedUpdateStatement.setInt(1, id);
                    preparedUpdateStatement.executeUpdate();
                    return true;
                }
            }
            return false;
        }
    }


    public void show() throws SQLException {
        String selectQuery = "SELECT id, name, address, \"number of free halls\" FROM cinemas";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            ResultSet rs = statement.executeQuery(selectQuery);
            while (rs.next()) {
                System.out.println(rs.getInt(1) + ". " + rs.getString(2) + " " + rs.getString(3) + " "
                        + rs.getInt(4));
            }
        }
    }

    @Override
    public void updateInDbByAdmin(ArrayList<String> data) throws SQLException {
        String updateQuery = "UPDATE cinemas SET name = ?, address = ?, \"number of free halls\" = ?" +
                " WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedUpdateStatement = connection.prepareStatement(updateQuery)) {
            preparedUpdateStatement.setString(1, data.get(1));
            preparedUpdateStatement.setString(2, data.get(2));
            preparedUpdateStatement.setInt(3, Integer.parseInt(data.get(3)));
            preparedUpdateStatement.setInt(4, Integer.parseInt(data.get(0)));
            preparedUpdateStatement.executeUpdate();
        }
    }

    @Override
    public void removeFromDb(int id) throws SQLException {
        String selectQuery = "SELECT id FROM halls WHERE cinema_id = ?";
        String deleteQuery = "DELETE FROM cinemas WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedSelectStatement = connection.prepareStatement(selectQuery);
             PreparedStatement preparedDeleteStatement = connection.prepareStatement(deleteQuery)) {
            preparedSelectStatement.setInt(1, id);
            ResultSet rs = preparedSelectStatement.executeQuery();
            while (rs.next()) {
                RemovableFromDb tableHall = applicationContext.getBean(TableHall.class);
                tableHall.removeFromDb(rs.getInt(1));
            }
            preparedDeleteStatement.setInt(1, id);
            preparedDeleteStatement.executeUpdate();
        }
    }
}
