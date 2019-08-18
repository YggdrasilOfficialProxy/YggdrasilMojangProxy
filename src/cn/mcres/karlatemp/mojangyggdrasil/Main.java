package cn.mcres.karlatemp.mojangyggdrasil;

import cn.mcres.karlatemp.mojangyggdrasil.bungeecord.BCSupport;
import cn.mcres.karlatemp.mojangyggdrasil.plugin.AuthMeStartup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public class Main {
    public static final String mojangHasJoined;
    public static final List<UListener.StreamHandler> handlers = new ArrayList<>();
    public static final boolean gson;

    public static URLStreamHandler http;
    public static URLStreamHandler https;

    static {
        mojangHasJoined = new StringBuilder().append("https://sessionserver").append(".mojang.com/session").append("/minecraft/hasJoined").toString();
        boolean a = false;
        try {
            GT.getUUID(new ByteArrayInputStream("{\"id\":\"\"}".getBytes()));
            a = true;
        } catch (Throwable thr) {
        }
        gson = a;
    }

    public static void premain(String opt, Instrumentation i) {
        bootstart(i, opt);
    }

    private static void inject(final String rootx) {
        final String root = www(rootx);
        final URLStreamHandler http = NetWork.getURLStreamHandler("http");
        final URLStreamHandler https = NetWork.getURLStreamHandler("https");
        Main.http = http;
        Main.https = https;
        UListener.StreamHandler sh = (listener, url, proxy, store) -> {
            final String ef = url.toExternalForm();
            if (ef.startsWith(root)) {
                String opt = ef.substring(root.length());
                if (opt.startsWith("sessionserver/session/minecraft/hasJoined")) {
                    for (UListener.StreamHandler handler : handlers) {
                        handler.run(listener, url, proxy, store);
                    }
                }
            }
        };
        Map<String, URLStreamHandler> handlers = NetWork.getHandlers();
        handlers.put("http", new UListener(80, http, sh));
        handlers.put("https", new UListener(443, https, sh));
    }

    private static String www(String rua) {
        if (!rua.startsWith("http")) {
            rua = "https://" + rua;
        }
        if (!rua.endsWith("/")) {
            rua += "/";
        }
        return rua;
    }

    private static void bootstart(Instrumentation i, String opt) {
        Loggin.boot.info("Welcome to use MojangYggdrasil");
        Loggin.boot.info("Version: " + Main.class.getPackage().getImplementationVersion());
        Loggin.boot.info("Author: Karla" + "temp. QQ: 3279826484.");
        opt = www(opt);
        Loggin.conf.info("Yggdrasil ROOT: " + opt);
        inject(opt);
        BCSupport.inject(i, opt);
        Mojang.inject();
        if (Boolean.getBoolean("mojangyggdrasil.offline")) {
            Offline.build();
        }
        try {
            if (Boolean.getBoolean("mojangyggdrasil.authme"))
                AuthMeStartup.startup(i);
        } catch (NoClassDefFoundError nf) {
            // BungeeCord...
        } catch (Throwable thr) {
            thr.printStackTrace();
        }
    }

    public static void agentmain(String opt, Instrumentation i) {
        bootstart(i, opt);
    }

    public static void main(String[] args) throws Throwable {
        inject("https://auth2.nide8.com:233/f2894ffc98e711e9921b525400b59b6a");
        new URL("https://auth2.nide8.com:233/f2894ffc98e711e9921b525400b59b6a/sessionserver/session/minecraft/hasJoined?username=Karlatemp&serverId=UID").openConnection();
    }
}
