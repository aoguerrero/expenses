package onl.andres.expenses.db.tables;

import onl.andres.expenses.db.records.RUser;
import onl.andres.mvcly.excp.ServiceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class TUserSession {

    private static Logger logger = LoggerFactory.getLogger(TUserSession.class);

    private final DataSource dataSource;

    public TUserSession(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<RUser> getUserBySessionId(String id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     SELECT 
                       users.name,
                       users.display_name
                     FROM sessions, users
                     WHERE sessions.id = ?
                     AND sessions.name_user = users.name
                     AND users.enabled = TRUE
                     """);
        ) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(
                        new RUser(
                                rs.getString("name"),
                                null,
                                rs.getString("display_name"),
                                true
                        ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error TSession.getById({})", id, e);
            throw new ServiceException.InternalServer();
        }
    }
}

