package net.vaultcraft.vchub.perks;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.concurrent.TimeUnit;

/**
 * Created by Connor on 7/30/14. Designed for the VCHub project.
 */

public class EndermanPerk implements Perk {

    private static ItemStack activate = VCItems.build(Material.EYE_OF_ENDER, "&5&lEnder Force", "&fPickup and throw a block!");
    private static HashMap<String, Long> cantUse = new HashMap<>();

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
        if (looking == null) {
            player.playSound(player.getLocation(), Sound.ITEM_BREAK, 1, 0);
            return;
        }

        final int id = looking.getTypeId();
        final byte data = looking.getData();

        new ERun() {
            public void run() {
                player.sendBlockChange(looking.getLocation(), 0, (byte)0);
                player.playEffect(looking.getLocation(), Effect.STEP_SOUND, id);
                player.playSound(player.getLocation(), Sound.IRONGOLEM_HIT, 1, 2);
            }
        }.call(1);

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
                                FallingBlock thrw = player.getWorld().spawnFallingBlock(player.getEyeLocation().clone().add(0, 1, 0), id, data);
                                thrw.setDropItem(false);
                                thrw.setVelocity(player.getLocation().getDirection().multiply(1.3));
                                threw.put(thrw, player);

                                player.playSound(player.getLocation(), Sound.BAT_TAKEOFF, 1, 0.7f);
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
        }.call(20*3);

        cantUse.put(player.getName(), System.currentTimeMillis());
    }

    private static HashMap<FallingBlock, Player> threw = new HashMap<>();

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (threw.containsKey(event.getEntity())) {
            Player value = threw.remove(event.getEntity());
            Location strike = event.getBlock().getLocation();

            event.setCancelled(true);

        }
    }
    public boolean isUsing(Player player) {
        return false;
    }

    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.ENDERMAN);
    }

    private abstract class ERun implements Runnable {
        public void call(int time) {
            Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), this, time);
        }
    }
}
