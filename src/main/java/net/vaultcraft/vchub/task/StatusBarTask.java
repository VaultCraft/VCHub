package net.vaultcraft.vchub.task;

import net.vaultcraft.vcutils.bossbar.StatusBarAPI;
import org.bukkit.ChatColor;

/**
 * Created by Connor on 9/1/14. Designed for the VCHub project.
 */

public class StatusBarTask implements Runnable {

    private static int index = 0;
    private static int padding = 250;
    private static String[] messages = {
            "&7Welcome to &d&lVaultCraft&7! Visit our website at &dvaultcraft.net&7. Read our rules with &d/rules &7and if you're lost use &d/help &7or &d/server&7. Have fun!",
            "&7There is currently a &d20% &7off sale! Use the code &dspooky &7at &dstore.vaultcraft.net &7or use the command &d/buy&7. &dHappy Halloween&7!"
    };
    private static String current_message = messages[0];

    public void run() {
        if (padding++ >= 250) {
            padding = -250;
            current_message = messages[((index+=1) >= messages.length ? index=0 : index)];
        }

        String lPadding = "";
        String rPadding = "";
        if (padding <= 0) {
            for (int x = 0; x < Math.abs(padding); x++) {
                lPadding+=" ";
            }
        } else {
            for (int x = 0; x < padding; x++) {
                rPadding+=" ";
            }
        }
        StatusBarAPI.setAllStatusBars(lPadding + ChatColor.translateAlternateColorCodes('&', current_message) + rPadding, (float)100);
    }
}
