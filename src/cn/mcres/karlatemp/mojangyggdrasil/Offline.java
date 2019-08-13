package cn.mcres.karlatemp.mojangyggdrasil;

import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.message.BufferedHeader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Offline {
    public static final List<Consumer<String>> handlers = new ArrayList<>();

    public static byte[] readAll(InputStream is) throws IOException {
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        byte[] array = new byte[2048];
        while (true) {
            int i = is.read(array);
            if (i == -1) break;
            bs.write(array, 0, i);
        }
        return bs.toByteArray();
    }

    public static void build() {
        Main.handlers.add((w, x, q, c) -> {
            boolean run = true;
            if (c.value != null) {
                run = false;
                if (c.value instanceof BuffedHttpConnection) {
                    if (((BuffedHttpConnection) c.value).getResponseCode() != 200) {
                        run = true;
                    }
                }
            }
            if (run) {
                String qr = x.getQuery();
                if (qr != null) {
                    int index = qr.indexOf("username=");
                    int end = qr.indexOf('&', index);
                    if (end == -1) {
                        end = qr.length();
                    }
                    String username = qr.substring(index + 9, end);
                    c.value = new BuffedHttpConnection(x, create(username));
                }
            } else if (Main.gson) {
                URLConnection uc = c.value;
                if (uc instanceof BuffedHttpConnection) {
                    String id = GT.getUUID(uc.getInputStream());
                    handlers.forEach(a -> a.accept(id));
                }
            }
        });
    }

    private static byte[] create(String username) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            bos.write("{\"id\":\"".getBytes());
            UUID offline = UUID.nameUUIDFromBytes(("OfflinePlayer:" + username).getBytes(StandardCharsets.UTF_8));
            bos.write(offline.toString().replaceAll("\\-", "").getBytes());
            bos.write("\",\"name\":\"".getBytes());
            byte[] buff = new byte[6];
            buff[0] = '\\';
            buff[1] = 'u';
            for (char c : username.toCharArray()) {
                buff[2] = a(c >> 12);
                buff[3] = a(c >> 8);
                buff[4] = a(c >> 4);
                //noinspection PointlessBitwiseExpression
                buff[5] = a(c >> 0);
                bos.write(buff);
            }
            bos.write("\",\"properties\":[]}".getBytes());
        } catch (IOException e) {
        }
        return bos.toByteArray();
    }

    private static byte a(int b) {
        b = b & 0xF;
        if (b < 0xA)
            return (byte) ('0' + b);
        return (byte) ('a' - 0xA + b);
    }

    public static void main(String[] w) throws IOException {
        System.out.write(create("鸡你太美,BayBay"));
    }
}
