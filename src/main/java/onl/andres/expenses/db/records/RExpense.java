package onl.andres.expenses.db.records;

import java.time.LocalDateTime;

public record RExpense(
    Long id,
    String nameUser,
    Integer dayNumber,
    String description,
    Long baseValue,
    LocalDateTime validFrom,
    LocalDateTime validTo
) {}
