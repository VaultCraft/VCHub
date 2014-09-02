package net.vaultcraft.vchub;

import net.vaultcraft.vchub.handler.PlayerVisibilityHandler;
import net.vaultcraft.vchub.listener.HubListener;
import net.vaultcraft.vchub.listener.MicroPlugin_PearlHandler;
import net.vaultcraft.vchub.listener.MicroPlugin_PrefsHandler;
import net.vaultcraft.vchub.listener.MicroPlugin_SpeedHandler;
import net.vaultcraft.vchub.perks.Perk;
import net.vaultcraft.vchub.perks.PerkHandler;
import net.vaultcraft.vchub.task.StatusBarTask;
import net.vaultcraft.vchub.user.UserPrefs;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
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
    private MicroPlugin_SpeedHandler spdh = new MicroPlugin_SpeedHandler();

    //handlers
    private PlayerVisibilityHandler pvh;

    //perks
    private PerkHandler perkHandler;

    //spawn
    private Location spawn;

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
        spdh.onEnable();

        pvh = new PlayerVisibilityHandler();
        perkHandler = new PerkHandler();

        Runnable dayTask = new Runnable() {
            public void run() {
                for (Player player : async_player_map.values()) {
                    User user = User.fromPlayer(player);
                    if (user.getUserdata(UserPrefs.FORCE_DAYTIME.getSerialNumber()+"") == null)
                        player.setPlayerTime(6000, false);
                    else if (User.fromPlayer(player).getUserdata(UserPrefs.FORCE_DAYTIME.getSerialNumber()+"").toLowerCase().equals("true")) {
                        player.setPlayerTime(6000, false);
                    } else {
                        player.setPlayerTime(18000, false);
                    }
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, dayTask, 20, 20);

        Runnable tokenTask = new Runnable() {
            public void run() {
                for (Player player : async_player_map.values()) {
                    //update item name
                    player.getInventory().setItem(5, VCItems.build(Material.EMERALD, "&6&lTotal Tokens: &f&l" + User.fromPlayer(player).getTokens(), "&5Buy more tokens today!", "&5Use &e\"/buy\" &5for more information"));
                    player.updateInventory();
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, tokenTask, 0, 20*10);

        if (getConfig().contains("spawn")) {
            String[] split = getConfig().getString("spawn").split(",");
            spawn = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new StatusBarTask(), 1, 1);
    }

    public void onDisable() {
        preh.onDisable();
        prlh.onDisable();
        spdh.onDisable();

        for (Player player : async_player_map.values()) {
            //disable perks
            Perk find = PerkHandler.getActivePerk(player);
            if (find == null)
                continue;

            find.stop(player); // B-)
        }

        for (LivingEntity ent : Bukkit.getWorlds().get(0).getLivingEntities()) {
            if (ent instanceof Player)
                continue;

            ent.remove();
        }
    }

    public Location getSpawn() {
        return spawn;
    }
}
