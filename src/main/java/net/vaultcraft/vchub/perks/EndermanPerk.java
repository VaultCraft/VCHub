package net.vaultcraft.vchub.perks;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.uncommon.FireworkEffectPlayer;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Connor on 7/30/14. Designed for the VCHub project.
 */

public class EndermanPerk implements Perk, Listener {

    private static ItemStack activate = VCItems.build(Material.EYE_OF_ENDER, "&5&lEnder Force", "&fPick up and throw a block!");
    private static HashMap<String, Long> cantUse = new HashMap<>();
    private static List<Player> using = Lists.newArrayList();

    public EndermanPerk() {
        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());
    }

    public ItemStack getActivatorStack() {
        return activate;
    }

    public void stop(Player player) {

    }

    public void start(final Player player) {
        //begin pickup task
        if(cantUse.containsKey(player.getName())) {
            if (!(System.currentTimeMillis() - cantUse.get(player.getName()) >= 30000)) {
                Form.at(player, String.format("You can use Ender Force in %d second(s)", TimeUnit.MILLISECONDS.toSeconds(-(System.currentTimeMillis() - (cantUse.get(player.getName()) + 30000)))));
                return;
            } else {
                cantUse.remove(player.getName());
            }
        }

        final Block looking = player.getTargetBlock(null, 6);
        if (looking.getType().equals(Material.AIR)) {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 0);
            return;
        }

        using.add(player);

        final int id = looking.getTypeId();
        final byte data = looking.getData();

        player.playEffect(looking.getLocation(), Effect.STEP_SOUND, id);
        player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1, 2);

        //make little fun task
        new ERun() {
            public void run() {
                float z = 0.5F;
                for (int i = 0; i <= 4; i++) {
                    z+=0.1;
                    final float mirror = z;
                    if (i == 4) {
                        //end
                        new ERun() {
                            public void run() {
                                final FallingBlock thrw = player.getWorld().spawnFallingBlock(player.getEyeLocation().clone().add(0, 1, 0), id, data);
                                thrw.setDropItem(false);
                                thrw.setVelocity(player.getLocation().getDirection().multiply(1.3));
                                threw.put(thrw, player);

                                player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1, 0.7f);
                                using.remove(player);

                                for (int i = 0; i < 35; i++) {
                                    if (thrw.isDead())
                                        return;

                                    new ERun() {
                                        public void run() {
                                            try {
                                                FireworkEffectPlayer.playFirework(thrw.getWorld(), thrw.getLocation(), random());
                                            } catch (Exception e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    }.call(i);
                                }
                            }
                        }.call(i*10);
                    }

                    new ERun() {
                        public void run() {
                            player.playSound(player.getLocation(), Sound.NOTE_STICKS, 1, mirror);
                        }
                    }.call(i*10);
                }
            }
        }.call(20);

        cantUse.put(player.getName(), System.currentTimeMillis());
    }

    private static HashMap<FallingBlock, Player> threw = new HashMap<>();

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (threw.containsKey(event.getEntity())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onProjectileClick(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType().equals(Material.EYE_OF_ENDER)) {
            event.setCancelled(true);
        }
    }



    private static FireworkEffect random() {
        Color[] c = {Color.PURPLE, Color.FUCHSIA};
        return FireworkEffect.builder().withColor(c[(int)(Math.random()*c.length)]).with(FireworkEffect.Type.BALL).withFlicker().build();
    }

    public boolean isUsing(Player player) {
        return using.contains(player);
    }

    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.ENDERMAN);
    }

    private abstract class ERun implements Runnable {
        public void call(int time) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), this, time);
        }
    }

    public String getName() {
        return "Ender Force";
    }
}
