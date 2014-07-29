package net.vaultcraft.vchub.listener;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.uncommon.Particles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Created by Connor on 7/24/14. Designed for the VCHub project.
 */

public class MicroPlugin_PearlHandler implements Listener {

    private int taskID;

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());
        taskID = Bukkit.getScheduler().scheduleAsyncRepeatingTask(VCHub.getInstance(), new ParticleTask(), 1l, 1l);
    }

    public void onDisable() {
        Bukkit.getScheduler().cancelTask(taskID);
    }

    private static List<Projectile> active_pearls = Lists.newArrayList();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.PHYSICAL))
            return;

        if (player.getItemInHand().equals(VCItems.PEARL_OF_TELEPORTATION)) {
            event.setCancelled(true);

            EnderPearl proj = player.launchProjectile(EnderPearl.class);
            player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1, 0);

            active_pearls.add(proj);
            player.getInventory().remove(VCItems.PEARL_OF_TELEPORTATION);
            player.updateInventory();
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile proj = event.getEntity();
        if (active_pearls.contains(proj)) {
            active_pearls.remove(proj);
            proj.remove();

            Player shooter = (Player)proj.getShooter();

            Location build = proj.getLocation().add(0, 0.5, 0);
            build.setYaw(shooter.getLocation().getYaw());
            build.setPitch(shooter.getLocation().getPitch());

            shooter.teleport(build);
            shooter.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 160, 1));
            shooter.getInventory().setItem(6, VCItems.PEARL_OF_TELEPORTATION);
            shooter.updateInventory();
        }
    }

    private class ParticleTask implements Runnable {
        public void run() {
            for (Projectile active : active_pearls) {
                Particles.ENCHANTMENT_TABLE.sendToPlayer((Player)active.getShooter(), active.getLocation(), .2f, .2f, .2f, 1f, 20);
            }
        }
    }
}
