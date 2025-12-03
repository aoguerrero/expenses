package onl.andres.expenses.db.records;

public record RExpensePayment(
        RExpense rExpense,
        RPayment rPayment
) {}
