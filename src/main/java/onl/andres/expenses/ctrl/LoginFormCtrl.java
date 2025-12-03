package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.mvcly.ctrl.FormController;

import javax.sql.DataSource;
import java.util.Map;

public class LoginFormCtrl extends FormController {

    private DataSource dataSource;
    private DataSource memDataSource;

    @Override
    public void execute(HttpRequest request, Map<String, String> formData) {
        String username = formData.get("username");
        String password = formData.get("password");
        AuthService authService = new AuthService(memDataSource);
        String sessionId = authService.login(username, password);
        authService.setSessionId(getResponseHeaders(), sessionId);
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMemDataSource(DataSource memDataSource) {
        this.memDataSource = memDataSource;
    }
}
