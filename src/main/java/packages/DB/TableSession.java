package packages.DB;

import org.postgresql.util.LruCache;
import packages.objects.Creatable;
import packages.objects.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.objects.Updatable;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

@Component
public class TableSession implements InsertableToDb, UpdatableInDb, RemovableFromDb {
    private DataSource dataSource;
    private ApplicationContext applicationContext;


    @Autowired
    public TableSession(DataSource dataSource, ApplicationContext applicationContext) throws SQLException {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
        initDb();
    }

    private void initDb() throws SQLException {
        String createSessionTable = "" +
                "CREATE TABLE IF NOT EXISTS Sessions (\n" +
                "   id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,\n" +
                "   hall_id integer REFERENCES Cinemas(id) NOT NULL,\n" +
                "   film_id integer REFERENCES Films(id) NOT NULL,\n" +
                "   time text NOT NULL\n" +
                ")";
        DbUtil.applyDdl(createSessionTable, dataSource);
    }

    public void insertToDbByAdmin(Creatable session) throws SQLException {
        String insertQuery = "INSERT INTO Sessions (hall_id, film_id, time) values (?, ?, ?)";
        String selectQuery = "SELECT id FROM Sessions ORDER BY id DESC LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
             Statement statement = connection.createStatement()) {
            preparedStatement.setInt(1, ((Session) session).getHall_id());
            preparedStatement.setInt(2, ((Session) session).getFilm_id());
            preparedStatement.setString(3, ((Session) session).getTime());
            preparedStatement.execute();

            ResultSet rs = statement.executeQuery(selectQuery);
            rs.next();
            ((Session) session).setId(rs.getInt(1));
        }
    }

    public void showAvailableSessions(String status) throws SQLException {
        String selectQuery = "SELECT sessions.id, cinemas.name, films.name, sessions.time, halls.price \n" +
                "FROM sessions INNER JOIN films ON sessions.film_id = films.id\n" +
                "INNER JOIN halls ON sessions.hall_id = halls.id INNER JOIN cinemas ON cinemas.id = halls.cinema_id";

        System.out.println("выберите подходящий сеанс. введите номер");
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(selectQuery);
            if (status.equals("simple")) {
                while (rs.next()) {
                    System.out.println(rs.getInt(1) + ". " + rs.getString(2) + " ; " + rs.getString(3) + " ; "
                            + rs.getString(4) + " ; " + rs.getInt(5));
                }
            }
            if (status.equals("friend")) {
                while (rs.next()) {
                    System.out.println(rs.getInt(1) + ". " + rs.getString(2) + " ; " + rs.getString(3) + " ; "
                            + rs.getString(4) + " ; " + rs.getInt(5) * 0.9);
                }
            }
            if (status.equals("VIP")) {
                while (rs.next()) {
                    System.out.println(rs.getInt(1) + ". " + rs.getString(2) + " ; " + rs.getString(3) + " ; "
                            + rs.getString(4) + " ; " + rs.getInt(5) * 0.8);
                }
            }
        }
    }

    public void show() throws SQLException {
        String selectQuery = "SELECT id, hall_id, film_id, time FROM sessions";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(selectQuery);
            while (rs.next()) {
                System.out.println(rs.getInt(1) + ". " + rs.getString(2) + " " + rs.getInt(3) + " "
                        + rs.getString(4));
            }
        }
    }

    @Override
    public void updateInDbByAdmin(ArrayList<String> data) throws SQLException {
        String updateQuery = "UPDATE sessions SET hall_id = ?, film_id = ?, time = ?" +
                " WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, Integer.parseInt(data.get(1)));
            preparedStatement.setInt(2, Integer.parseInt(data.get(2)));
            preparedStatement.setString(3, data.get(3));
            preparedStatement.setInt(4, Integer.parseInt(data.get(0)));
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void removeFromDb(int id) throws SQLException {
        String deleteQuery = "DELETE FROM Sessions WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }
    }
}
