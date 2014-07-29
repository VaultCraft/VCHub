package net.vaultcraft.vchub.perks;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.uncommon.Particles;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Connor on 7/26/14. Designed for the VCHub project.
 */

public class SilverfishPerk implements Perk, Listener {

    private static ItemStack active = VCItems.build(Material.MONSTER_EGG, (byte)60, "&7&lSilverfish Hat", "&fSpawn a silverfish on your head, make him your best buddy!");
    private static volatile List<Player> using = Lists.newArrayList();

    public ItemStack getActivatorStack() {
        return active;
    }

    public void stop(Player player) {
        if (player.getPassenger() != null) {
            player.getPassenger().remove();
            player.getPassenger().eject();
            Particles.ENCHANTMENT_TABLE.sendToLocation(player.getEyeLocation().add(0, 0.6, 0), 0F, 0F, 0F, 1F, 35);
        }
        using.remove(player);
    }

    public void start(Player player) {
        if (player.getPassenger() != null) {
            stop(player);
            return;
        }
        Silverfish mount = (Silverfish)player.getWorld().spawnEntity(player.getEyeLocation(), EntityType.SILVERFISH);
        player.setPassenger(mount);
        mount.setVelocity(new Vector(0, 0, 0));
        using.add(player);
        Form.at(player, "You have a silverfish on your head :o");
    }

    public boolean isUsing(Player player) {
        return using.contains(player);
    }

    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.SILVERFISH);
    }

    @EventHandler
    public void onEntityTeleport(final EntityTeleportEvent event) {
        Runnable run = new Runnable() {
            @Override
            public void run() {
                if (!(event.getEntity() instanceof Player))
                    return;

                Player player = (Player)event.getEntity();
                if (using.contains(player) && player.getPassenger() != null) {
                    Entity tp = player.getPassenger();
                    tp.teleport(player.getEyeLocation());
                    tp.setVelocity(new Vector(0, 0, 0));
                    player.setPassenger(tp);
                }
            }
        };
        Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), run, 2);
    }
}
