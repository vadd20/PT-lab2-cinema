package packages.objects;

import java.sql.SQLException;

public interface Updatable {
    void updateByAdmin () throws SQLException;
}
