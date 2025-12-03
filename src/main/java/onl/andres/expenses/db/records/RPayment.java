package onl.andres.expenses.db.records;

import java.time.LocalDateTime;

public record RPayment(
        Long id,
        Long idExpense,
        LocalDateTime paymentDate,
        Long paymentValue
) {
}
