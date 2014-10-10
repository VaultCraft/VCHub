package net.vaultcraft.vchub.commands;

import net.vaultcraft.vcutils.command.ICommand;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by CraftFest on 10/9/2014.
 */

public class VCHelp extends ICommand {

    public VCHelp(String name, Group permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void processCommand(Player player, String[] strings) {
        VCHub.help.setItem(2, new ItemStack(VCItems.WEBSITE));
        VCHub.help.setItem(4, new ItemStack(VCItems.WELCOME));
        VCHub.help.setItem(6, new ItemStack(VCItems.SHOP));
        VCHub.help.setItem(18, new ItemStack(VCItems.GAME_SELECTOR_TUTORIAL));
        VCHub.help.setItem(19, new ItemStack(VCItems.STORE_TUTORIAL));
        VCHub.help.setItem(20, new ItemStack(VCItems.SPEED_BOOST_TUTORIAL));
        VCHub.help.setItem(21, new ItemStack(VCItems.NO_ACTIVE_PERK_TUTORIAL));
        VCHub.help.setItem(22, new ItemStack(VCItems.PICK_PERK_TUTORIAL));
        VCHub.help.setItem(23, new ItemStack(VCItems.VAULT_COINS_TUTORIAL));
        VCHub.help.setItem(24, new ItemStack(VCItems.PEARL_OF_TELEPORTATION_TUTORIAL));
        VCHub.help.setItem(25, new ItemStack(VCItems.MAP_BY_TUTORIAL));
        VCHub.help.setItem(26, new ItemStack(VCItems.USER_SETTINGS_TUTORIAL));
        player.openInventory(VCHub.help);
    }

}
