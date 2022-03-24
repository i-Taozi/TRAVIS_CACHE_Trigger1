package net.porillo.database.queries.select;

import net.porillo.database.api.SelectQuery;
import net.porillo.database.tables.OffsetTable;
import net.porillo.objects.OffsetBounty;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class OffsetSelectQuery extends SelectQuery<OffsetBounty, OffsetTable> {

    public OffsetSelectQuery(OffsetTable callback) {
        super("offsets", callback);
    }

    @Override
    public String getSQL() {
        return "SELECT * FROM offsets WHERE timeCompleted = 0";
    }

    @Override
    public List<OffsetBounty> queryDatabase(Connection connection) throws SQLException {
        List<OffsetBounty> bounties = new ArrayList<>();
        ResultSet rs = prepareStatement(connection).executeQuery(getSQL());

        while (rs.next()) {
            bounties.add(new OffsetBounty(rs));
        }

        return bounties;
    }

    @Override
    public Statement prepareStatement(Connection connection) throws SQLException {
        return connection.createStatement(); // H2 doesn't allow empty prepared statements
    }
}
