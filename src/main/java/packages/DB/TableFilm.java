package packages.DB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.objects.Creatable;
import packages.objects.Film;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

@Component
public class TableFilm implements InsertableToDb, UpdatableInDb, RemovableFromDb {
    private DataSource dataSource;
    private ApplicationContext applicationContext;

    @Autowired
    TableFilm(DataSource dataSource, ApplicationContext applicationContext) throws SQLException {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
        initDb();
    }

    private void initDb() throws SQLException {
        String createFilmTable = "" +
                "CREATE TABLE IF NOT EXISTS Films (\n" +
                "   id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,\n" +
                "   name text NOT NULL,\n" +
                "   year integer NOT NULL,\n" +
                "   genre text NOT NULL,\n" +
                "   time integer NOT NULL,\n" +
                "   format text NOT NULL\n" +
                ")";
        DbUtil.applyDdl(createFilmTable, dataSource);
    }

    @Override
    public void insertToDbByAdmin(Creatable film) throws SQLException {
        String insertQuery = "INSERT INTO Films (name, year, genre, time, format) values (?, ?, ?, ?, ?)";
        String selectQuery = "SELECT id FROM Films ORDER BY id DESC LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
             Statement statement = connection.createStatement()) {
            preparedStatement.setString(1, ((Film) film).getName());
            preparedStatement.setInt(2, ((Film) film).getYear());
            preparedStatement.setString(3, ((Film) film).getGenre());
            preparedStatement.setInt(4, ((Film) film).getTime());
            preparedStatement.setString(5, ((Film) film).getFormat());
            preparedStatement.execute();

            ResultSet rs = statement.executeQuery(selectQuery);
            rs.next();
            ((Film) film).setId(rs.getInt(1));
        }
    }


    public void show() throws SQLException {
        String selectQuery = "SELECT id, name, year, genre, time, format FROM Films";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(selectQuery);
            while (rs.next()) {
                System.out.println(rs.getInt(1) + ". " + rs.getString(2) + " " + rs.getInt(3) + " "
                        + rs.getString(4) + " " + rs.getInt(5) + " " + rs.getString(6));
            }
        }
    }

    @Override
    public void updateInDbByAdmin(ArrayList<String> data) throws SQLException {
        String updateQuery = "UPDATE films SET name = ?, year = ?, genre = ?, time = ?, format = ?" +
                " WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setString(1, data.get(1));
            preparedStatement.setInt(2, Integer.parseInt(data.get(2)));
            preparedStatement.setString(3, data.get(3));
            preparedStatement.setInt(4, Integer.parseInt(data.get(4)));
            preparedStatement.setString(5, data.get(5));
            preparedStatement.setInt(6, Integer.parseInt(data.get(0)));
            preparedStatement.executeUpdate();
        }
    }

    @Override
    public void removeFromDb(int id) throws SQLException {
        String selectQuery = "SELECT id FROM sessions WHERE film_id = ?";
        String deleteQuery = "DELETE FROM Films WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedSelectStatement = connection.prepareStatement(selectQuery);
             PreparedStatement preparedDeleteStatement = connection.prepareStatement(deleteQuery)) {
            preparedSelectStatement.setInt(1, id);
            ResultSet rs = preparedSelectStatement.executeQuery();
            while (rs.next()) {
                RemovableFromDb tableSession = applicationContext.getBean(TableSession.class);
                tableSession.removeFromDb(rs.getInt(1));
            }
            preparedDeleteStatement.setInt(1, id);
            preparedDeleteStatement.executeUpdate();
        }
    }
}
