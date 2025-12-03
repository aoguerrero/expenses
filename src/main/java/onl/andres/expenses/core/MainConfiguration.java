package onl.andres.expenses.core;

import onl.andres.expenses.ctrl.*;
import onl.andres.expenses.db.SchemaInitializer;
import onl.andres.expenses.db.records.RUser;
import onl.andres.expenses.db.tables.TUser;
import onl.andres.mvcly.ctrl.BaseController;
import onl.andres.mvcly.ctrl.ControllerFactory;
import onl.andres.mvcly.ctrl.StaticController;
import onl.andres.mvcly.ctrl.StaticTemplateCtrl;
import onl.andres.mvcly.excp.ServiceException;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

import static onl.andres.expenses.core.AppParameters.*;

public class MainConfiguration {

    private static Logger logger = LoggerFactory.getLogger(MainConfiguration.class);

    private final Map<String, BaseController> controllers;

    public MainConfiguration() {
        try {
            logger.info("Current Directory: {}", System.getProperty("user.dir"));

            DataSource dataSource = JdbcConnectionPool.create(JDBC_CONNECTION.get(), JDBC_USER.get(), JDBC_PASSWORD.get());
            if (INIT_DB.getBoolean()) {
                SchemaInitializer.init(dataSource, "init.sql");
            }

            DataSource memDataSource = JdbcConnectionPool.create("jdbc:h2:mem:sessions", "user", "");
            SchemaInitializer.init(memDataSource, "mem.sql");
            TUser tUser = new TUser(memDataSource);
            String[] users = APP_USERS.get().split(";");
            for(String userStr : users) {
                String[] user = userStr.split(",");
                tUser.save(new RUser(user[0], user[1], user[2], true));
            }


            Map<String, byte[]> templateMap = new HashMap<>();
            Map<String, byte[]> staticMap = new HashMap<>();

            Map<String, Object> dependencies = new HashMap<>();
            dependencies.put("setDataSource", dataSource);
            dependencies.put("setMemDataSource", memDataSource);
            dependencies.put("setTemplateMap", templateMap);
            dependencies.put("setStaticMap", staticMap);
            ControllerFactory cf = new ControllerFactory(dependencies);

            controllers = new HashMap<>();

            controllers.put("/files/.*",
                    cf.getController(StaticController.class, "setPath", ""));

            controllers.put("/favicon\\\\.ico\"",
                    cf.getController(StaticController.class, "setPath", "favicon.ico"));

            /* ***** */

            controllers.put("/",
                    cf.getController(HomeRedirectCtrl.class, "setRedirectPaths", new String[]{"/expenses/list", "/login"}));

            controllers.put("/login",
                    cf.getController(StaticTemplateCtrl.class, "setPath", "login.vm"));

            controllers.put("/login/validate",
                    cf.getController(LoginFormCtrl.class, "setRedirectPath", "/expenses/list"));

            controllers.put("/logout",
                    cf.getController(LogoutRedirectCtrl.class, "setRedirectPaths", new String[]{"/login"}));

            /* ***** */

            controllers.put("/expenses/list(.*)",
                    cf.getController(ExpensesListTemplateCtrl.class, "setPath", "expenses_list.vm"));

            controllers.put("/expenses/new",
                    cf.getController(ExpenseNewTemplateCtrl.class, "setPath", "expense.vm"));

            controllers.put("/expenses/update(.*)",
                    cf.getController(ExpenseUpdateTemplateCtrl.class, "setPath", "expense.vm"));

            controllers.put("/expenses/save",
                    cf.getController(ExpenseSaveFormCtrl.class, "setRedirectPath", "/expenses/list"));

            controllers.put("/payments/new(.*)",
                    cf.getController(PaymentNewTemplateCtrl.class, "setPath", "payment.vm"));

            controllers.put("/payments/save",
                    cf.getController(PaymentSaveFormCtrl.class, "setRedirectPath", "/expenses/list"));

            controllers.put("/payments/delete(.*)",
                    cf.getController(PaymentDeleteRedirectCtrl.class, "setRedirectPaths", new String[]{"/expenses/list"}));


        } catch (Exception e) {
            logger.error("Error initializing the application", e);
            throw new ServiceException.InternalServer();
        }
    }

    public Map<String, BaseController> getControllers() {
        return controllers;
    }
}
