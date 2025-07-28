package jbst.foundation.domain.constants;

import com.diogonunes.jcolor.AnsiFormat;
import jbst.foundation.domain.annotations.DeletionScheduled;
import jbst.foundation.domain.base.AbstractAuthority;
import jbst.foundation.domain.base.Username;
import jbst.foundation.domain.enums.Status;
import jbst.foundation.domain.properties.configs.ServerConfigs;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.diogonunes.jcolor.Attribute.BLACK_TEXT;
import static com.diogonunes.jcolor.Attribute.BOLD;
import static jbst.foundation.domain.constants.JbstConstants.JColor.BLACK_BOLD_TEXT;

@UtilityClass
public class JbstConstants {

    @SuppressWarnings("unused")
    @DeletionScheduled(version = "1.8")
    public class BigDecimals {
        public static final BigDecimal MINUS_ONE = BigDecimal.valueOf(-1);
        public static final BigDecimal TWO = BigDecimal.valueOf(2L);
        public static final BigDecimal ONE_HUNDRED = BigDecimal.valueOf(100);
    }

    @SuppressWarnings("unused")
    @DeletionScheduled(version = "1.8")
    public class BigIntegers {
        public static final BigInteger MINUS_ONE = BigInteger.valueOf(-1);
        public static final BigInteger ONE_HUNDRED = BigInteger.valueOf(100);
    }

    @SuppressWarnings("unused")
    public class DateTimeFormatters {
        public static final DateTimeFormatter DTF10 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss.SSS");
        public static final DateTimeFormatter DTF11 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
        public static final DateTimeFormatter DTF12 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
        public static final DateTimeFormatter DTF13 = DateTimeFormatter.ofPattern("dd-MM-yyyy");

        public static final DateTimeFormatter DTF20 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
        public static final DateTimeFormatter DTF21 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        public static final DateTimeFormatter DTF22 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        public static final DateTimeFormatter DTF23 = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        public static final DateTimeFormatter DTF30 = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss.SSS");
        public static final DateTimeFormatter DTF31 = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss");
        public static final DateTimeFormatter DTF32 = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm");
        public static final DateTimeFormatter DTF33 = DateTimeFormatter.ofPattern("MM-dd-yyyy");

        public static final DateTimeFormatter DTF41 = DateTimeFormatter.ofPattern("dd MMMM yyyy");
        public static final DateTimeFormatter DTF42 = DateTimeFormatter.ofPattern("dd MMMM");

        public static final DateTimeFormatter DTF51 = DateTimeFormatter.ofPattern("HH:mm:ss");
        public static final DateTimeFormatter DTF52 = DateTimeFormatter.ofPattern("HH:mm");
    }

    public static class Domains {
        public static final String HARDCODED = "yyluchkiv.com";
    }

    @SuppressWarnings("unused")
    public class Dropdowns {
        public static final String ALL = "All";
    }

    @SuppressWarnings("unused")
    public static class JColor {
        public static final AnsiFormat BLACK_BOLD_TEXT = new AnsiFormat(BLACK_TEXT(), BOLD());
    }

    public class Files {
        public static final String PATH_DELIMITER = "/";
    }

    @SuppressWarnings("unused")
    public class Logs {
        // =================================================================================================================
        // Prefixes
        // =================================================================================================================
        public static final String PREFIX = "[jbst]";

        // =================================================================================================================
        // Standard
        // =================================================================================================================
        public static final String SERVER_OFFLINE = BLACK_BOLD_TEXT.format("{}") + " is probably offline. Exception: {}";
        public static final String USER_ACTION = "Username: " + BLACK_BOLD_TEXT.format("{}") + ". Action: " + BLACK_BOLD_TEXT.format("{}");

        // =================================================================================================================
        // Server
        // =================================================================================================================
        public static String getServerContainer(ServerConfigs serverConfigs, Status status) {
            return BLACK_BOLD_TEXT.format(serverConfigs.getName().value()) + " container/server: " + status.asANSI();
        }

