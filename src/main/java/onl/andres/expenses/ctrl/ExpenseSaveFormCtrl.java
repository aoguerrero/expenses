package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.vm.CustomFormatter;
import onl.andres.mvcly.ctrl.FormController;
import onl.andres.expenses.auth.AuthService;
import onl.andres.expenses.db.records.RExpense;
import onl.andres.expenses.db.tables.TExpense;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.Map;

public class ExpenseSaveFormCtrl extends FormController {

    private DataSource dataSource;
    private DataSource memDataSource;

    @Override
    public void execute(HttpRequest request, Map<String, String> formData) {
        AuthService authService = new AuthService(memDataSource);
        TExpense tExpense = new TExpense(dataSource);
        Long id = formData.get("id") != null && !formData.get("id").isBlank() ? Long.valueOf(formData.get("id")) : null;
        LocalDateTime validTo = formData.get("valid_to") != null && !formData.get("valid_to").isBlank() ?
                CustomFormatter.parseDate(formData.get("valid_to")) : null;
        RExpense rExpense = new RExpense(
                id,
                authService.getUser(request).name(),
                Integer.valueOf(formData.get("day_number")),
                formData.get("description"),
                Long.valueOf(formData.get("base_value")),
                CustomFormatter.parseDate(formData.get("valid_from")),
                validTo);
        if(id == null) {
            tExpense.save(rExpense);
        } else {
            tExpense.update(rExpense);
        }
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMemDataSource(DataSource memDataSource) {
        this.memDataSource = memDataSource;
    }
}
