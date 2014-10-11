package net.vaultcraft.vchub.task.scoreboard;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 9/2/14. Designed for the VCHub project.
 */

public class VCScoreboardManager {

    private static HashMap<Player, VCScoreboardController> boards = new HashMap<>();

    public static void init() {
        Runnable ticker = () -> {
            List<Player> remove = Lists.newArrayList();
            for (Player key : boards.keySet()) {
                if (!(key.isOnline()) || User.fromPlayer(key) == null)
                    remove.add(key);
                else {
                    VCScoreboardController value = boards.get(key);
                    value.run();
                }
            }

            for (Player player : remove)
                boards.remove(player);
        };
        Bukkit.getScheduler().scheduleSyncRepeatingTask(VCHub.getInstance(), ticker, 5, 5);
    }

    public static void addPlayer(Player player) {
        boards.put(player, new VCScoreboardController(player));
    }

    public static void removePlayer(Player player) {
        if (boards.containsKey(player))
            boards.remove(player);
    }
}
