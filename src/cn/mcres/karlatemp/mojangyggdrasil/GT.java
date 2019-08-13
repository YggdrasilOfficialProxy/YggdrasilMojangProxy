package cn.mcres.karlatemp.mojangyggdrasil;

import com.google.gson.Gson;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class GT {
    public static final Gson g = new Gson();

    public static String getUUID(InputStream is) {
        return String.valueOf(g.fromJson(new InputStreamReader(is, StandardCharsets.UTF_8), Map.class).get("id"));
    }
}
