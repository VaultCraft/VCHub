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
            "&7Be sure to vote everyday! You can vote by using the command \"&e&l/vote&7\"!",
            "&7Having troubles? Online staff members are always available to help!",
            "&7Visit our website at \"&e&lhttp://vaultcraft.net&7\".",
            "&7Donate today with \"&e&l/buy&7\"!"
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
