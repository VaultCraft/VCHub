package net.vaultcraft.vchub.perks;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Material;
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
            if (!(System.currentTimeMillis() - cantUse.get(player.getName()) >= 60000)) {
                Form.at(player, String.format("You can use the Slime Cannon in %d second(s)", TimeUnit.MILLISECONDS.toSeconds(-(System.currentTimeMillis() - (cantUse.get(player.getName()) + 60000)))));
                return;
            } else {
                cantUse.remove(player.getName());
            }
        }
        final List<Slime> slimes = new ArrayList<>();
        float spread = 0.2f;
        for(int i = 0; i < 25; i++) {
            int randomSize = (int) (Math.random() * 3);
            Slime slime = (Slime) player.getWorld().spawnEntity(player.getLocation(), EntityType.SLIME);
            slime.setSize(randomSize);
            Vector vector = player.getLocation().getDirection().add(new Vector(Math.random() * spread - spread, Math.random() - spread, Math.random() * spread - spread));
            slime.setVelocity(vector.multiply(1.5));
            slimes.add(slime);
        }
        Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), new Runnable() {
            @Override
            public void run() {
                for(Slime slime1: slimes) {
                    slime1.remove();
                }
                slimes.clear();
            }
        }, 70);
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
