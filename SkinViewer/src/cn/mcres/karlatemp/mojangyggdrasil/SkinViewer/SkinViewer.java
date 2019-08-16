package cn.mcres.karlatemp.mojangyggdrasil.SkinViewer;

import com.google.gson.Gson;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftProfileTexture;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import javassist.ClassPool;
import javassist.bytecode.ClassFile;
import javassist.bytecode.CodeAttribute;
import javassist.bytecode.ExceptionTable;
import javassist.bytecode.MethodInfo;
import org.bukkit.metadata.MetadataValue;

import java.io.*;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.lang.reflect.Method;
import java.net.Proxy;
import java.security.ProtectionDomain;
import java.security.PublicKey;
import java.util.*;

public class SkinViewer implements ClassFileTransformer {
    public static boolean req(String paramString) {
        return true;
    }

    public static byte[] run(ClassFile cf, DataInputStream dis, String function) throws IOException {
        if (cf == null) cf = new ClassFile(dis);
        MethodInfo met = cf.getMethod(function);
        final CodeAttribute caw = met.getCodeAttribute();
        CodeAttribute ca = new CodeAttribute(met.getConstPool(), caw.getMaxStack(), caw.getMaxLocals(), new byte[]{4, -84}, new ExceptionTable(met.getConstPool()));
        met.setCodeAttribute(ca);
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        DataOutputStream out = new DataOutputStream(bo);
        cf.write(out);
        return bo.toByteArray();
    }

    public static void main(String[] args) throws Throwable {
        ClassFile cf = new ClassFile(new DataInputStream(SkinViewer.class.getResourceAsStream("SkinViewer.class")));
        System.out.println(cf.getName());
        MethodInfo func = cf.getMethod("req");
        final byte[] code = func.getCodeAttribute().getCode();
        System.out.println(Arrays.toString(code));
        func.getConstPool().print();
        Thread.sleep(1000L);

        byte[] data = run(null,
                new DataInputStream(SkinViewer.class.getResourceAsStream("/com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService.class")),
                "isWhitelistedDomain");
        ClassLoader loader = ClassLoader.getSystemClassLoader();
        final Method defineClass = ClassLoader.class.getDeclaredMethod("defineClass", String.class, byte[].class, int.class, int.class);
        defineClass.setAccessible(true);
        defineClass.invoke(loader, null, data, 0, data.length);
        data = run(null,
                new DataInputStream(SkinViewer.class.getResourceAsStream("/com/mojang/authlib/properties/Property.class")),
                "isSignatureValid"
        );
        defineClass.invoke(loader, null, data, 0, data.length);

        YggdrasilAuthenticationService auth = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        final MinecraftSessionService sessionService = auth.createMinecraftSessionService();
        UUID uid = UUID.randomUUID();
        GameProfile profile = new GameProfile(uid, "Karlatemp");
        String a = "{\"timestamp\":" + System.currentTimeMillis()
                + ",\"profileId\":\"" + uid.toString().replaceAll("\\-", "")
                + "\",\"profileName\":\"Karlatemp\",\"textures\":{\"SKIN\":{\"url\":\"" +
                "https://skin.nide8.com:233/skin64x64/a5/ea/a5ea17b30ccab0da83102fca7ed29cdbed5ec1422f6831054f809f77b8d97efb.png" +
                "\"}}}";
        Property tx = new Property("textures", Base64.getEncoder().encodeToString(a.getBytes()), "NOPE");
        profile.getProperties().put("textures", tx);
        final Map<MinecraftProfileTexture.Type, MinecraftProfileTexture> textures = sessionService.getTextures(profile, true);
        System.out.println(textures);
    }

    public static void premain(String w, Instrumentation is) {
        is.addTransformer(new SkinViewer(), true);
        Class[] loaded = is.getAllLoadedClasses();
        try {
            for (Class c : loaded) {
                switch (c.getName()) {
                    case "com.mojang.authlib.properties.Property":
                    case "com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService": {
                        is.retransformClasses(c);
                    }
                }
            }
        } catch (UnmodifiableClassException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        ClassFile cf = null;
        if (className == null) {
            try {
                cf = new ClassFile(new DataInputStream(new ByteArrayInputStream(classfileBuffer)));
                className = cf.getName().replaceAll("\\.", "/");
            } catch (IOException e) {
                return classfileBuffer;
            }
        }
        String name = null;
        switch (className) {
            case "com/mojang/authlib/yggdrasil/YggdrasilMinecraftSessionService": {
                name = "isWhitelistedDomain";
                break;
            }
            case "com/mojang/authlib/properties/Property": {
                name = "isSignatureValid";
                break;
            }
        }
        if (name != null) {
            DataInputStream dis = null;
            if (cf == null) {
                dis = new DataInputStream(new ByteArrayInputStream(classfileBuffer));
            }
            try {
                return run(cf, dis, name);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }
}
