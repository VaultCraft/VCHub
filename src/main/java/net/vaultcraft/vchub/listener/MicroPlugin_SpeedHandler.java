package net.vaultcraft.vchub.listener;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.chat.Prefix;
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
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.List;

/**
 * Created by Connor on 7/27/14. Designed for the VCHub project.
 */

public class MicroPlugin_SpeedHandler implements Listener {

    private int taskID;

    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());
    }

    public void onDisable() {
        for (Player player : VCHub.async_player_map.values()) {
            player.removePotionEffect(PotionEffectType.SPEED);
        }
        cannotUse.clear();
    }

    private static List<Player> cannotUse = Lists.newArrayList();

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        final Player player = event.getPlayer();
        if (event.getAction().equals(Action.PHYSICAL))
            return;

        if (player.getItemInHand().equals(VCItems.SPEED_BOOST)) {
            if (cannotUse.contains(player))
                return;

            if (player.hasPotionEffect(PotionEffectType.SPEED)) {
                player.removePotionEffect(PotionEffectType.SPEED);
                Form.at(player, "Speed boost toggled &coff" + Prefix.VAULT_CRAFT.getChatColor() + "!");
            } else {
                player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, Integer.MAX_VALUE, 1));
                Form.at(player, "Speed boost toggled &aon" + Prefix.VAULT_CRAFT.getChatColor() + "!");
            }

            Runnable run = new Runnable() {
                public void run() {
                    cannotUse.remove(player);
                }
            };
            Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), run, 20*5);
            cannotUse.add(player);

            event.setCancelled(true);
        }
    }

    @EventHandler
    public void potionDrink(PlayerItemConsumeEvent event) {
        event.setCancelled(true);
    }
}
