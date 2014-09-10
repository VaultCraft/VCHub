package net.vaultcraft.vchub.perks;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Connor on 7/26/14. Designed for the VCHub project.
 */
public interface Perk {

    public ItemStack getActivatorStack();

    public void stop(Player player);

    public void start(Player player);

    public boolean isUsing(Player player);

    public boolean canUse(Player player);

    public String getName();
}
