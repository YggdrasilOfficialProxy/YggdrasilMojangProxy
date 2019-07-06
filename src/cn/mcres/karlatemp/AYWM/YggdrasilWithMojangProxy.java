package cn.mcres.karlatemp.AYWM;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.lang.instrument.Instrumentation;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLConnection;
import moe.yushi.authlibinjector.AuthlibInjector;
import moe.yushi.authlibinjector.javaagent.AuthlibInjectorPremain;

public class YggdrasilWithMojangProxy {

    private static final Logger core = Loggin.getSL("core");
    private static final Logger conf = Loggin.getSL("conf");
    private static boolean loaded = false;
    private static boolean noatl = false;
    private static int port = 0;

    private static final int[] cc;

    static {
        int[] d = new int[128];
        for (int i = 0; i < d.length; i++) {
            d[i] = -1;
        }
        for (int i = 0; i < 10; i++) {
            d['0' + i] = i;
        }
        cc = d;
    }

    private static void shelp() {
        core.info("MojangYggdrasilProxy Static Mode Help");
        core.info(" java -jar MojangYggdrasilProxy [options] yggdrasil_server_root");
        core.info("");
        core.info("     --help: show this help");
        core.info("     --debug: start debug mode");
        core.info("     --port [port]: set proxy httpd port.");
        core.info("     --noatl: Disable ATL.");
        core.info("");
    }

    public static void main(String[] args) throws Throwable {
        if (args.length == 0) {
            shelp();
        } else {
            int i = 0;
            running:
            for (; i < args.length; i++) {
                String opt = args[i];
                switch (opt.toLowerCase()) {
                    case "--debug": {
                        conf.warning("Debug on.");
                        Loggin.getSL("httpd").setLevel(Level.ALL);
                        core.setLevel(Level.ALL);
                        conf.setLevel(Level.ALL);
                        break;
                    }
                    case "--help": {
                        shelp();
                        break;
                    }
                    case "--port": {
                        port = Integer.decode(args[++i]);
                        break;
                    }
                    case "--noatl": {
                        noatl = true;
                        conf.warning("Unuse ATL.");
                        break;
                    }
                    default: {
                        break running;
                    }
                }
            }
            StringBuilder sb = new StringBuilder();
            for (; i < args.length; i++) {
                sb.append(args[i]);
            }
            String root = sb.toString().trim();
            if (root.isEmpty()) {
                core.severe("No yggdrsail root!");
                System.exit(1);
            } else {
                core.info("API Root: " + root);
                if (!noatl) {
                    root = Utils.getATL(new URL(root), 7, conf);
                }
                HttpdServer hs = new HttpdServer(root);
                hs.setPort(port);
                if (!hs.run(false)) {
                    System.exit(2);
                } else {
                    core.info("Using proxy with");
                    String p = String.valueOf(hs.getPort());
                    core.info("http://localhost:" + p);
                    core.info("http://127.0.0.1:" + p);
                    InetAddress ia = InetAddress.getLocalHost();
                    core.info("http://" + ia.getHostAddress() + ":" + p);
                    core.info("http://" + ia.getHostName() + ":" + p);
                    core.info("http://" + ia.getCanonicalHostName() + ":" + p);
                }
            }
        }
    }

    public static void opt(String t) {
        conf.log(Level.INFO, "Options: {0}", t);
        final char[] opt = t.toCharArray();
        final int l = opt.length;
        for (int i = 0; i < l; i++) {
            char c = opt[i];
            switch (c) {
                case 'p': {
                    int p = 0;
                    for (i++; i < l; i++) {
                        c = opt[i];
                        if (c == '.') {
                            break;
                        }
                        int code = cc[c];
                        if (code == -1) {
                            throw new java.lang.IllegalArgumentException("Unknown numer char '" + c + "'(0x" + Integer.toHexString(c) + ") in parsing httpd server port.");
                        } else {
                            p = (p * 10) + code;
                        }
                    }
                    port = p;
                    break;
                }
                case 'd': {
                    conf.warning("Debug on.");
                    Loggin.getSL("httpd").setLevel(Level.ALL);
                    core.setLevel(Level.ALL);
                    conf.setLevel(Level.ALL);
                    break;
                }
                case 'a': {
                    conf.warning("Unuse ATL.");
                    noatl = true;
                    break;
                }
                case 'h': {
                    conf.info(
                            "Options: \n"
                            + "         d: Open Debug mode.\n"
                            + "         h: Show this help.\n"
                            + "         a: Unuse ATL.\n"
                            + "   p[num].: Set Httpd Server port. E.g: p25566.\n"
                            + "\n"
                            + "Example:\n"
                            + "  -cp AuthlibInjector.jar \n"
                            + "     -javaagent:MojangYggdrasilProxy.jar=/p25566.dh/http://yggdrasil.example.com\n"
                            + "     -javaagent:MojangYggdrasilProxy.jar=/hdap233/http://yggdrasil.example.com/api\n"
                            + "  -jar minecraft_server.jar\n"
                            + "\n"
                            + "AuthlibInjector download: https://github.com/yushijinhun/authlib-injector"
                    );
                    break;
                }
            }
        }
    }

    public static String opts(String wx) {
        if (wx.startsWith("/")) {
            int f = wx.indexOf('/', 1);
            String opts = wx.substring(1, f);
            opt(opts);
            return wx.substring(f + 1, wx.length());
        }
        return wx;
    }

    public static String boot(String s) {
        if (loaded) {
            return s;
        }
        conf.log(Level.INFO, "Using with yggdrasil api root: {0}", s);
        try {
            if (!noatl) {
                s = Utils.getATL(new URL(s), 7, conf);
            }
            HttpdServer hs = new HttpdServer(s);
            hs.setPort(port);
            if (hs.run()) {
                loaded = true;
                return "http://localhost:" + hs.getPort();
            }
            conf.log(Level.SEVERE, "Cannot start the httpd proxy server.");
        } catch (Throwable thr) {
            conf.log(Level.SEVERE, "Cannot start the httpd proxy server.", thr);
        }
        return s;
    }

    public static void premain(String s, Instrumentation i) {
        if (loaded) {
            core.severe("MojangYggdrasilProxy was running on this server.");
            return;
        }
        AuthlibInjectorPremain.premain(boot(opts(s)), i);
    }

    public static void agentmain(String s, Instrumentation i) {
        if (loaded) {
            core.severe("MojangYggdrasilProxy was running on this server.");
            return;
        }
        AuthlibInjectorPremain.agentmain(boot(opts(s)), i);
    }
}
