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
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Slime;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by tacticalsk8er on 7/28/2014.
 */
public class SlimePerk implements Perk {
    private static ItemStack active = VCItems.build(Material.SLIME_BALL, "&aSlime Cannon", "&fLaunch a barrage pf slimes at your target!");
    private static HashMap<String, Long> cantUse = new HashMap<>();

    @Override
    public ItemStack getActivatorStack() {
        return active;
    }

    @Override
    public void stop(Player player) {
    }

    @Override
    public void start(Player player) {
        if(cantUse.containsKey(player.getName())) {
            if (!(System.currentTimeMillis() - cantUse.get(player.getName()) >= 30000)) {
                Form.at(player, Prefix.ERROR, String.format("You can use the Slime Cannon in %d second(s)", TimeUnit.MILLISECONDS.toSeconds(-(System.currentTimeMillis() - (cantUse.get(player.getName()) + 30000)))));
                return;
            } else {
                cantUse.remove(player.getName());
            }
        }
        player.playSound(player.getLocation(), Sound.SLIME_ATTACK, 1, 1);
        final List<Slime> slimes = new ArrayList<>();
        float spread = 0.07f;
        for(int i = 0; i < 12; i++) {
            final Slime slime = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME);
            slime.setSize(1);
            Vector vector = player.getEyeLocation().getDirection().multiply(1.5).add(new Vector((Math.random() * spread) - (spread / 2), 0, (Math.random() * spread) - (spread / 2))).setY(1.2);
            slime.setVelocity(vector.multiply(1.5));
            slimes.add(slime);
            Runnable delay = new Runnable() {
                @Override
                public void run() {
                    Location at = slime.getLocation();
                    try {
                        FireworkEffectPlayer.playFirework(at.getWorld(), at, FireworkEffect.builder().withColor(Color.LIME).withFade(Color.GREEN).with(FireworkEffect.Type.BURST).trail(true).build());
                    } catch (Exception e) {
                        Logger.error(VCHub.getInstance(), e);
                    }
                    slime.remove();
                    slimes.remove(slime);
                }
            };
            Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), delay, 10 + (i * 2));
        }
        cantUse.put(player.getName(), System.currentTimeMillis());
    }

    @Override
    public boolean isUsing(Player player) {
        return false;
    }

    @Override
    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.SLIME);
    }
}
