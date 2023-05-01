package packages.objects;

import java.sql.SQLException;

public interface Creatable {
    void createByAdmin () throws SQLException;
}
