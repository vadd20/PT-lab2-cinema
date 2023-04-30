package packages.DB;

import java.sql.SQLException;
import java.util.ArrayList;

public interface UpdatableInDb {
    void updateInDbByAdmin(ArrayList<String> data) throws SQLException;
}
