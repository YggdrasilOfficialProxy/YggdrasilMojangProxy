package cn.mcres.karlatemp.mojangyggdrasil.plugin;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import fr.xephi.authme.api.v3.AuthMeApi;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;

import java.net.Proxy;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AuthMeInjectPlugin extends BPlugin implements Listener, EventExecutor {
    private String lodPWD = "WIXNuW892IW(@*#ISDN82hjjNO@(---WLKMXMXMMXMMX(@*&niWEW@#28328173*(@(**@@*(999";
    private final YggdrasilAuthenticationService authenticationService;
    private final MinecraftSessionService minecraftSessionService;

    public AuthMeInjectPlugin() {
        authenticationService = new YggdrasilAuthenticationService(Proxy.NO_PROXY, UUID.randomUUID().toString());
        minecraftSessionService = authenticationService.createMinecraftSessionService();
    }

    @Override
    public void onEnable() {
        RegisteredListener rl = new RegisteredListener(this, this, EventPriority.MONITOR, this, false);
        PlayerJoinEvent.getHandlerList().register(rl);
        getLogger().info("Registered Listener");
        if (Boolean.getBoolean("mojangyggdrasil.offline")) {
            AsyncPlayerPreLoginEvent.getHandlerList().register(rl);
        }
    }

    @Override
    public String getName() {
        return "MojangYggdrasilAuthMeInject";
    }

    protected void postOnline(String uuid) {
        uuids.put(uuid.replaceAll("\\-", ""), System.currentTimeMillis());
    }

    @Override
    public void execute(Listener listener, Event event) throws EventException {
        if (event instanceof PlayerJoinEvent) {
            PlayerJoinEvent pe = (PlayerJoinEvent) event;
            Player p = pe.getPlayer();
            String ky = p.getUniqueId().toString().replaceAll("\\-", "");
            Long time = uuids.get(ky);
            String pname = p.getName();
            AuthMeApi api = AuthMeApi.getInstance();
            if (time != null) {
                uuids.remove(ky);
                if (System.currentTimeMillis() - time < timedout) {
                    if (!api.isRegistered(pname)) {
                        api.registerPlayer(pname, lodPWD);
                        getLogger().info("Registered for " + pname);
                    }
                    api.forceLogin(p);
                    getLogger().info("Login for " + pname);
                    return;
                }
            }
            if (api.isRegistered(pname)) {
                if (api.checkPassword(pname, lodPWD)) {
                    api.forceUnregister(p);
                }
            }
        } else if (getServer().getOnlineMode()) {
        } else if (event instanceof AsyncPlayerPreLoginEvent) {
            AsyncPlayerPreLoginEvent ev = (AsyncPlayerPreLoginEvent) event;
            GameProfile profile = new GameProfile(ev.getUniqueId(), ev.getName());
            try {
                GameProfile gp = minecraftSessionService.hasJoinedServer(profile, getServer().getServerId(), null);
                if (gp.getName().endsWith(ev.getName())) {
                    if (ev.getUniqueId().equals(gp.getId())) return;
                }
                ev.disallow(AsyncPlayerPreLoginEvent.Result.KICK_BANNED, "Auth failed.");
            } catch (AuthenticationUnavailableException e) {
                ev.disallow(AsyncPlayerPreLoginEvent.Result.KICK_FULL, e.getMessage());
            }
        }
    }
}
