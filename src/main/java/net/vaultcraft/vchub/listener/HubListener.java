package net.vaultcraft.vchub.listener;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vchub.perks.Perk;
import net.vaultcraft.vchub.perks.PerkHandler;
import net.vaultcraft.vchub.task.scoreboard.VCScoreboardManager;
import net.vaultcraft.vcutils.VCUtils;
import net.vaultcraft.vcutils.bossbar.StatusBarAPI;
import net.vaultcraft.vcutils.uncommon.Particles;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import net.vaultcraft.vcutils.user.UserLoadedEvent;
import org.bukkit.*;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.Vector;

import java.util.List;

/**
 * Created by Connor on 7/23/14. Designed for the VCHub project.
 */

public class HubListener implements Listener {

    @EventHandler(priority = EventPriority.LOW)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        player.teleport(VCHub.getInstance().getSpawn());

        if(!player.getGameMode().equals(GameMode.SURVIVAL)) {
            player.setGameMode(GameMode.SURVIVAL);
        }

        VCHub.async_player_map.put(player.getUniqueId(), player);
        player.getInventory().clear();

        player.getInventory().setItem(0, VCItems.GAME_SELECTOR);
        player.getInventory().setItem(1, VCItems.STORE);
        player.getInventory().setItem(2, VCItems.SPEED_BOOST);
        player.getInventory().setItem(3, VCItems.NO_ACTIVE_PERK);
        player.getInventory().setItem(4, VCItems.PICK_PERK);
        player.getInventory().setItem(17, new ItemStack(Material.ARROW));
        ItemStack modify = VCItems.VAULT_COINS.clone();
        ItemMeta meta = modify.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&6&lTotal Tokens: &f&l"+ User.fromPlayer(player).getTokens()));
        modify.setItemMeta(meta);
        player.getInventory().setItem(5, modify);

        player.getInventory().setItem(6, VCItems.PEARL_OF_TELEPORTATION);
        player.getInventory().setItem(7, VCItems.MAP_BY);
        player.getInventory().setItem(8, VCItems.USER_SETTINGS.clone());

        //Double Jump
        player.setAllowFlight(true);

        StatusBarAPI.setStatusBar(player, "", 0f);
        VCScoreboardManager.addPlayer(player);
    }

    @EventHandler
    public void onUserLoad(UserLoadedEvent e) {
        Player player = e.getUser().getPlayer();
        VCUtils.getGhostFactory().addPlayer(player);
        VCUtils.getGhostFactory().setGhost(player, !User.fromPlayer(player).getGroup().hasPermission(Group.HELPER));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();

        VCHub.async_player_map.remove(player.getUniqueId());

        Perk perk = PerkHandler.getActivePerk(player);
        if (perk == null)
            return;

        perk.stop(player);
        VCUtils.getGhostFactory().setGhost(player, false);
        VCUtils.getGhostFactory().removePlayer(player);

        VCScoreboardManager.removePlayer(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageEvent event) {
        event.setCancelled(true);

        if (event.getCause().equals(EntityDamageEvent.DamageCause.VOID)) {
            event.getEntity().teleport(VCHub.getInstance().getSpawn());
        }
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

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBlockBreak(BlockBreakEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (!(event.getSpawnReason().equals(CreatureSpawnEvent.SpawnReason.CUSTOM)))
            event.setCancelled(true);
    }

    @EventHandler
    public void onItemSpawn(ItemSpawnEvent event) {
        if (event.getEntity().getItemStack().getType().equals(Material.LEASH))
            event.setCancelled(true);
    }

    @EventHandler
    public void onFly(PlayerToggleFlightEvent event) {
        if(event.getPlayer().getGameMode().equals(GameMode.CREATIVE))
            return;
        Player player = event.getPlayer();
        player.playSound(player.getLocation(), Sound.IRONGOLEM_THROW, 5, -10);
        player.setVelocity(player.getLocation().getDirection().multiply(1).setY(1));
        Particles.FLAME.sendToLocation(player.getLocation(), 0.5f, 0, 0.5f, 0, 25);
        player.setAllowFlight(false);
        event.setCancelled(true);
    }

    private List<Entity> cannotUse = Lists.newArrayList();

    @EventHandler
    public void onPlayerInteract(PlayerMoveEvent event) {
        if (event.getTo().getBlock().getType().equals(Material.GOLD_PLATE)) {
            if (cannotUse.contains(event.getPlayer()))
                return;

            final LivingEntity entity = event.getPlayer();
            Vector vec = entity.getEyeLocation().getDirection().multiply(15.0).setY(1.0);
            entity.setVelocity(vec);
            cannotUse.add(entity);
            Runnable remove = new Runnable() {
                @Override
                public void run() {
                    cannotUse.remove(entity);
                }
            };
            Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), remove, 15);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.PHYSICAL))
            return;

        if (event.getPlayer().getItemInHand().equals(VCItems.GAME_SELECTOR)) {
            event.getPlayer().chat("/server");
        }

        if (event.getPlayer().getItemInHand().equals(VCItems.STORE)) {
            event.getPlayer().chat("/buy");
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if(!player.getAllowFlight())
            if(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType() != Material.AIR)
                player.setAllowFlight(true);
    }

    @EventHandler
    public void onWeatherToggle(WeatherChangeEvent event) {
        if (!(event.getWorld().hasStorm()))
            event.setCancelled(true);
    }
}