        public static String getServerStartup(ServerConfigs serverConfigs, Status status) {
            return BLACK_BOLD_TEXT.format(serverConfigs.getName().value()) + " startup listener: " + status.asANSI();
        }

        public static String getServerProcess(String processId, Status status) {
            return "ProcessId: " + BLACK_BOLD_TEXT.format(processId) + ": " + status.asANSI();
        }

        // =================================================================================================================
        // User
        // =================================================================================================================
        public static String getUserProcess(Username username, String processId, Status status) {
            return "User: " + username + ". ProcessId: " + BLACK_BOLD_TEXT.format(processId) + " " + status.asANSI();
        }
    }

    public static class MemoryUnits {
        public static final long BYTES_IN_KILOBYTE = 1024L;
        public static final long BYTES_IN_MEGABYTE = 1048576L;
        public static final long BYTES_IN_GIGABYTE = 1073741824L;
    }

    public static class Numbers {

        public static class BigDecimals {
            public static final BigDecimal TWO = new BigDecimal("2");
            public static final BigDecimal HUNDRED = new BigDecimal("100");
        }

        @SuppressWarnings("unused")
        public static class BigIntegers {
            public static final BigInteger HUNDRED = new BigInteger("100");
        }
    }

    @SuppressWarnings("unused")
    public static class Properties {
        public static final String ACCOUNT = "account";
        public static final String ANY_PROBLEM = "anyProblem";
        public static final String DETAILS = "details";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String NOTIFICATION = "notification";
        public static final String NOTIFICATION_ONLY = "notificationOnly";
        public static final String PASSWORD = "password";
        public static final String STATS = "stats";
        public static final String STATUS = "status";
        public static final String STATUSES = "statuses";
        public static final String TIMESTAMP = "timestamp";
        public static final String USERNAME = "username";
        public static final String VALUE = "value";
        public static final String WEBSOCKET = "websocket";
    }

    @SuppressWarnings("unused")
    public static class SpringAuthorities {
        public static final SimpleGrantedAuthority SUPERADMIN = new SimpleGrantedAuthority(AbstractAuthority.SUPERADMIN);
        public static final SimpleGrantedAuthority ADMIN = new SimpleGrantedAuthority("admin");
        public static final SimpleGrantedAuthority USER = new SimpleGrantedAuthority("user");
    }

    @SuppressWarnings("unused")
    public static class Strings {
        public static final String ACCOUNT = "ACCOUNT";
        public static final String SUM = "SUM";
        public static final String TOTAL = "TOTAL";

        public static final String UNKNOWN = "Unknown";
        public static final String UNDEFINED = "[?]";

        public static final String OPS = "[Ops]";
    }

    public static class Swagger {
        public static final List<String> ENDPOINTS = List.of(
                "/v3/api-docs/**",
                "/swagger-ui.html",
                "/swagger-ui/**"
        );
    }

    @SuppressWarnings("unused")
    public static class Symbols {
        public static final String LINE_SEPARATOR_INTERPUNCT = "··································································································";

        public static final String COMMA = ",";
        public static final String DASH = "—";
        public static final String EMPTY = "";
        public static final String HYPHEN = "-";
        public static final String SEMICOLON = ";";
        public static final String SLASH = "/";

        public static final String TAB = "\t";
        public static final String NEWLINE = "\n";
        public static final String TWO_NEWLINE = "\n\n";

        public static final String COLLECTORS_COMMA_SPACE = ", ";
    }

    public static class ZoneIds {
        // Poland
        public static final ZoneId POLAND = ZoneId.of("Poland");

        // Ukraine, Kyiv
        // Daylight Saving Time; EEST: Eastern European Summer Time; UTC+3
        // Standard Time; EET: Eastern European Time; UTC+2
        // WARNING: https://github.com/eggert/tz/commit/e13e9c531fc48a04fb8d064acccc9f8ae68d5544
        public static final ZoneId UKRAINE = ZoneId.of("Europe/Kyiv");
    }
}
