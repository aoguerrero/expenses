package onl.andres.expenses.core;

import onl.andres.mvcly.core.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) throws Exception {
        MainConfiguration config = new MainConfiguration();
        new Application().start(config.getControllers());
    }
}
