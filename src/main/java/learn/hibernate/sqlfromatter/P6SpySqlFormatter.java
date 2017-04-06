package learn.hibernate.sqlfromatter;

import org.hibernate.engine.jdbc.internal.BasicFormatterImpl;
import org.hibernate.engine.jdbc.internal.Formatter;

import com.p6spy.engine.spy.appender.MessageFormattingStrategy;

/**
 * Created by Igor Dmitriev / Mikalai Alimenkou
 */
public class P6SpySqlFormatter implements MessageFormattingStrategy {

    private static final Formatter HIBERNATE_SQL_FORMATTER = new BasicFormatterImpl();

    @Override
    public String formatMessage(int connId, String now, long elapsed, String category, String prepared, String sql) {
        if (sql.isEmpty()) {
            return "";
        }
        String batch = "batch".equals(category) ? " add to batch " : "";
        return String.format("Hibernate: %s %s {elapsed: %dms}", batch, HIBERNATE_SQL_FORMATTER.format(sql), elapsed);
    }
}