package onl.andres.expenses.db.tables;

import onl.andres.mvcly.excp.ServiceException;
import onl.andres.expenses.db.records.RPayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;

public class TPayment {

    private static Logger logger = LoggerFactory.getLogger(TPayment.class);

    private DataSource dataSource;

    public TPayment(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long save(RPayment RPayment) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     INSERT INTO payments
                     (id_expense, payment_date, payment_value)
                     VALUES (?, ?, ?)
                     """, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setLong(1, RPayment.idExpense());
            ps.setObject(2, RPayment.paymentDate());
            ps.setLong(3, RPayment.paymentValue());
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if(rs.next()) {
                return rs.getLong(1);
            }
            throw new SQLException("Error getting generated key");
        } catch (SQLException e) {
            logger.error("Error TPayment.save({})", RPayment, e);
            throw new ServiceException.InternalServer();
        }
    }

    public int delete(Long id) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     DELETE FROM payments
                     WHERE id = ?
                     """, Statement.RETURN_GENERATED_KEYS);
        ) {
            ps.setLong(1, id);
            return ps.executeUpdate();
        } catch (SQLException e) {
            logger.error("Error TPayment.delete({})", id, e);
            throw new ServiceException.InternalServer();
        }
    }
}
