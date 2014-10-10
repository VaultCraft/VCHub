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
        VCHub.help.setItem(11, new ItemStack(VCItems.WEBSITE));
        VCHub.help.setItem(13, new ItemStack(VCItems.WELCOME));
        VCHub.help.setItem(15, new ItemStack(VCItems.SHOP));
        VCHub.help.setItem(27, new ItemStack(VCItems.GAME_SELECTOR_TUTORIAL));
        VCHub.help.setItem(28, new ItemStack(VCItems.STORE_TUTORIAL));
        VCHub.help.setItem(29, new ItemStack(VCItems.SPEED_BOOST_TUTORIAL));
        VCHub.help.setItem(30, new ItemStack(VCItems.NO_ACTIVE_PERK_TUTORIAL));
        VCHub.help.setItem(31, new ItemStack(VCItems.PICK_PERK_TUTORIAL));
        VCHub.help.setItem(32, new ItemStack(VCItems.VAULT_COINS_TUTORIAL));
        VCHub.help.setItem(33, new ItemStack(VCItems.PEARL_OF_TELEPORTATION_TUTORIAL));
        VCHub.help.setItem(34, new ItemStack(VCItems.MAP_BY_TUTORIAL));
        VCHub.help.setItem(35, new ItemStack(VCItems.USER_SETTINGS_TUTORIAL));
        player.openInventory(VCHub.help);
    }

}
