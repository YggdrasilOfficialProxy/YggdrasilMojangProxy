/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.mcres.karlatemp.AYWM;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpServer;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.sun.net.httpserver.HttpExchange;

/**
 *
 * @author 32798
 */
public class HttpdServer {

    private static final Logger lg = Loggin.getSL("httpd");
    private String bot;
    private int port;

    public void setPort(int p) {
        if (port == 0) {
            if (p < 0 || p > 0xFFFF) {
                throw new IllegalArgumentException("port out of range:" + port);
            }
            port = p;
        } else {
            throw new java.lang.IllegalArgumentException("HttpdServer was setted port.");
        }
    }

    public int getPort() {
        return port;
    }

    public HttpdServer(String s) {
        this.bot = a(s);
    }

    private String a(String x) {
        if (!x.endsWith("/")) {
            return x + '/';
        }
        return x;
    }

    private String s(String w) {
        int f = 0;
        for (; f < w.length(); f++) {
            if (w.charAt(f) != '/') {
                break;
            }
        }
        return w.substring(f);
    }

    protected static Closeable ac(HttpURLConnection co) {
        return () -> co.disconnect();
    }

    private void copyHeaders(Headers rh, URLConnection urc) {
        lg.fine("[copyHeaders] Coping headers.");
        rh.entrySet().forEach((et) -> {
            String n = et.getKey();
            if (n.equalsIgnoreCase("Host")) {
                lg.log(Level.FINE, "[copyHeaders] NoReweite Host.");
            } else {
                et.getValue().forEach((ne) -> {
                    urc.addRequestProperty(n, ne);
                    lg.log(Level.FINE, "[copyHeaders] {0} -> {1}", new Object[]{n, ne});
                });
            }
        });
    }

    private OutputStream RunCopyInputStream(byte[] buffer, InputStream is, URLConnection urc, OutputStream ww) throws IOException {
        boolean ce = true;
        while (true) {
            int leng = is.read(buffer);
            if (leng == -1) {
                break;
            }
            if (ce) {
                if (urc != null) {
                    if (!urc.getDoOutput()) {
                        urc.setDoOutput(true);
                        HttpdServer.lg.fine("[RunCopyInputStream] Set Do Output: true");
                    }
                }
                if (ww == null) {
                    ww = urc.getOutputStream();
                    HttpdServer.lg.fine("[RunCopyInputStream] Binding Output");
                }
                ce = false;
            }
            ww.write(buffer, 0, leng);
            ww.flush();
        }
        HttpdServer.lg.fine("[RunCopyInputStream] Method out.");
        return ww;
    }

    private void copyInputStream(byte[] buffer, InputStream is, URLConnection urc, OutputStream ww, boolean closeOut) throws IOException {
        lg.log(Level.FINE, "[copyInputStream] Coping datas. {0}", new Throwable().getStackTrace()[1]);
        if (is != null) {
            if (buffer == null || buffer.length == 0) {
                buffer = new byte[1024];
            }
            try {
                ww = this.RunCopyInputStream(buffer, is, urc, ww);
            } finally {
                if (closeOut) {
                    if (ww != null) {
                        lg.log(Level.FINE, "[copyInputStream] Run closing.", new Throwable());
                        ww.close();
                    }
                }
            }
        }
    }

    private void rewrite(URL nw, HttpExchange w) throws IOException {
        lg.log(Level.FINE, "[rewrite] Open Rewrite {0}", nw);
        Headers rh = w.getRequestHeaders();
        HttpURLConnection urc = (HttpURLConnection) nw.openConnection();
        try (Closeable ac = ac(urc)) {
            lg.log(Level.FINE, "[rewrite] Set HTTP Method {0} to it.", w.getRequestMethod());
            urc.setRequestMethod(w.getRequestMethod());
            copyHeaders(rh, urc);
            lg.fine("[rewrite] Running Post Data push.");
            byte[] buffer = new byte[1024];
            copyInputStream(buffer, w.getRequestBody(), urc, null, false);
            w.sendResponseHeaders(urc.getResponseCode(), 0);
            lg.log(Level.FINE, "[rewrite] Response Code: {0}, LG: {1}", new Object[]{urc.getResponseCode(), urc.getContentLengthLong()});
            if (urc.getDoInput()) {
                InputStream ls;
                if (urc.getResponseCode() == 200) {
                    ls = urc.getInputStream();
                } else {
                    ls = urc.getErrorStream();
                }
                OutputStream wr = w.getResponseBody();
                this.copyInputStream(buffer, ls, null, wr, false);
                lg.fine("[rewrite] Rewrite end.");
            }
        }
    }

    public boolean run() {
        return run(true);
    }

    public String getRoot() {
        return bot;
    }

    public void setRoot(String r) {
        bot = r;
    }

    public boolean run(boolean daemon) {
        try {
            HttpServer hs = HttpServer.create();
            int nport;
            try (ServerSocket sk = new ServerSocket(port)) {
                port = nport = sk.getLocalPort();
            }
            hs.bind(new InetSocketAddress(nport), 0);
            hs.createContext("/", (w) -> {
                URL root = new URL(bot);
                URI ui = w.getRequestURI();
                lg.log(Level.FINE, "[run] {0} {1} -> {2}: {3}", new Object[]{w.getRequestMethod(), ui.getPath(), ui, ui.getRawQuery()});
                String pt = ui.getPath();
                try {
                    switch (pt) {
                        case "/": {
                            rewrite(root, w);
                            break;
                        }
                        case "/sessionserver/session/minecraft/hasJoined": {
                            String mojang = "https://sessionserver.mojang.com/session/minecraft/hasJoined";
                            URL u = new URL(root, s(ui.toString()));
                            try {
                                lg.fine("[run] Open yggdrasil sessionserver hasjoined");
                                HttpURLConnection huc = (HttpURLConnection) u.openConnection();
                                try (Closeable ac = ac(huc)) {
                                    huc.setRequestMethod(w.getRequestMethod());
                                    this.copyHeaders(w.getRequestHeaders(), huc);
                                    int code = huc.getResponseCode();
                                    if (code == 200) {
                                        lg.fine("[run] Yggdrasil search OK.");
                                        w.sendResponseHeaders(200, 0);
                                        this.copyInputStream(null, huc.getInputStream(), null, w.getResponseBody(), true);
                                        break;
                                    }
                                }
                            } catch (IOException ioe) {
                            }
                            lg.fine("[run] Open Mojang Rewrite.");
                            rewrite(new URL(mojang + "?" + ui.getRawQuery()), w);
                            break;
                        }
                        default: {
                            rewrite(new URL(root, s(ui.toString())), w);
                            break;
                        }
                    }
                } catch (IOException ie) {
                    lg.log(Level.FINE, null, ie);
//                    w.sendResponseHeaders(500, 0);
//                    w.close();
                    throw ie;
                }

                lg.log(Level.FINE, "[run] running WE closing.");
                w.close();
            });
            lg.fine("Starting Server. Creating Thread.");
            Object lk = new Object();
            Thread th = new Thread(() -> {
                synchronized (lk) {
                    try {
                        hs.start();
                    } finally {
                        lg.fine("Notify the lock.");
                        lk.notify();
                    }
                }
            }, "Httpd Server Thread");
            th.setDaemon(daemon);
            synchronized (lk) {
                lg.fine("Start Httpd Server Thread");
                th.start();
                lg.fine("Push a lock for 20s");
                lk.wait(20 * 1000);
            }
            lg.log(Level.INFO, "Httpd proxy server run on port {0}", String.valueOf(port));
            return true;
        } catch (InterruptedException | IOException ex) {
            lg.log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
