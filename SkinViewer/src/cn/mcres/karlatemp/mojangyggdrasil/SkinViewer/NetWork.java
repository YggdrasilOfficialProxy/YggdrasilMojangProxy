package cn.mcres.karlatemp.mojangyggdrasil.SkinViewer;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLStreamHandler;
import java.util.Map;

public class NetWork {
    private static final Class<URL> clazz = URL.class;
    private static final Method getURLStreamHandler;
    private static final Field handlers;

    static {
        Method met = null;
        for (Method def : clazz.getDeclaredMethods()) {
            if (def.getName().equalsIgnoreCase("getURLStreamHandler")) {
                if (def.getParameterCount() == 1) {
                    if (def.getParameterTypes()[0] == String.class) {
                        met = def;
                        break;
                    }
                }
            }
        }
        getURLStreamHandler = met;
        met.setAccessible(true);
        Field f = null;
        for (Field ff : clazz.getDeclaredFields()) {
            if (ff.getName().equalsIgnoreCase("handlers")) {
                f = ff;
            }
        }
        handlers = f;
        f.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    public static Map<String, URLStreamHandler> getHandlers() {
        try {
            return (Map<String, URLStreamHandler>) handlers.get(null);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public static URLStreamHandler getURLStreamHandler(String proc) {
        try {
            return (URLStreamHandler) getURLStreamHandler.invoke(null, proc);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }
}
