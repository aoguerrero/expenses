package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.mvcly.ctrl.RedirectController;

public class LogoutRedirectCtrl extends RedirectController {

    @Override
    public String execute(HttpRequest request) {
        AuthService.logout(getResponseHeaders());
        return super.execute(request);
    }
}
