package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.mvcly.ctrl.RedirectController;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.expenses.db.tables.TSession;

import javax.sql.DataSource;

public class HomeRedirectCtrl extends RedirectController {

    private AuthService authService;

    @Override
    public String execute(HttpRequest request) {
        try {
            authService.getSessionId(request);
        } catch (ServiceException.Unauthorized seu) {
            return "/login";
        }
        return super.execute(request);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
