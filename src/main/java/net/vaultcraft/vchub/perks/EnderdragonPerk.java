package net.vaultcraft.vchub.perks;

import net.vaultcraft.vchub.VCItems;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Connor on 7/27/14. Designed for the VCHub project.
 */

public class EnderdragonPerk implements Perk {

    private static ItemStack active = VCItems.build(Material.DRAGON_EGG, (byte)0, "&5&lEnderdragon Mount", "&fRide around on an enderdragon!", "&fSecondary ability to shoot fire! (To activate, drop this item)");

    public ItemStack getActivatorStack() {
        return active;
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
