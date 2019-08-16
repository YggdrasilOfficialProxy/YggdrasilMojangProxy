package cn.mcres.karlatemp.mojangyggdrasil;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

public class Mojang {
    public static void inject() {
        Main.handlers.add((listener, url, proxy, store) -> {
            int respone = 0;
            try {
                URLStreamHandler handler;
                switch (url.getProtocol().toLowerCase()) {
                    case "http": {
                        handler = Main.http;
                        break;
                    }
                    case "https": {
                        handler = Main.https;
                        break;
                    }
                    default: {
                        throw new IOException("Sorry, but we cannot run with protocol: " + url.getProtocol() + ", was to errored?");
                    }
                }
                URL x = new URL(null, url.toExternalForm(), handler);
                URLConnection connect;
                if (proxy == null) connect = x.openConnection();
                else connect = x.openConnection(proxy);
                HttpURLConnection huc = (HttpURLConnection) connect;
                if ((respone = huc.getResponseCode()) == 200) {
                    final byte[] got = Offline.readAll(huc.getInputStream());
                    store.value = new BuffedHttpConnection(url, got);
                    return;
                }
                huc.disconnect();
            } catch (IOException ioe) {
            }
            try {
                URL mojang = new URL(null, Main.mojangHasJoined + "?" + url.getQuery(), Main.https);
                URLConnection connect;
                if (proxy == null) connect = mojang.openConnection();
                else connect = mojang.openConnection(proxy);
                HttpURLConnection huc = (HttpURLConnection) connect;
                if ((respone = huc.getResponseCode()) == 200) {
                    final byte[] got = Offline.readAll(huc.getInputStream());
                    store.value = new BuffedHttpConnection(mojang, got);
                }
                huc.disconnect();
            } catch (IOException ioe) {
            }
            if (store.value == null) {
                store.value = new BuffedHttpConnection(url, new byte[0], respone);
            }
        });
    }
}
