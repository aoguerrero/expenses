package onl.andres.expenses.db.records;

public record RUser(
        String name,
        String hCode,
        String display_name,
        boolean enabled
) {
}
