package packages.DB;

import org.springframework.context.ApplicationContext;
import packages.MyUtils;
import packages.objects.Creatable;
import packages.objects.Hall;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;

@Component
public class TableHall implements InsertableToDb, UpdatableInDb, RemovableFromDb {
    private DataSource dataSource;
    private ApplicationContext applicationContext;


    @Autowired
    TableHall(DataSource dataSource, ApplicationContext applicationContext) throws SQLException {
        this.dataSource = dataSource;
        this.applicationContext = applicationContext;
        initDb();
    }

    private void initDb() throws SQLException {
        String createHallTable = "" +
                "CREATE TABLE IF NOT EXISTS Halls (\n" +
                "   id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL,\n" +
                "   rows integer NOT NULL,\n" +
                "   columns integer NOT NULL,\n" +
                "   price integer NOT NULL,\n" +
                "   type text NOT NULL,\n" +
                "   cinema_id integer REFERENCES Cinemas(id) NOT NULL\n" +
                ")";
        DbUtil.applyDdl(createHallTable, dataSource);
    }

    public void insertToDbByAdmin(Creatable hall) throws SQLException {
        String insertQuery = "INSERT INTO Halls (rows, columns, price, type, cinema_id) values (?, ?, ?, ?, ?)";
        String selectQuery = "SELECT id FROM Halls ORDER BY id DESC LIMIT 1";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery);
             Statement statement = connection.createStatement()) {
            preparedStatement.setInt(1, ((Hall)hall).getRows());
            preparedStatement.setInt(2, ((Hall)hall).getColumns());
            preparedStatement.setInt(3, ((Hall)hall).getPrice());
            preparedStatement.setString(4, ((Hall)hall).getType());
            preparedStatement.setInt(5, ((Hall)hall).getCinema_id());
            preparedStatement.execute();

            ResultSet rs = statement.executeQuery(selectQuery);
            rs.next();
            ((Hall)hall).setId(rs.getInt(1));

            addPlacesToTable(((Hall)hall).getPlaces(), ((Hall)hall).getRows(),
                    ((Hall)hall).getColumns(), ((Hall)hall).getId());
        }
    }

    private void addPlacesToTable (ArrayList<ArrayList<String>> places, int rows, int columns, int id) throws SQLException {
        String insertQuery = "INSERT INTO places (hall_id, row_value, column_value, value) values (?, ?, ?, ?)";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < columns; ++j) {
                    preparedStatement.setInt(1, id);
                    preparedStatement.setInt(2, i + 1);
                    preparedStatement.setInt(3, j + 1);
                    preparedStatement.setString(4, places.get(i).get(j));
                    preparedStatement.execute();
                }
            }
            //preparedStatement.executeBatch();
        }
    }

    public void getHallData (int sessionId) throws SQLException {
        String selectQuery = "" +
                "   SELECT * FROM halls WHERE id = (" +
                "   SELECT hall_id FROM sessions WHERE id = ?)";
        try (Connection connection = dataSource.getConnection();
        PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)) {
            preparedStatement.setInt(1, sessionId);
            ResultSet rs = preparedStatement.executeQuery();
            rs.next();
            rsToHall(rs);
        }
    }

    private void rsToHall (ResultSet rs) throws SQLException {
        int hall_id = rs.getInt(1);
        int rows = rs.getInt(2);
        int columns = rs.getInt(3);
        ArrayList<ArrayList<String>> places = getPlaces(hall_id, rows, columns);

        Hall hall = applicationContext.getBean(Hall.class);
        hall.createHallFromDb(hall_id, rows, columns, rs.getInt(4), rs.getString(5), rs.getInt(6), places);
    }

    private ArrayList<ArrayList<String>> getPlaces (int hall_id, int rows, int columns) throws SQLException {
        String selectQuery = "" +
                "   SELECT row_value, column_value, value " +
                "   FROM places WHERE hall_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(selectQuery)){
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

    public void reservePlaceInDb (int id, int row, int column) throws SQLException {
        String updateQuery = "UPDATE places SET value = '*' WHERE hall_id = ? AND row_value = ? AND column_value = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery)) {
            preparedStatement.setInt(1, id);
            preparedStatement.setInt(2, row);
            preparedStatement.setInt(3, column);
            preparedStatement.executeUpdate();
        }
    }

    public void show() throws SQLException {
        String selectQuery = "SELECT id, rows, columns, price, type, cinema_id FROM halls";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(selectQuery);
            while (rs.next()) {
                System.out.println(rs.getInt(1) + ". " + rs.getInt(2) + " " + rs.getInt(3) + " "
                        + rs.getInt(4) + " " + rs.getString(5) + " " + rs.getInt(6));
            }
        }
    }

    @Override
    public void updateInDbByAdmin(ArrayList<String> data) throws SQLException {
        // можем обновлять, если в зале нет сеанса. если есть, то обновляем данные
        String updateQuery = "UPDATE halls SET rows = ?, columns = ?, price = ?, type = ?, cinema_id = ?" +
                " WHERE id = ?";
        String deleteQuery = "DELETE FROM places WHERE hall_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
             PreparedStatement preparedDeleteStatement = connection.prepareStatement(deleteQuery)) {
            preparedStatement.setInt(1, Integer.parseInt(data.get(1)));
            preparedStatement.setInt(2, Integer.parseInt(data.get(2)));
            preparedStatement.setInt(3, Integer.parseInt(data.get(3)));
            preparedStatement.setString(4, data.get(4));
            preparedStatement.setInt(5, Integer.parseInt(data.get(5)));
            preparedStatement.setInt(6, Integer.parseInt(data.get(0)));
            preparedStatement.executeUpdate();

            preparedDeleteStatement.setInt(1, Integer.parseInt(data.get(0)));
            preparedDeleteStatement.executeUpdate();

            ArrayList<ArrayList<String>> places = MyUtils.createEmptyArrayOfPlaces(Integer.parseInt(data.get(1)),
                    Integer.parseInt(data.get(2)));
            addPlacesToTable(places,
                    Integer.parseInt(data.get(1)),
                    Integer.parseInt(data.get(2)),
                    Integer.parseInt(data.get(0)));
        }
    }

    @Override
    public void removeFromDb(int id) throws SQLException {
        String selectQuery = "SELECT id FROM sessions WHERE hall_id = ?";
        String deleteQuery = "DELETE FROM halls WHERE id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedSelectStatement = connection.prepareStatement(selectQuery);
             PreparedStatement preparedDeleteStatement = connection.prepareStatement(deleteQuery)) {

            preparedSelectStatement.setInt(1, id);
            ResultSet sessionRs = preparedSelectStatement.executeQuery();
            while (sessionRs.next()) {
                RemovableFromDb tableSession = applicationContext.getBean(TableSession.class);
                tableSession.removeFromDb(sessionRs.getInt(1));
            }

            TablePlaces tablePlaces = applicationContext.getBean(TablePlaces.class);
            tablePlaces.deletePlaces(id);

            preparedDeleteStatement.setInt(1, id);
            preparedDeleteStatement.executeUpdate();
        }
    }
}
