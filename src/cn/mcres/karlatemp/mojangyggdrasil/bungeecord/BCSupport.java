package cn.mcres.karlatemp.mojangyggdrasil.bungeecord;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.net.*;
import java.security.ProtectionDomain;
import java.util.Arrays;
import java.util.logging.Level;

import cn.mcres.karlatemp.mojangyggdrasil.Loggin;
import com.google.common.cache.Cache;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import static java.nio.charset.StandardCharsets.UTF_8;

public class BCSupport implements ClassFileTransformer, HttpHandler {
    private static final String root = "https://sessionserver.mojang.com/session/minecraft/hasJoined?username=";

    public static int indexOf(byte[] from, byte[] search, int off) {
        if (from.length < search.length) {
            return -1;
        }
        final int sleng = search.length;
        final int end = from.length - search.length;
        dox:
        for (int i = off; i < end; i++) {
            for (int k = 0; k < sleng; k++) {
                if (from[i + k] != search[k]) {
                    continue dox;
                }
            }
            return i;
        }
        return -1;
    }

    public static byte[] replace(byte[] codes, String contain, String rep) {
        byte[] utf8 = toContant(contain);
        int ind = indexOf(codes, utf8, 0);
        if (ind != -1) {
            int ed = utf8.length;
            ByteArrayOutputStream buff = new ByteArrayOutputStream(codes.length);
            buff.write(codes, 0, ind);
            byte[] repx = toContant(rep);
            buff.write(repx, 0, repx.length);
            buff.write(codes, ind + ed, codes.length - ind - ed);
            return buff.toByteArray();
        }
        return codes;
    }

    public static byte[] toContant(String contain) {
        byte[] got = contain.getBytes(UTF_8);
        byte[] nw = new byte[got.length + 2];
        int leng = got.length;
        nw[0] = (byte) ((leng >>> Byte.SIZE) & 0xFF);
        nw[1] = (byte) (leng & 0xFF);
        System.arraycopy(got, 0, nw, 2, leng);
        return nw;
    }

    public BCSupport() {

    }

    public static void inject(Instrumentation i, String opt) {
        BCSupport bc = new BCSupport();
        bc.port = Integer.getInteger("mojangyggdrasil.bungee.port", 0);
        new Thread(() -> {
            // Wait Authlib-injector
            i.addTransformer(bc);
        }).start();
    }

    private int port;
    private boolean rooted = false;

    @Override
    public byte[] transform(ClassLoader loader, String className,
                            Class<?> classBeingRedefined, ProtectionDomain protectionDomain,
                            byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className != null) {
            if (className.startsWith("net/md_5/bungee/")) {
                if (!rooted) {
                    Loggin.bungee.info("Yggdrasil root: " + Waitable.bungeePrefix);
                    rooted = true;
                }
            }
            if (className.equals("net/md_5/bungee/connection/InitialHandler")) {
                Loggin.bungee.info("Check up BungeeCord mode..");
                startup();
                byte[] replaced = replace(classfileBuffer, root, "http://127.0.0.1:" + port + "/?username=");
                if (Arrays.equals(replaced, classfileBuffer)) {
                    Loggin.bungee.severe("Error: Cannot transform BungeeCord!");
                }
                return replaced;
            }
        }
        return classfileBuffer;
    }

    private boolean booted = false;

    private void startup() {
        if (booted) return;
        booted = true;
        try {
            HttpServer server = HttpServer.create();
            if (port == 0) {
                ServerSocket ss = new ServerSocket(port);
                port = ss.getLocalPort();
                try {
                    ss.close();
                } catch (IOException e) {
                }
            }
            server.bind(new InetSocketAddress(port), 0);
            server.createContext("/", this);
            Loggin.bungee.info("Fake server startup with: " + port);
            server.start();
        } catch (IOException ioe) {
            Loggin.bungee.log(Level.SEVERE, ioe.getMessage(), ioe);
        }
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        final URI uri = httpExchange.getRequestURI();
        String bungeePrefix = Waitable.bungeePrefix;
        final URL url = new URL(bungeePrefix.substring(0, bungeePrefix.length() - 9) + uri.getQuery());
        final URLConnection uc = url.openConnection(Proxy.NO_PROXY);
        final HttpURLConnection http = (HttpURLConnection) uc;
        int i = http.getResponseCode();
        httpExchange.sendResponseHeaders(i, 0);
        InputStream io;
        if (i == 200) io = http.getInputStream();
        else io = http.getErrorStream();
        byte[] buff = new byte[1024];
        OutputStream out = httpExchange.getResponseBody();
        while (true) {
            int len = io.read(buff);
            if (len == -1) break;
            out.write(buff, 0, len);
        }
        httpExchange.close();
    }
}
