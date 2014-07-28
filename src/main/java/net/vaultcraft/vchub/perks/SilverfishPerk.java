package net.vaultcraft.vchub.perks;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.uncommon.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Silverfish;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Connor on 7/26/14. Designed for the VCHub project.
 */

public class SilverfishPerk implements Perk {

    private static ItemStack active = VCItems.build(Material.MONSTER_EGG, (byte)60, "&7&lSilverfish Hat", "&fSpawn a silverfish on your head, make him your best buddy!");
    private static volatile List<Player> using = Lists.newArrayList();

    public SilverfishPerk() {
        Bukkit.getScheduler().scheduleAsyncRepeatingTask(VCHub.getInstance(), new SFISHTask(), 2, 2);
    }

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
        return true;
    }

    private class SFISHTask implements Runnable {
        public void run() {
            for (Player active : using) {
                if (active.getPassenger() == null)
                    continue;

                Location loc = active.getPassenger().getLocation().clone().add(0, 2.1, 0);
                Particles.SPELL.sendToLocation(loc, 0F, 0F, 0F, 1, 1);
            }
        }
    }
}
