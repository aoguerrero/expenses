package onl.andres.expenses.db.tables;

import onl.andres.mvcly.excp.ServiceException;
import onl.andres.expenses.db.records.RExpense;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import javax.swing.text.html.Option;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.Optional;

public class TExpense {

    private static Logger logger = LoggerFactory.getLogger(TExpense.class);

    private DataSource dataSource;

    public TExpense(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Optional<RExpense> getById(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     SELECT 
                       name_user, 
                       day_number, 
                       description, 
                       base_value, 
                       valid_from, 
                       valid_to 
                     FROM expenses
                     WHERE id = ?
                     """);
        ) {
            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Long baseValue = rs.getLong("base_value");
                if (rs.wasNull()) {
                    baseValue = null;
                }
                LocalDateTime validFrom = rs.getObject("valid_from", LocalDateTime.class);
                LocalDateTime validTo = rs.getObject("valid_to", LocalDateTime.class);
                return Optional.of(new RExpense(
                        id,
                        rs.getString("name_user"),
                        rs.getInt("day_number"),
                        rs.getString("description"),
                        baseValue,
                        validFrom,
                        validTo));
            }
            return Optional.empty();
        } catch (SQLException e) {
            logger.error("Error TExpense.getById({})", id, e);
            throw new ServiceException.InternalServer();
        }
    }

    public long save(RExpense RExpense) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     INSERT INTO expenses
                     (name_user, day_number, description, base_value, valid_from)
                     VALUES (?, ?, ?, ?, ?)
                     """, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setString(1, RExpense.nameUser());
            ps.setInt(2, RExpense.dayNumber());
            ps.setString(3, RExpense.description());
            ps.setLong(4, RExpense.baseValue());
            ps.setObject(5, RExpense.validFrom());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("Error getting generated key");
        } catch (SQLException e) {
            logger.error("Error TExpense.save({})", RExpense, e);
            throw new ServiceException.InternalServer();
        }
    }

    public void update(RExpense RExpense) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     UPDATE expenses
                     SET 
                       day_number = ?, 
                       description = ?, 
                       base_value = ?, 
                       valid_from = ?, 
                       valid_to = ?
                     WHERE id = ?
                     """);
        ) {
            ps.setInt(1, RExpense.dayNumber());
            ps.setString(2, RExpense.description());
            ps.setLong(3, RExpense.baseValue());
            ps.setObject(4, RExpense.validFrom());
            ps.setObject(5, RExpense.validTo());
            ps.setLong(6, RExpense.id());
            ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error TExpense.update({})", RExpense, e);
            throw new ServiceException.InternalServer();
        }
    }
}
