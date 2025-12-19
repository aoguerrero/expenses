package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.expenses.db.records.RPayment;
import onl.andres.expenses.db.tables.TPayment;
import onl.andres.expenses.vm.CustomFormatter;
import onl.andres.mvcly.ctrl.FormController;

import javax.sql.DataSource;
import java.util.Map;

public class PaymentSaveFormCtrl extends FormController {

    private DataSource dataSource;
    private AuthService authService;

    @Override
    public void execute(HttpRequest request, Map<String, String> formData) {
        authService.getUser(request);
        TPayment tPayment = new TPayment(dataSource);
        RPayment rPayment = new RPayment(
                null,
                Long.valueOf(formData.get("expense_id")),
                CustomFormatter.parseDate(formData.get("payment_date")),
                Long.valueOf(formData.get("payment_value")));
        tPayment.save(rPayment);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
