package net.vaultcraft.vchub;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.commands.VCHelp;
import net.vaultcraft.vchub.commands.VCRules;
import net.vaultcraft.vchub.handler.PlayerVisibilityHandler;
import net.vaultcraft.vchub.listener.HubListener;
import net.vaultcraft.vchub.listener.MicroPlugin_PearlHandler;
import net.vaultcraft.vchub.listener.MicroPlugin_PrefsHandler;
import net.vaultcraft.vchub.listener.MicroPlugin_SpeedHandler;
import net.vaultcraft.vchub.perks.Perk;
import net.vaultcraft.vchub.perks.PerkHandler;
import net.vaultcraft.vchub.task.StatusBarTask;
import net.vaultcraft.vchub.task.scoreboard.VCScoreboardManager;
import net.vaultcraft.vchub.user.UserPrefs;
import net.vaultcraft.vcutils.command.CommandManager;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Connor on 7/23/14. Designed for the VCHub project.
 */

public class VCHub extends JavaPlugin {
    //hash map
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

        CommandManager.addCommand(new VCHelp("help", Group.COMMON));
        CommandManager.addCommand(new VCRules("rules", Group.COMMON));

        pvh = new PlayerVisibilityHandler();
        perkHandler = new PerkHandler();

        Runnable dayTask = () -> {
            for (Player player : async_player_map.values()) {
                try {
                    User user = User.fromPlayer(player);
                    if (user.getUserdata(UserPrefs.FORCE_DAYTIME.getSerialNumber() + "") == null)
                        player.setPlayerTime(6000, false);
                    else if (User.fromPlayer(player).getUserdata(UserPrefs.FORCE_DAYTIME.getSerialNumber() + "").toLowerCase().equals("true")) {
                        player.setPlayerTime(6000, false);
                    } else {
                        player.setPlayerTime(18000, false);
                    }
                } catch (NullPointerException ex) {
                    async_player_map.remove(player);
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, dayTask, 20, 20);

        Runnable tokenTask = () -> {
            for (Player player : async_player_map.values()) {
                //update item name
                try {
                    player.getInventory().setItem(5, VCItems.build(Material.EMERALD, "&6&lTotal Tokens: &f&l" + User.fromPlayer(player).getTokens(), "&5Buy more tokens today!", "&5Use &e\"/buy\" &5for more information"));
                    player.updateInventory();
                } catch (NullPointerException ex) {
                    async_player_map.remove(player);
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, tokenTask, 0, 20 * 10);

        saveDefaultConfig();

        if (getConfig().contains("spawn")) {
            String[] split = getConfig().getString("spawn").split(",");
            spawn = new Location(Bukkit.getWorld(split[0]), Double.parseDouble(split[1]), Double.parseDouble(split[2]), Double.parseDouble(split[3]), Float.parseFloat(split[4]), Float.parseFloat(split[5]));
        }

        Bukkit.getScheduler().scheduleAsyncRepeatingTask(this, new StatusBarTask(), 1, 1);
        VCScoreboardManager.init();
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

        saveDefaultConfig();
    }

    public Location getSpawn() {
        return spawn;
    }

    public static List<Player> getStaff() {
        List<Player> staff = Lists.newArrayList();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (User.fromPlayer(player) != null && User.fromPlayer(player).getGroup() != null)
                if (User.fromPlayer(player).getGroup().hasPermission(Group.HELPER))
                    staff.add(player);
        }
        return staff;
    }
}
