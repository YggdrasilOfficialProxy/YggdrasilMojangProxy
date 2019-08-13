package cn.mcres.karlatemp.mojangyggdrasil.plugin;

import cn.mcres.karlatemp.mojangyggdrasil.Offline;
import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;
import org.bukkit.plugin.*;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

public class PLoader implements PluginLoader {
    protected final Server server;

    public PLoader(Server server) {
        this.server = server;
    }

    public static ClassLoader parent;
    public static final File main = new File("w") {
        @Override
        public String getName() {
            return "MojangYggdrasilProxy/MainStartup";
        }
    };

    @Override
    public Plugin loadPlugin(File file) throws InvalidPluginException, UnknownDependencyException {
        try {
            BPlugin bp = (BPlugin) new ClassLoader() {
                @Override
                protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                    synchronized (getClassLoadingLock(name)) {
                        Class loaded = findLoadedClass(name);
                        if (loaded != null) {
                            return loaded;
                        }
                        return findClass(name);
                    }
                }

                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    if ("cn.mcres.karlatemp.mojangyggdrasil.plugin.AuthMeInjectPlugin".equals(name)) {
                        InputStream buffer = PLoader.this.getClass().getResourceAsStream("AuthMeInjectPlugin.class");
                        if (buffer != null) {
                            try (InputStream clazz = buffer) {
                                byte[] array = Offline.readAll(clazz);
                                return defineClass(name, array, 0, array.length);
                            } catch (IOException ioe) {
                            }
                        }
                    }
                    if (parent != null) {
                        return parent.loadClass(name);
                    }
                    return super.findClass(name);
                }
            }.loadClass("cn.mcres.karlatemp.mojangyggdrasil.plugin.AuthMeInjectPlugin").newInstance();
            bp.loader = this;
            return bp;
        } catch (Throwable thr) {
            throw new InvalidPluginException(thr);
        }
    }

    @Override
    public PluginDescriptionFile getPluginDescription(File file) throws InvalidDescriptionException {
        return null;
    }

    @Override
    public Pattern[] getPluginFileFilters() {
        return new Pattern[]{
                Pattern.compile("^MojangYggdrasilProxy\\/MainStartup$")
        };
    }

    @Override
    public Map<Class<? extends Event>, Set<RegisteredListener>> createRegisteredListeners(Listener listener, Plugin plugin) {
        return null;
    }

    @Override
    public void enablePlugin(Plugin plugin) {
        if (plugin instanceof BPlugin) {
            ((BPlugin) plugin).enabled = true;
            plugin.onEnable();
        }
    }

    @Override
    public void disablePlugin(Plugin plugin) {
        if (plugin instanceof BPlugin) {
            ((BPlugin) plugin).enabled = false;
            plugin.onDisable();
        }
    }
}
