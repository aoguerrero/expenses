package onl.andres.expenses.db.tables;

import onl.andres.mvcly.excp.ServiceException;
import onl.andres.expenses.db.records.RSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public class TSession {

    private static Logger logger = LoggerFactory.getLogger(TSession.class);

    private final DataSource dataSource;

    public TSession(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<RSession> getById(String id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     SELECT name_user, expiry_date 
                     FROM sessions 
                     WHERE id = ?
                     """);
        ) {
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(
                        new RSession(
                                id,
                                rs.getString("name_user"),
                                rs.getObject("expiry_date", LocalDateTime.class)
                        ));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error TSession.getById({})", id, e);
            throw new ServiceException.InternalServer();
        }
    }

    public String save(RSession rSession) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     INSERT INTO sessions 
                     (id, name_user, expiry_date) 
                     VALUES (?, ?, ?)
                     """);
        ) {
            String id = rSession.id() != null ? rSession.id() : UUID.randomUUID().toString();
            ps.setString(1, id);
            ps.setString(2, rSession.nameUser());
            ps.setObject(3, rSession.expiryDate());
            ps.executeUpdate();
            return id;
        } catch (SQLException e) {
            logger.error("Error TSession.save({})", rSession,  e);
            throw new ServiceException.InternalServer();
        }
    }

    public void deleteByNameUser(String nameUser) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     DELETE FROM sessions 
                     WHERE name_user = ?
                     """);
        ) {
            ps.setString(1, nameUser);
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error TSession.deleteByNameUser({})", nameUser, e);
            throw new ServiceException.InternalServer();
        }
    }
}

