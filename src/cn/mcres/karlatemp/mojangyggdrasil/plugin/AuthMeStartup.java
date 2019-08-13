package cn.mcres.karlatemp.mojangyggdrasil.plugin;

import cn.mcres.karlatemp.mojangyggdrasil.Loggin;
import cn.mcres.karlatemp.mojangyggdrasil.Offline;
import com.avaje.ebean.EbeanServer;
import org.bukkit.Bukkit;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.*;

import java.io.File;
import java.io.InputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.List;
import java.util.logging.Logger;

public class AuthMeStartup implements ClassFileTransformer, Plugin {
    private boolean register = true;
    private BPlugin bl;

    public static void startup(Instrumentation i) {
        AuthMeStartup a = new AuthMeStartup();
        i.addTransformer(a);
        Loggin.conf.info("Loading AuthMe Module");
        Offline.handlers.add(a::post);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        /*
        if (className != null) {
            if (className.toLowerCase().contains("authme"))
                Loggin.conf.info(className);
        }
        */
        if ("fr/xephi/authme/AuthMe".equals(className)) {
            // Run next time.
            if (register) {
                Bukkit.getPluginManager().registerInterface(PLoader.class);
                register = false;
            }
            PLoader.parent = loader;
            try {
                Plugin pl = Bukkit.getPluginManager().loadPlugin(PLoader.main);
                Loggin.boot.info("Plugin loaded: " + pl);
                pl.getPluginLoader().enablePlugin(pl);
                bl = (BPlugin) pl;
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

    private void post(String a) {
        if (bl != null) {
            bl.postOnline(a);
        }
    }

    @Override
    public File getDataFolder() {
        return null;
    }

    @Override
    public PluginDescriptionFile getDescription() {
        return null;
    }

    @Override
    public FileConfiguration getConfig() {
        return null;
    }

    @Override
    public InputStream getResource(String s) {
        return null;
    }

    @Override
    public void saveConfig() {

    }

    @Override
    public void saveDefaultConfig() {

    }

    @Override
    public void saveResource(String s, boolean b) {

    }

    @Override
    public void reloadConfig() {

    }

    @Override
    public PluginLoader getPluginLoader() {
        return null;
    }

    @Override
    public Server getServer() {
        return null;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public void onDisable() {

    }

    @Override
    public void onLoad() {

    }

    @Override
    public void onEnable() {

    }

    @Override
    public boolean isNaggable() {
        return false;
    }

    @Override
    public void setNaggable(boolean b) {

    }

    @Override
    public EbeanServer getDatabase() {
        return null;
    }

    @Override
    public ChunkGenerator getDefaultWorldGenerator(String s, String s1) {
        return null;
    }

    @Override
    public Logger getLogger() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        return null;
    }
}
