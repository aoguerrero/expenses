package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.expenses.db.records.RExpense;
import onl.andres.expenses.db.records.RUser;
import onl.andres.expenses.db.tables.TExpense;
import onl.andres.expenses.vm.CustomFormatter;
import onl.andres.mvcly.ctrl.BaseTemplateCtrl;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.utl.HttpUtils;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExpenseUpdateTemplateCtrl extends BaseTemplateCtrl {

    private DataSource dataSource;
    private DataSource memDataSource;

    @Override
    public Map<String, Object> getContext(HttpRequest request) {
        Map<String, String> params = HttpUtils.getUrlParams(request.uri());
        TExpense tExpense = new TExpense(dataSource);
        String expenseId = params.get("expenseId");
        if(expenseId == null || expenseId.isBlank()) {
            throw new ServiceException.InternalServer();
        }
        RExpense rExpense = tExpense.getById(Long.valueOf(expenseId)).orElseThrow(() -> new ServiceException.NotFound());

        AuthService authService = new AuthService(memDataSource);
        RUser rUser = authService.getUser(request);
        if(!rExpense.nameUser().equals(rUser.name())) {
            throw new ServiceException.Unauthorized();
        }

        Map<String, Object> context = new HashMap<>();
        context.put("validFrom", CustomFormatter.formatDate(LocalDateTime.now()));
        context.put("newExpense", false);
        context.put("rExpense", rExpense);
        context.put("CustomFormatter", CustomFormatter.class);
        return context;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMemDataSource(DataSource memDataSource) {
        this.memDataSource = memDataSource;
    }
}
