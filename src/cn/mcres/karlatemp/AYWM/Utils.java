/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cn.mcres.karlatemp.AYWM;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 32798
 */
public class Utils {

    public static String getATL(boolean atl, String v) throws IOException {
        if (atl) {
            return getATL(v);
        }
        return v;
    }

    public static String getATL(String v) throws IOException {
        return getATL(v, 5);
    }

    public static String getATL(String v, int max) throws IOException {
        return getATL(new URL(v), max, null);
    }

    public static String getATL(URL url, int max, Logger log) throws IOException {
        if (max < 0) {
            throw new java.net.ConnectException("Redirected too many times");
        }
        HttpURLConnection uc = (HttpURLConnection) url.openConnection();
        uc.connect();
        String xl;
        try (Closeable c = HttpdServer.ac(uc)) {
            xl = uc.getHeaderField("X-Authlib-Injector-API-Location");
            if (xl == null || xl.trim().isEmpty()) {
                return url.toString();
            }

        }
        URL old = url;
        url = new URL(url, xl);
        if (old.equals(url)) {
            return url.toString();
        }
        log.log(Level.INFO, "Redirected to {0}", url);
        return getATL(url, max - 1, log);
    }
}
