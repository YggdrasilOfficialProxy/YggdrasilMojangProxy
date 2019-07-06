package cn.mcres.karlatemp.AYWM;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.logging.Filter;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Loggin extends Logger {

    private static final Logger main = Logger.getLogger("MYP");

    private static class LogginHandler extends Handler implements Consumer<PrintStream> {

        private String acc;
        private Level lv;
        protected static int max;

        @Override
        public void accept(PrintStream t) {
            String lev = String.valueOf(lv);
            int emp = 1;
            int lg = acc.length() + 1 + lev.length();
            if (lg > max) {
                max = lg;
            } else if (lg < max) {
                emp = max - lg + 1;
            }
            t.append('[').append(acc);
            while (emp-- > 0) {
                t.append(' ');
            }
            t.append(lev).append("] ");
        }
        final PR pr = new PR(System.out);
        final SimpleFormatter sf = new SimpleFormatter();

        {
            setLevel(Level.ALL);
            pr.prefix = this;
        }

        @Override
        @SuppressWarnings("NestedSynchronizedStatement")
        public void publish(LogRecord record) {
            if (this.isLoggable(record)) {
                synchronized (this) {
                    synchronized (pr) {
                        synchronized (pr.w) {
                            synchronized (record) {
                                Throwable thr = record.getThrown();
                                record.setThrown(null);
                                acc = record.getLoggerName();
                                lv = record.getLevel();
                                String formated = sf.formatMessage(record);
                                pr.pall(formated);
                                if (thr != null) {
                                    thr.printStackTrace(pr);
                                }
                            }
                        }
                    }
                }
            }
        }

        @Override
        public void flush() {
        }

        @Override
        public void close() throws SecurityException {
        }
    }

    static {
        main.setUseParentHandlers(false);
        main.addHandler(new LogginHandler());
    }
    private static final Map<String, Loggin> loggers = new HashMap<>();

    static Logger getSL(String name) {
        name = name.toLowerCase();
        Loggin l = loggers.get(name);
        if (l != null) {
            return l;
        }
        l = new Loggin(name);
        loggers.put(name, l);
        return l;
    }

    private Loggin(String sname) {
        super(sname = main.getName() + '.' + sname, null);
        LogginHandler.max = Math.max(LogginHandler.max, sname.length() + 8);
        super.setParent(main);

    }

    @Override
    public void addHandler(Handler handler) throws SecurityException {
        throw new SecurityException();
    }

    @Override
    public boolean getUseParentHandlers() {
        return true;
    }

    @Override
    public void setFilter(Filter newFilter) throws SecurityException {
        throw new SecurityException();
    }

    @Override
    public void setParent(Logger parent) {
        throw new SecurityException();
    }

}
