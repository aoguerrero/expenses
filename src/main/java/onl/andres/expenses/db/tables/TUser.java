package onl.andres.expenses.db.tables;

import onl.andres.expenses.db.records.RSession;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.expenses.db.records.RUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class TUser {

    private static Logger logger = LoggerFactory.getLogger(TUser.class);

    private final DataSource dataSource;

    public TUser(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void save(RUser rUser) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     INSERT INTO users 
                     (name, hash_code, display_name) 
                     VALUES (?, ?, ?)
                     """);
        ) {
            ps.setString(1, rUser.name());
            ps.setString(2, rUser.hCode());
            ps.setString(3, rUser.display_name());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error TUser.save({})", rUser,  e);
            throw new ServiceException.InternalServer();
        }
    }

    public Optional<RUser> getByNameAndEnabled(String name, boolean enabled) {
        try (Connection conn = dataSource.getConnection(); PreparedStatement ps = conn.prepareStatement("""
                SELECT name, hash_code, display_name, enabled 
                FROM users 
                WHERE name = ?
                AND enabled = ?
                """);) {
            ps.setString(1, name);
            ps.setBoolean(2, enabled);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return Optional.of(
                        new RUser(
                                rs.getString("name"),
                                rs.getString("hash_code"),
                                rs.getString("display_name"),
                                rs.getBoolean("enabled")
                        )
                );
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error TUser.getUserByNameAndEnabled({}, {})", name, enabled, e);
            throw new ServiceException.InternalServer();
        }
    }
}
