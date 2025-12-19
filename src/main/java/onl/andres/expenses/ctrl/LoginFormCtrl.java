package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.mvcly.ctrl.FormController;

import javax.sql.DataSource;
import java.util.Map;

public class LoginFormCtrl extends FormController {

    private AuthService authService;

    @Override
    public void execute(HttpRequest request, Map<String, String> formData) {
        String username = formData.get("username");
        String password = formData.get("password");
        String sessionId = authService.login(username, password);
        authService.setSessionId(getResponseHeaders(), sessionId);
    }

    public void setAuthService(AuthService authService) {
        this.authService = authService;
    }
}
