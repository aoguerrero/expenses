package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.ctrl.BaseTemplateCtrl;
import onl.andres.mvcly.utl.HttpUtils;
import onl.andres.expenses.vm.CustomFormatter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class PaymentNewTemplateCtrl extends BaseTemplateCtrl {

    @Override
    public Map<String, Object> getContext(HttpRequest request) {
        LocalDateTime localDateTime = LocalDateTime.now();
        Map<String, String> urlParams = HttpUtils.getUrlParams(request.uri());
        Map<String, Object> context = new HashMap<>();
        context.put("expenseId", urlParams.get("expenseId"));
        context.put("expenseDescription", urlParams.get("expenseDescription"));
        context.put("paymentDate", CustomFormatter.formatDate(localDateTime));
        return context;
    }
}
