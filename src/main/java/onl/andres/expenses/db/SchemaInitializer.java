package onl.andres.expenses.db;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class SchemaInitializer {

    private static Logger logger = LoggerFactory.getLogger(SchemaInitializer.class);

    private SchemaInitializer() {
    }

    public static void init(DataSource dataSource, String sqlFile) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stm = conn.createStatement()) {
            stm.execute("RUNSCRIPT FROM 'classpath:/sql/" + sqlFile + "'");
        } catch (SQLException sqle) {
            logger.error("Error initializing the database", sqle);
            throw sqle;
        }
    }

}
