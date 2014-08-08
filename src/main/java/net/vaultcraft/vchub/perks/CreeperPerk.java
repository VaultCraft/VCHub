package net.vaultcraft.vchub.perks;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.chat.Prefix;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 7/30/14. Designed for the VCHub project.
 */

public class CreeperPerk implements Perk {

    private static ItemStack active = VCItems.build(Material.MONSTER_EGG, (byte) 50, "&bParty &2Creeper", "&fLaunch a Creeper that explodes into confetti!");
    private static List<String> using = new ArrayList<>();

    public ItemStack getActivatorStack() {
        return active;
    }

    public void stop(Player player) {
        Form.at(player, Prefix.ERROR, "You are currently using this perk!");
    }

    public void start(final Player player) {
        final NPC creeper = CitizensAPI.getNPCRegistry().createNPC(EntityType.CREEPER, ChatColor.translateAlternateColorCodes('&', "&bParty &2Creeper"));
        creeper.spawn(player.getLocation());
        Vector vector = player.getLocation().getDirection();
        vector.multiply(5);
        creeper.getNavigator().setTarget(new Location(player.getWorld(), vector.getX(), player.getLocation().getY(), vector.getZ()));
        using.add(player.getName());
        Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), new Runnable() {
            @Override
            public void run() {
                creeper.destroy();
                using.remove(player.getName());
            }
        }, 20 * 5l);

    }

    public boolean isUsing(Player player) {
        return using.contains(player.getName());
    }

    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.CREEPER);
    }
}
