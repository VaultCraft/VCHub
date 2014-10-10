package net.vaultcraft.vchub.perks;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.Controllable;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.EnderDragon;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitTask;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.HashMap;

/**
 * Created by Connor on 7/27/14. Designed for the VCHub project.
 */

public class EnderdragonPerk implements Perk, Listener {

    private static ItemStack active = VCItems.build(Material.DRAGON_EGG, (byte) 0, "&5&lEnder Dragon Mount", "&fRide around on an ender dragon!", "&fSecondary ability to shoot fire!", "&f(To activate, drop this item)");
    private static HashMap<String, NPC> using = new HashMap<>();
    private static HashMap<String, BukkitTask> tasks = new HashMap<>();

    public EnderdragonPerk() {
        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());
    }

    public ItemStack getActivatorStack() {
        return active;
    }

    public void stop(Player player) {
        if(!using.containsKey(player.getName()))
            return;
        NPC dragon = using.get(player.getName());
        dragon.destroy();
        using.remove(player.getName());
        if(tasks.containsKey(player.getName())) {
            tasks.get(player.getName()).cancel();
            tasks.remove(player.getName());
        }
    }

    public void start(final Player player) {
        NPC dragon = CitizensAPI.getNPCRegistry().createNPC(EntityType.ENDER_DRAGON, player.getName() + "'s Ender Dragon");
        dragon.spawn(player.getLocation());
        dragon.addTrait(Controllable.class);
        dragon.getEntity().setPassenger(player);
        using.put(player.getName(), dragon);
        BukkitTask task = Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), new Runnable() {
            @Override
            public void run() {
                if(!using.containsKey(player.getName()))
                    return;
                NPC dragon = using.get(player.getName());
                dragon.destroy();
                using.remove(player.getName());
                tasks.remove(player.getName());
            }
        }, 20 * 30l);
        tasks.put(player.getName(), task);
    }

    public boolean isUsing(Player player) {
        return using.containsKey(player.getName());
    }

    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.ENDERDRAGON);
    }

    @EventHandler
    public void onDismount(EntityDismountEvent event) {
        if(!(event.getEntity() instanceof Player))
            return;
        if(!(event.getDismounted() instanceof EnderDragon))
            return;
        Player player = (Player) event.getEntity();
        if(using.containsKey(player.getName())) {
            using.get(player.getName()).destroy();
            using.remove(player.getName());
        }
    }

    public String getName() {
        return "Ender Mount";
    }
}
