package net.vaultcraft.vchub.perks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Connor on 7/30/14. Designed for the VCHub project.
 */

public class CreeperPerk implements Perk {

    public ItemStack getActivatorStack() {
        return null;
    }

    public void stop(Player player) {

    }

    public void start(Player player) {

    }

    public boolean isUsing(Player player) {
        return false;
    }

    public boolean canUse(Player player) {
        return false;
    }
}
