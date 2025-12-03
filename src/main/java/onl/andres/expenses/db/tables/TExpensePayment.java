package onl.andres.expenses.db.tables;

import onl.andres.mvcly.excp.ServiceException;
import onl.andres.expenses.db.records.RExpense;
import onl.andres.expenses.db.records.RExpensePayment;
import onl.andres.expenses.db.records.RPayment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TExpensePayment {

    private static Logger logger = LoggerFactory.getLogger(TExpensePayment.class);

    private DataSource dataSource;

    public TExpensePayment(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public List<RExpensePayment> getByNameUserAndPaymentDate(String nameUser, LocalDateTime start, LocalDateTime end) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement ps = conn.prepareStatement("""
                     SELECT 
                       _expenses.id id_1,
                       _expenses.day_number,
                       _expenses.description,
                       _expenses.base_value,
                       _expenses.valid_from,
                       _expenses.valid_to,
                       payments.id id_2,
                       payments.payment_date,
                       payments.payment_value
                     FROM (SELECT * FROM expenses WHERE name_user = ?) AS _expenses
                     LEFT JOIN payments
                     ON _expenses.id = payments.id_expense
                     AND payments.payment_date BETWEEN ? AND ?
                     ORDER BY _expenses.day_number
                     """);
        ) {
            ps.setString(1, nameUser);
            ps.setObject(2, start);
            ps.setObject(3, end);

            ResultSet rs = ps.executeQuery();
            List<RExpensePayment> expensesPayments = new ArrayList<>();
            while (rs.next()) {
                Long paymentValue = rs.getLong("payment_value");
                if (rs.wasNull()) {
                    paymentValue = null;
                }
                RExpensePayment rExpensePayment = new RExpensePayment(
                        new RExpense(
                                rs.getLong("id_1"),
                                nameUser,
                                rs.getInt("day_number"),
                                rs.getString("description"),
                                rs.getLong("base_value"),
                                rs.getObject("valid_from", LocalDateTime.class),
                                rs.getObject("valid_to", LocalDateTime.class)),
                        new RPayment(
                                rs.getLong("id_2"),
                                rs.getLong("id_1"),
                                rs.getObject("payment_date", LocalDateTime.class),
                                paymentValue
                        )
                );
                if (end.isAfter(rExpensePayment.rExpense().validFrom()) &&
                        (rExpensePayment.rExpense().validTo() == null || end.isBefore(rExpensePayment.rExpense().validTo()))) {
                    expensesPayments.add(rExpensePayment);
                }
            }
            return expensesPayments;
        } catch (SQLException e) {
            logger.error("Error TExpensePayment.getByNameUserAndPaymentDate({})", nameUser, e);
            throw new ServiceException.InternalServer();
        }
    }
}
