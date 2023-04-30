package packages.DB;

import packages.objects.Creatable;

import java.sql.SQLException;

public interface InsertableToDb {
    void insertToDbByAdmin(Creatable creatable) throws SQLException;
}
