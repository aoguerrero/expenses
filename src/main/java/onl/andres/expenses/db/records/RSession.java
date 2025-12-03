package onl.andres.expenses.db.records;

import java.time.LocalDateTime;

public record RSession(
        String id,
        String nameUser,
        LocalDateTime expiryDate
) {
}
