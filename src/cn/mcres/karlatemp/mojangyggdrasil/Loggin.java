package cn.mcres.karlatemp.mojangyggdrasil;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.ConsoleHandler;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class Loggin {
    public static final String prefix = "MojangYggdrasil";
    public static final Logger boot = Logger.getLogger(prefix);
    public static final Logger conf = getLogger("conf");
    public static final Logger plugin = getLogger("plugin");
    public static final Logger bungee = getLogger("bungee");
    public static final PrintStream ps = System.out;

    private static Logger getLogger(String suffix) {
        Logger l = Logger.getLogger(prefix + "." + suffix);
        l.setParent(boot);
        return l;
    }

    static {
        boot.setUseParentHandlers(false);
        boot.addHandler(new ConsoleHandler() {
            @Override
            public void publish(LogRecord record) {
                if (isLoggable(record)) {
                    Throwable thr = record.getThrown();
                    record.setThrown(null);
                    StringBuilder bui = new StringBuilder().append(getFormatter().formatMessage(record));
                    if (thr != null) {
                        bui.append('\n');
                        StringWriter sw = new StringWriter();
                        thr.printStackTrace(new PrintWriter(sw));
                        bui.append(sw);
                    }
                    write(bui, record.getLoggerName());
                }
            }
        });
    }

    private static final String n = "\n";

    private synchronized static void write(StringBuilder bui, String lname) {
        synchronized (ps) {
            int i = 0, k;
            while ((k = bui.indexOf(n, i)) > 0) {
                ps.append('[').append(lname).append("] ").println(bui.subSequence(i, k));
                i = k + 1;
            }
            String cut = bui.substring(i);
            if (!cut.isEmpty()) {
                ps.append('[').append(lname).append("] ").println(cut);
            }
        }
    }
}
