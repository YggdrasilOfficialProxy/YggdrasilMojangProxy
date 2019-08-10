package cn.mcres.karlatemp.mojangyggdrasil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.instrument.Instrumentation;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;
import java.util.logging.Level;

public class Main {
    public static final String mojangHasJoined;

    static {
        mojangHasJoined = new StringBuilder().append("https://sessionserver").append(".mojang.com/session").append("/minecraft/hasJoined").toString();
    }

    public static void premain(String opt, Instrumentation i) {
        bootstart(i, opt);
    }

    private static void inject(final String rootx) {
        final String root = www(rootx);
        final URLStreamHandler http = NetWork.getURLStreamHandler("http");
        final URLStreamHandler https = NetWork.getURLStreamHandler("https");
        UListener.StreamHandler sh = (listener, url, proxy, store) -> {
            final String ef = url.toExternalForm();
            if (ef.startsWith(root)) {
                String opt = ef.substring(root.length());
                if (opt.startsWith("sessionserver/session/minecraft/hasJoined")) {
                    try {
                        URL mojang = new URL(null, mojangHasJoined + "?" + url.getQuery(), https);
                        URLConnection connect;
                        if (proxy == null) connect = mojang.openConnection();
                        else connect = mojang.openConnection(proxy);
                        HttpURLConnection huc = (HttpURLConnection) connect;
                        if (huc.getResponseCode() == 200) {
                            ByteArrayOutputStream buff = new ByteArrayOutputStream();
                            byte[] buffer = new byte[1024];
                            InputStream is = huc.getInputStream();
                            while (true) {
                                int size = is.read(buffer);
                                if (size == -1) break;
                                buff.write(buffer, 0, size);
                            }
                            final byte[] got = buff.toByteArray();
                            store.value = new HttpURLConnection(mojang) {
                                @Override
                                public int getResponseCode() throws IOException {
                                    return 200;
                                }

                                @Override
                                public InputStream getInputStream() throws IOException {
                                    return new ByteArrayInputStream(got);
                                }

                                @Override
                                public void disconnect() {

                                }

                                @Override
                                public boolean usingProxy() {
                                    return false;
                                }

                                @Override
                                public void connect() throws IOException {
                                    connected = true;
                                }
                            };
                        }
                        huc.disconnect();
                    } catch (IOException ioe) {

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
        opt = www(opt);
        Loggin.conf.info("Yggdrasil ROOT: " + opt);
        inject(opt);
    }

    public static void agentmain(String opt, Instrumentation i) {
        bootstart(i, opt);
    }

    public static void main(String[] args) throws Throwable {
        inject("https://auth2.nide8.com:233/f2894ffc98e711e9921b525400b59b6a");
        new URL("https://auth2.nide8.com:233/f2894ffc98e711e9921b525400b59b6a/sessionserver/session/minecraft/hasJoined?username=Karlatemp&serverId=UID").openConnection();
    }
}
