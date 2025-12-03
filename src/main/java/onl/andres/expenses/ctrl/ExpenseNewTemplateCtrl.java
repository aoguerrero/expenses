package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.db.records.RExpense;
import onl.andres.expenses.vm.CustomFormatter;
import onl.andres.mvcly.ctrl.BaseTemplateCtrl;
import onl.andres.mvcly.utl.HttpUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class ExpenseNewTemplateCtrl extends BaseTemplateCtrl {

    @Override
    public Map<String, Object> getContext(HttpRequest request) {
        Map<String, Object> context = new HashMap<>();
        RExpense rExpense = new RExpense(
                null,
                null,
                0,
                "",
                0l,
                LocalDateTime.now(),
                null);
        context.put("rExpense", rExpense);
        context.put("newExpense", true);
        context.put("CustomFormatter", CustomFormatter.class);
        return context;
    }
}
