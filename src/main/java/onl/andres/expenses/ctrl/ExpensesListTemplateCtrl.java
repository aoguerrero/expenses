package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.expenses.db.records.RUser;
import onl.andres.mvcly.ctrl.BaseTemplateCtrl;
import onl.andres.mvcly.utl.HttpUtils;
import onl.andres.expenses.db.records.RExpensePayment;
import onl.andres.expenses.db.tables.TExpense;
import onl.andres.expenses.db.tables.TExpensePayment;
import onl.andres.expenses.db.tables.TPayment;
import onl.andres.expenses.vm.CustomFormatter;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.TextStyle;
import java.util.*;

import static java.time.temporal.TemporalAdjusters.lastDayOfMonth;

public class ExpensesListTemplateCtrl extends BaseTemplateCtrl {

    private DataSource dataSource;
    private DataSource memDataSource;


    @Override
    public Map<String, Object> getContext(HttpRequest request) {
        Map<String, String> urlParams = HttpUtils.getUrlParams(request.uri());

        LocalDateTime localDateTime = LocalDateTime.now();

        final int month = urlParams.get("month") != null ? Integer.valueOf(urlParams.get("month")) : localDateTime.getMonthValue();
        final int year = urlParams.get("year") != null ? Integer.valueOf(urlParams.get("year")) : localDateTime.getYear();

        AuthService authService = new AuthService(memDataSource);
        TExpensePayment tExpensePayment = new TExpensePayment(dataSource);
        RUser rUser = authService.getUser(request);
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.with(lastDayOfMonth());
        List<RExpensePayment> rExpensesPayments = tExpensePayment.getByNameUserAndPaymentDate(rUser.name(),
                start, end);

        Long sumPayments = rExpensesPayments.stream().map(e -> e.rPayment().paymentValue()).filter(Objects::nonNull).reduce(0l, Long::sum);
        Long sumExpenses = rExpensesPayments.stream().map(e -> e.rExpense().baseValue()).filter(Objects::nonNull).reduce(0l, Long::sum);

        Map<String, Object> context = new HashMap<>();
        context.put("user", rUser.display_name());
        context.put("CustomFormatter", CustomFormatter.class);
        context.put("expenses", rExpensesPayments);
        context.put("nextMonth", start.plusMonths(1).getMonthValue());
        context.put("previousMonth", start.minusMonths(1).getMonthValue());
        context.put("nextYear", start.plusMonths(1).getYear());
        context.put("previousYear", start.minusMonths(1).getYear());
        context.put("year", year);
        context.put("currentMonth", LocalDateTime.now().getMonthValue());
        context.put("currentYear", LocalDateTime.now().getYear());
        context.put("monthName", start.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH));
        context.put("sumPayments", sumPayments);
        context.put("sumExpenses", sumExpenses);
        return context;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMemDataSource(DataSource memDataSource) {
        this.memDataSource = memDataSource;
    }

}
