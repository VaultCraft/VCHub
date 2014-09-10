package net.vaultcraft.vchub.task.scoreboard;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.perks.PerkHandler;
import net.vaultcraft.vcutils.scoreboard.*;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.List;

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

        header = new VCTicker(ChatColor.BOLD, "Welcome "+player.getName()+" to VaultCraft!         ", 14);

        text.put(15, "----------------");
        text.put(14, "&5&lTokens");
        text.put(13, "{tokens}");
        text.put(12, " ");
        text.put(11, "&5&lOnline Staff");
        text.put(9, "  ");
        text.put(8, "&5&lOnline");
        text.put(7, "{online}");
        text.put(6, "   ");
        text.put(5, "&5&lCurrent Perk");
        text.put(4, "{perk}");
        text.put(3, "    ");
        text.put(2, "&5&lWebsite");
    }

    private VCTicker header;
    private VCTicker staff = new VCTicker("", 16);
    private VCTicker website = new VCTicker("https://vaultcraft.net               ", 16);

    private HashMap<Integer, String> text = new HashMap<>();

    public void run() {
        if (current == null) {
            current = new VCObjective(header.tick());

            current.addScore(new VCScore(format(VCHub.getStaff()), 10, current));
            current.addScore(new VCScore("", 1, current));

            for (int x : text.keySet()) {
                String txt = text.get(x);
                current.addScore(new VCScore(txt, x, current));
            }

            current.addScoreboardAndDisplay(board, VCDisplay.SIDEBAR);
        }

        current.setName(header.tick());

        staff.updateTicker(format(VCHub.getStaff())+"               ");
        current.getFirstScore(1).setName(website.tick());
        current.getFirstScore(10).setName(staff.tick());

        for (int x : text.keySet()) {
            VCScore score = current.getFirstScore(x);
            String use = text.get(x);
            use = use.replace("{perk}", (PerkHandler.getActivePerk(player) == null ? "None" : PerkHandler.getActivePerk(player).getName()));
            use = use.replace("{online}", Bukkit.getOnlinePlayers().size()+"");
            use = use.replace("{tokens}", User.fromPlayer(player).getTokens()+"");
            score.setName(ChatColor.translateAlternateColorCodes('&', use));
        }
    }

    public static String format(List<Player> players) {
        String s = "";
        for (Player p : players) {
            s+=p.getName()+", ";
        }
        if (s.endsWith(", "))
            s = s.substring(0, s.length()-2);

        return s;
    }
}
