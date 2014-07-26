package net.vaultcraft.vchub.listener;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Created by Connor on 7/23/14. Designed for the VCHub project.
 */

public class HubListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        VCHub.async_player_map.put(player.getUniqueId(), player);
        player.getInventory().clear();

        player.getInventory().setItem(0, VCItems.GAME_SELECTOR);
        player.getInventory().setItem(1, VCItems.HUB_SELECTOR);

        player.getInventory().setItem(3, VCItems.NO_ACTIVE_PERK);
        player.getInventory().setItem(4, VCItems.VAULT_COINS.clone());
        player.getInventory().setItem(5, VCItems.PEARL_OF_TELEPORTATION);

        player.getInventory().setItem(7, VCItems.MAP_BY);
        player.getInventory().setItem(8, VCItems.USER_SETTINGS.clone());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        VCHub.async_player_map.remove(player.getUniqueId());
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(EntitySpawnEvent event) {
        event.setCancelled(true);
    }
}
