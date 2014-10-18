package net.vaultcraft.vchub.task.scoreboard;

import net.vaultcraft.vchub.perks.PerkHandler;
import net.vaultcraft.vcutils.scoreboard.*;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;

/**
 * Created by Connor on 9/2/14. Designed for the VCHub project.
 */

public class VCScoreboardController implements Runnable {

    private VCScoreboard board;
    private VCObjective current;
    private Player player;

    public VCScoreboardController(Player player) {
        board = new VCScoreboard(player);
        this.player = player;
        text.put(12, " ");
        text.put(11, "&5&lTokens");
        text.put(10, "&7{tokens}");
        text.put(9, "  ");
        text.put(8, "&5&lOnline");
        text.put(7, "&7{online}");
        text.put(6, "    ");
        text.put(5, "&5&lCurrent Perk");
        text.put(4, "&7{perk}");
        text.put(3, "     ");
        text.put(2, "&5&lWebsite");
        text.put(1, "&7vaultcraft.net");
    }

    private HashMap<Integer, String> text = new HashMap<>();

    public void run() {
        if (current == null) {
            current = new VCObjective(ChatColor.translateAlternateColorCodes('&',"&5&lVault&oCraft"));

            for (int x : text.keySet()) {
                String txt = text.get(x);
                current.addScore(new VCScore(txt, x, current));
            }

            current.addScoreboardAndDisplay(board, VCDisplay.SIDEBAR);
        }

        for (int x : text.keySet()) {
            VCScore score = current.getFirstScore(x);
            String use = text.get(x);
            use = use.replace("{perk}", (PerkHandler.getActivePerk(player) == null ? "None" : PerkHandler.getActivePerk(player).getName()));
            use = use.replace("{online}", Bukkit.getOnlinePlayers().size()+"");
            use = use.replace("{tokens}", User.fromPlayer(player).getTokens()+"");
            score.setName(ChatColor.translateAlternateColorCodes('&', use));
        }
    }
}
