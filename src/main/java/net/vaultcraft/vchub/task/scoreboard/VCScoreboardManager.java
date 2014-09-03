package net.vaultcraft.vchub.task.scoreboard;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vcutils.scoreboard.VCDisplay;
import net.vaultcraft.vcutils.scoreboard.VCObjective;
import net.vaultcraft.vcutils.scoreboard.VCScore;
import net.vaultcraft.vcutils.scoreboard.VCTicker;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Connor on 9/2/14. Designed for the VCHub project.
 */

public class VCScoreboardManager {

    private static HashMap<Player, VCScoreboardController> boards = new HashMap<>();

    public static void init() {
        Runnable ticker = new Runnable() {
            public void run() {
                for (Player key : boards.keySet()) {
                    VCScoreboardController value = boards.get(key);
                    value.getCurrentObjective().tick();
                }
            }
        };
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(VCHub.getInstance(), ticker, 2, 2);
    }

    public static void addPlayer(Player player) {
        boards.put(player, new VCScoreboardController(player, makeMain(player)));
    }

    public static void removePlayer(Player player) {
        if (boards.containsKey(player))
            boards.remove(player);
    }

    private static VCObjective makeMain(Player player) {
        VCObjective objective = new VCObjective(new VCTicker(ChatColor.BOLD, "Welcome "+player.getName()+" to VaultCraft", 13));
        VCScore s15 = new VCScore(ChatColor.DARK_PURPLE.toString()+ChatColor.BOLD.toString()+"Tokens", 15, objective);
        VCScore s14 = new VCScore(ChatColor.BOLD+"17 (eg)", 14, objective);
        objective.display(VCDisplay.SIDEBAR);
        return objective;
    }
}
