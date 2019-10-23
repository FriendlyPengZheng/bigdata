package ad.pay;

import java.util.*;
import java.sql.*;

public class RecordIdMap
{
    HashMap<String, Integer> mapTable =
            new HashMap<String, Integer>();
    Statement statement = null;

    public RecordIdMap(Connection dbConn) throws SQLException
    {
        statement = dbConn.createStatement();
    }

    public boolean initMap(String category) throws SQLException
    {
        String sql = String.format(
                " SELECT %s_id, %s_name FROM t_dim_%s",
                category, category, category);
        ResultSet result = this.statement.executeQuery(sql);

        while (result.next()) {
            int id = result.getInt(1);
            String name = result.getString(2);
            String key = category + ":" + name;
            mapTable.put(key.trim(), id);
        }

        return true;
    }

    public int getId(String category, String name)
    {
        String key = category + ":" + name;

        Object value = mapTable.get(key.trim());

        if (value == null) {
            return -1;
        } else {
            return (Integer) value;
        }
    }
}
