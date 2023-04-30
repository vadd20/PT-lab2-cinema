package packages.DB;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;

@Component
public class TablePlaces {

    private DataSource dataSource;

    @Autowired
    TablePlaces(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
        initDb();
    }

    private void initDb() throws SQLException {
        String createSessionTable = "" +
                "CREATE TABLE IF NOT EXISTS places (" +
                "id integer PRIMARY KEY GENERATED ALWAYS AS IDENTITY NOT NULL," +
                "hall_id integer REFERENCES halls(id) NOT NULL," +
                "row_value integer NOT NULL," +
                "column_value integer NOT NULL," +
                "value text NOT NULL)";
        DbUtil.applyDdl(createSessionTable, dataSource);
    }

    public void showStatsAndEarnings() throws SQLException {
        String selectQuery = "SELECT places.hall_id, COUNT(*) AS count, halls.price\n" +
                "FROM places\n" +
                "INNER JOIN halls ON places.hall_id = halls.id\n" +
                "WHERE places.value = '*'\n" +
                "GROUP BY places.hall_id, halls.price;";
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {

            ResultSet rs = statement.executeQuery(selectQuery);
            int totalEarn = 0;
            while (rs.next())
            {
                System.out.println("Номер зала - " + rs.getInt(1) + ". Количество проданных билетов - " + rs.getInt(2));
                totalEarn += rs.getInt(2) * rs.getInt(3);
            }
            System.out.println("Общая выручка: " + totalEarn);
        }
    }

    public void deletePlaces(int id) throws SQLException {
        String selectPlacesQuery = "SELECT * FROM places WHERE hall_id = ?";
        String deleteQeury = "DELETE FROM places WHERE hall_id = ?";
        try (Connection connection = dataSource.getConnection();
             PreparedStatement preparedSelectPlacesStatement = connection.prepareStatement(selectPlacesQuery);
            PreparedStatement preparedDeleteStatement = connection.prepareStatement(deleteQeury)) {
            preparedSelectPlacesStatement.setInt(1, id);
            ResultSet placesRs = preparedSelectPlacesStatement.executeQuery();
            if (placesRs.next()) {
                preparedDeleteStatement.setInt(1, id);
                preparedDeleteStatement.executeUpdate();
            }
        }
    }
}