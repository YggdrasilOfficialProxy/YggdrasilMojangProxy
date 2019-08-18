package cn.mcres.karlatemp.mojangyggdrasil.SkinViewer;

import java.io.IOException;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class UListener extends URLStreamHandler {
    private final int defport;
    private final URLStreamHandler parent;
    private static final Method a, b;
    private final StreamHandler h;

    public static class Store<T> {
        T value;
    }

    public interface StreamHandler {
        void run(UListener listener, URL u, Proxy p, Store<URLConnection> uc) throws IOException;
    }

    static {
        Method c = null, d = null;
        try {
            c = URLStreamHandler.class.getDeclaredMethod("openConnection", URL.class);
            d = URLStreamHandler.class.getDeclaredMethod("openConnection", URL.class, Proxy.class);
        } catch (NoSuchMethodException e) {
        }
        a = c;
        b = d;
        AccessibleObject.setAccessible(new AccessibleObject[]{c, d}, true);
    }

    private static URLConnection open(URLStreamHandler handler, URL u, Proxy p) throws IOException {
        try {
            if (p == null) {
                return (URLConnection) a.invoke(handler, u);
            } else {
                return (URLConnection) b.invoke(handler, u, p);
            }
        } catch (IllegalAccessException e) {
            throw new IOException(e);
        } catch (InvocationTargetException e) {
            Throwable thr = e.getTargetException();
            if (thr instanceof IOException) throw (IOException) thr;
            throw new IOException(thr);
        }
    }

    public UListener(int defport, URLStreamHandler parent, StreamHandler sh) {
        this.defport = defport;
        this.parent = parent;
        h = sh;
    }

    @Override
    protected int getDefaultPort() {
        return defport;
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        Store<URLConnection> connect = new Store<>();
        h.run(this, u, p, connect);
        if (connect.value != null) return connect.value;
        return open(parent, u, p);
    }

    @Override
    protected URLConnection openConnection(URL u) throws IOException {
        Store<URLConnection> connect = new Store<>();
        h.run(this, u, null, connect);
        if (connect.value != null) return connect.value;
        return open(parent, u, null);
    }
}
