package net.vaultcraft.vchub;

import net.vaultcraft.vchub.handler.PlayerVisibilityHandler;
import net.vaultcraft.vchub.listener.HubListener;
import net.vaultcraft.vchub.listener.MicroPlugin_PearlHandler;
import net.vaultcraft.vchub.listener.MicroPlugin_PrefsHandler;
import net.vaultcraft.vchub.user.UserPrefs;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connor on 7/23/14. Designed for the VCHub project.
 */

public class VCHub extends JavaPlugin {

    public static volatile ConcurrentHashMap<UUID, Player> async_player_map = new ConcurrentHashMap<>();

    private MicroPlugin_PearlHandler prlh = new MicroPlugin_PearlHandler();
    private MicroPlugin_PrefsHandler preh = new MicroPlugin_PrefsHandler();

    //handlers
    private PlayerVisibilityHandler pvh;

    private static VCHub instance;

    public static VCHub getInstance() {
        return instance;
    }

    public void onEnable() {
        instance = this;

        HubListener instance = new HubListener();
        Bukkit.getPluginManager().registerEvents(instance, this);
        for (Player player : Bukkit.getOnlinePlayers()) {
            instance.onPlayerJoin(new PlayerJoinEvent(player, null));
        }

        preh.onEnable();
        prlh.onEnable();

        pvh = new PlayerVisibilityHandler();

        Runnable dayTask = new Runnable() {
            public void run() {
                for (Player player : async_player_map.values()) {
                    if (User.fromPlayer(player).getUserdata(UserPrefs.FORCE_DAYTIME.getSerialNumber()+"").toLowerCase().equals("true")) {
                        player.setPlayerTime(6000, false);
                    } else {
                        player.setPlayerTime(18000, false);
                    }
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, dayTask, 20, 20);
    }

    public void onDisable() {
        preh.onDisable();
        prlh.onDisable();
    }
}
