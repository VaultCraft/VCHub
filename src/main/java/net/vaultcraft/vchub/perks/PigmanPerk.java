package net.vaultcraft.vchub.perks;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.chat.Prefix;
import net.vaultcraft.vcutils.logging.Logger;
import net.vaultcraft.vcutils.uncommon.FireworkEffectPlayer;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by tacticalsk8er on 7/29/2014.
 */
public class PigmanPerk implements Perk, Listener {

    ItemStack active = VCItems.build(Material.BLAZE_ROD, "&4&lFlamethrower", "&fThrow fire at your enemies");
    List<String> using = new ArrayList<>();
    List<Item> items = new ArrayList<>();
    HashMap<String, Long> cooldown = new HashMap<>();

    public PigmanPerk(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public ItemStack getActivatorStack() {
        return active;
    }

    @Override
    public void stop(Player player) {
    }

    @Override
    public void start(Player player) {
        if(using.contains(player.getName())) {
            Form.at(player, Prefix.ERROR, "You can currently using this perk!");
            return;
        }
        if(cooldown.containsKey(player.getName())) {
            if (!(System.currentTimeMillis() - cooldown.get(player.getName()) >= 60000)) {
                Form.at(player, Prefix.ERROR ,String.format("You can use the Flamethrower in %d second(s)", TimeUnit.MILLISECONDS.toSeconds(-(System.currentTimeMillis() - (cooldown.get(player.getName()) + 60000)))));
                return;
            } else {
                cooldown.remove(player.getName());
            }
        }
        Bukkit.getScheduler().runTask(VCHub.getInstance(), new FlamethrowerTask(player, 1, 100));
    }

    @Override
    public boolean isUsing(Player player) {
        return false;
    }

    @Override
    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.PIGMAN);
    }

    private class FlamethrowerTask implements Runnable{

        private Player player;
        private int delay;
        private int times;

        public FlamethrowerTask(Player player, int delay, int times) {
            this.player = player;
            this.delay = delay;
            this.times = times;
            using.add(player.getName());
        }

        @Override
        public void run() {
            if(times == 0) {
                using.remove(player.getName());
                cooldown.put(player.getName(), System.currentTimeMillis());
                return;
            }
            times--;
            final Item fire = player.getWorld().dropItem(player.getEyeLocation(), new ItemStack(Material.FIRE));
            fire.setPickupDelay(Integer.MAX_VALUE);
            fire.setVelocity(player.getLocation().getDirection().multiply(1.2).add(new Vector((Math.random() * 0.1) - 0.05, (Math.random() * 0.1) - 0.05, (Math.random() * 0.1) - 0.05)));
            items.add(fire);
            player.getWorld().playSound(player.getLocation(), Sound.BLAZE_BREATH, 1, -5);
            Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if(Math.random() > 0.8) {
                        try {
                            FireworkEffectPlayer.playFirework(fire.getWorld(), fire.getLocation(), FireworkEffect.builder().withColor(Color.RED).withFade(Color.ORANGE).with(FireworkEffect.Type.BURST).withFlicker().build());
                        } catch (Exception e) {
                            Logger.error(VCHub.getInstance(), e);
                        }
                    }
                    fire.remove();
                    items.remove(fire);
                }
            }, 50l);
            Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), this, delay);
        }
    }

    @EventHandler
    public void onChunkUnload(ChunkUnloadEvent e) {
        for(Entity entity: e.getChunk().getEntities()) {
            if(entity instanceof Item) {
                if(items.contains(entity)) {
                    entity.remove();
                    items.remove(entity);
                }
            }
        }
    }
}