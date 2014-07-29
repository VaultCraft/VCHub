package net.vaultcraft.vchub.listener;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vchub.perks.Perk;
import net.vaultcraft.vchub.perks.PerkHandler;
import net.vaultcraft.vcutils.uncommon.Particles;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;

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
        player.getInventory().setItem(2, VCItems.SPEED_BOOST);
        player.getInventory().setItem(3, VCItems.NO_ACTIVE_PERK);
        player.getInventory().setItem(4, VCItems.PICK_PERK);
        player.getInventory().setItem(5, VCItems.VAULT_COINS.clone());
        player.getInventory().setItem(6, VCItems.PEARL_OF_TELEPORTATION);
        player.getInventory().setItem(7, VCItems.MAP_BY);
        player.getInventory().setItem(8, VCItems.USER_SETTINGS.clone());

        //Double Jump
        player.setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        VCHub.async_player_map.remove(player.getUniqueId());

        Perk perk = PerkHandler.getActivePerk(player);
        if (perk == null)
            return;

        perk.stop(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerHunger(FoodLevelChangeEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onInventoryClick(InventoryClickEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            event.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (!(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.IRONGOLEM_THROW, 3, -10);
        player.setVelocity(player.getLocation().getDirection().multiply(1).setY(1));
        Particles.FLAME.sendToLocation(player.getLocation(), 0.5f, 0, 0.5f, 0, 25);
        player.setAllowFlight(false);
        event.setCancelled(true);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!player.getAllowFlight())
            if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR)
                player.setAllowFlight(true);
    }
}
