package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.mvcly.ctrl.RedirectController;
import onl.andres.mvcly.utl.HttpUtils;
import onl.andres.expenses.db.tables.TPayment;

import javax.sql.DataSource;
import java.util.Map;

public class PaymentDeleteRedirectCtrl extends RedirectController {

    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public int execute(HttpRequest request) {
        TPayment tPayment = new TPayment(dataSource);
        Map<String, String> urlParams = HttpUtils.getUrlParams(request.uri());
        Long id = Long.valueOf(urlParams.get("paymentId"));
        tPayment.delete(id);
        return 0;
    }
}
