package onl.andres.expenses.ctrl;

import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.auth.AuthService;
import onl.andres.mvcly.ctrl.RedirectController;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.expenses.db.tables.TSession;

import javax.sql.DataSource;

public class HomeRedirectCtrl extends RedirectController {

    private DataSource dataSource;
    private DataSource memDataSource;

    @Override
    public int execute(HttpRequest request) {
        TSession TSession = new TSession(dataSource);
        AuthService authService = new AuthService(memDataSource);
        try {
            authService.getSessionId(request);
        } catch (ServiceException.Unauthorized seu) {
            return 1;
        }
        return 0;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void setMemDataSource(DataSource memDataSource) {
        this.memDataSource = memDataSource;
    }
}
