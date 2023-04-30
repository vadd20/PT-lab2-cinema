package packages.DB;

import java.sql.SQLException;

public interface RemovableFromDb {
    void removeFromDb (int id) throws SQLException;
}
