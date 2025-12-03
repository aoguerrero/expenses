package onl.andres.expenses.auth;

import io.netty.handler.codec.http.HttpHeaders;
import io.netty.handler.codec.http.HttpRequest;
import onl.andres.expenses.db.records.RSession;
import onl.andres.expenses.db.records.RUser;
import onl.andres.expenses.db.tables.TSession;
import onl.andres.expenses.db.tables.TUser;
import onl.andres.expenses.db.tables.TUserSession;
import onl.andres.mvcly.excp.ServiceException;
import onl.andres.mvcly.utl.HttpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class AuthService {

    private static Logger logger = LoggerFactory.getLogger(AuthService.class);

    private DataSource memDataSource;

    public AuthService(DataSource memDataSource) {
        this.memDataSource = memDataSource;
    }

    public String login(String username, String password) {
        TUser tUser = new TUser(memDataSource);
        RUser rUser = tUser.getByNameAndEnabled(username, true).orElseThrow(() -> new ServiceException.Unauthorized());
        if (!getMd5(password).equals(rUser.hCode())) {
            throw new ServiceException.Unauthorized();
        }
        TSession tSession = new TSession(memDataSource);
        tSession.deleteByNameUser(username);
        RSession rSession = new RSession(null, username, null);
        return tSession.save(rSession);
    }

    private String getMd5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(input.getBytes("UTF-8"));
            return String.format("%032x", new BigInteger(1, md.digest()));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            logger.error("Error processing MD5 hash", e);
            throw new ServiceException.InternalServer();
        }
    }

    public String getSessionId(HttpRequest request) {
        String cookiesStr = request.headers().get("Cookie");
        Map<String, String> cookies = HttpUtils.cookiesToMap(cookiesStr);
        String sessionId = cookies.get("sessionId");
        if (sessionId != null) {
            return sessionId;
        }
        throw new ServiceException.Unauthorized();
    }

    public void setSessionId(HttpHeaders headers, String sessionId) {
        headers.add("Set-Cookie", "sessionId=" + sessionId + "; Path=/");
    }

    public static void logout(HttpHeaders headers) {
        headers.add("Set-Cookie", "sessionId=; Max-Age=0; Path=/");
    }

    public RUser getUser(HttpRequest request) {
        String sessionId = this.getSessionId(request);
        TUserSession tUserSession = new TUserSession(memDataSource);
        return tUserSession.getUserBySessionId(sessionId).orElseThrow(() -> new ServiceException.Unauthorized());
    }
}
