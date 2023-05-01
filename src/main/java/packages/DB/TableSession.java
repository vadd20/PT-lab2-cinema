package packages.DB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import packages.MyUtils;
import packages.objects.Creatable;
import packages.objects.Session;

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
                "CREATE TABLE IF NOT EXISTS sessions (\n" +
                "   id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,\n" +
                "   hall_id integer REFERENCES halls(id) NOT NULL,\n" +
                "   film_id integer REFERENCES films(id) NOT NULL,\n" +
                "   time text NOT NULL\n" +
                ")";
        DbUtil.applyDdl(createSessionTable, dataSource);
    }

    public Session getSessionData(int id) throws SQLException {
        String selectQuery = "" +
                "   SELECT id, hall_id, film_id, time time FROM sessions WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, id);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            return rsToSession(rs);
        }
    }

    private Session rsToSession(ResultSet rs) throws SQLException {
        int id = rs.getInt(1);
        int hall_id = rs.getInt(2);
        int film_id = rs.getInt(3);
        String time = rs.getString(4);

        TableHall tableHall = applicationContext.getBean(TableHall.class);
        int rows = tableHall.getHallRowAndColumn(hall_id).get(0);
        int columns = tableHall.getHallRowAndColumn(hall_id).get(1);

        ArrayList<ArrayList<String>> places = getPlacesFromDb(id, rows, columns);
        Session session = new Session();
        session.createSessionFromDb(id, hall_id, film_id, time, places, rows, columns);
        return session;
    }

    private ArrayList<ArrayList<String>> getPlacesFromDb(int hall_id, int rows, int columns) throws SQLException {
        String selectQuery = "" +
                "   SELECT row_value, column_value, value " +
                "   FROM places WHERE session_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, hall_id);
            ResultSet resultSet = preparedStatement.executeQuery();

            ArrayList<ArrayList<String>> places = new ArrayList<>();
            for (int i = 0; i < rows; i++) {
                ArrayList<String> innerList = new ArrayList<>();
                places.add(innerList);
                for (int j = 0; j < columns; j++) {
                    innerList.add(null);
                }
            }

            while (resultSet.next()) {
                places.get(resultSet.getInt(1) - 1).set(resultSet.getInt(2) - 1, resultSet.getString(3));
            }

            return places;
        }
    }

    public void reservePlaceInDb(int id, int row, int column) throws SQLException {
        String updateQuery = "UPDATE places SET value = '*' WHERE session_id = ? AND row_value = ? AND column_value = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, row);
            preparedStatement.setInt(3, column);
            preparedStatement.executeUpdate();
        }
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


            addPlacesToTable(((Session) session).getPlaces(), ((Session) session).getRows(),
                    ((Session) session).getColumns(), ((Session) session).getId());
        }
    }


    private void addPlacesToTable(ArrayList<ArrayList<String>> places, int rows, int columns, int id) throws SQLException {
        String insertQuery = "INSERT INTO places (session_id, row_value, column_value, value) values (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < columns; ++j) {
                    preparedStatement.setInt(1, id);
                    preparedStatement.setInt(2, i + 1);
                    preparedStatement.setInt(3, j + 1);
                    preparedStatement.setString(4, places.get(i).get(j));
                    preparedStatement.addBatch();
                }
            }
            preparedStatement.executeBatch();
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
            TableHall tableHall = applicationContext.getBean(TableHall.class);
            int rows = tableHall.getHallRowAndColumn(Integer.parseInt(data.get(1))).get(0);
            int columns = tableHall.getHallRowAndColumn(Integer.parseInt(data.get(1))).get(1);

            ArrayList<ArrayList<String>> places = MyUtils.createEmptyArrayOfPlaces(rows, columns);
            addPlacesToTable(places, rows, columns, Integer.parseInt(data.get(0)));
        }
    }

    @Override
    public void removeFromDb(int id) throws SQLException {
        String deleteQuery = "DELETE FROM sessions WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(deleteQuery)) {
            TablePlaces tablePlaces = applicationContext.getBean(TablePlaces.class);
            tablePlaces.deletePlaces(id);

            preparedStatement.setInt(1, id);
            preparedStatement.executeUpdate();
        }

    }
}
