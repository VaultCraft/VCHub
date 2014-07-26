package net.vaultcraft.vchub.handler;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.user.UserPrefs;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashSet;
import java.util.Observer;

/**
 * Created by Connor on 7/25/14. Designed for the VCHub project.
 */

public class PlayerVisibilityHandler implements Listener {

    private HashSet<Player> hide_players = new HashSet<>();

    public PlayerVisibilityHandler() {
        instance = this;

        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());

        for (Player player : Bukkit.getOnlinePlayers()) {
            onPlayerJoin(new PlayerJoinEvent(player, null));
        }
    }

    private static PlayerVisibilityHandler instance;

    public static PlayerVisibilityHandler getInstance() {
        return instance;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        User user = User.fromPlayer(player);
        if (user.getAllUserdata().containsKey(UserPrefs.HIDE_PLAYERS.getSerialNumber()+"")) {
            //check if hiding players
            boolean value = Boolean.valueOf(user.getUserdata(UserPrefs.HIDE_PLAYERS.getSerialNumber()+""));
            if (value) {
                hide_players.add(player);

                hide(player);
            }
        }
    }

    public void update(User user) {
        boolean isHiding = Boolean.valueOf(user.getUserdata(UserPrefs.HIDE_PLAYERS.getSerialNumber()+""));
        if (hide_players.contains(user.getPlayer()) && !isHiding) {
            hide_players.remove(user.getPlayer());
            show(user.getPlayer());
        } else if (isHiding && !(hide_players.contains(user.getPlayer()))) {
            hide_players.add(user.getPlayer());
            hide(user.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if (hide_players.contains(player)) {
            hide_players.remove(player);

            show(player);
        }
    }

    private void show(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player))
                continue;

            if (hide_players.contains(p))
                continue;

            p.showPlayer(player);
            player.showPlayer(p);
        }
    }

    private void hide(Player player) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.equals(player))
                continue;

            p.hidePlayer(player);
            player.hidePlayer(p);
        }
    }
}
