package net.vaultcraft.vchub.perks;

import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.trait.LookClose;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.chat.Prefix;
import net.vaultcraft.vcutils.uncommon.FireworkEffectPlayer;
import net.vaultcraft.vcutils.uncommon.Particles;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.*;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Connor on 7/30/14. Designed for the VCHub project.
 */

public class CreeperPerk implements Perk {

    private static ItemStack active = VCItems.build(Material.MONSTER_EGG, (byte) 50, "&b&lParty &2&lCreeper", "&fLaunch a creeper that", "&fexplodes into confetti!");
    private static List<String> using = new ArrayList<>();

    public ItemStack getActivatorStack() {
        return active;
    }

    public void stop(Player player) {
        Form.at(player, Prefix.ERROR, "You are currently using this perk!");
    }

    public void start(final Player player) {
        final NPC creeper = CitizensAPI.getNPCRegistry().createNPC(EntityType.CREEPER, ChatColor.translateAlternateColorCodes('&', "&bParty &2Creeper"));
        creeper.spawn(player.getLocation());
        creeper.addTrait(LookClose.class);
        Vector vector = player.getLocation().getDirection();
        vector.multiply(5);
        creeper.getNavigator().setTarget(new Location(player.getWorld(), vector.getX(), player.getLocation().getY(), vector.getZ()));
        using.add(player.getName());
        Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), () -> {
            final Location loc = creeper.getStoredLocation();

            Particles.EXPLODE.sendToLocation(creeper.getEntity().getLocation(), 1F, 1F, 1F, 1, 10);
            Particles.RED_DUST.sendToLocation(creeper.getEntity().getLocation(), 1F, 1F, 1F, 1, 100);
            Particles.NOTE.sendToLocation(creeper.getEntity().getLocation(), 1.5F, 1.5F, 1.5F, 1, 90);
            for (int i = 0; i < (int)(Math.random()*10)+15; i++) {
                Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), () -> {
                    Location ranged = loc.clone().add((Math.random() * 10) - 5, Math.random() * 10, (Math.random() * 10) - 5);
                    FireworkEffectPlayer.playFirework(loc.getWorld(), ranged, random());
                }, (int)(Math.random()*20));
            }
            creeper.destroy();
            using.remove(player.getName());
        }, 20 * 5l);
    }

    public boolean isUsing(Player player) {
        return using.contains(player.getName());
    }

    private FireworkEffect random() {
        Color c1 = Color.fromRGB((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));
        Color c2 = Color.fromRGB((int)(Math.random()*255), (int)(Math.random()*255), (int)(Math.random()*255));

        FireworkEffect.Builder b = FireworkEffect.builder().withColor(c1).withFade(c2).with(FireworkEffect.Type.CREEPER);
        if (Math.random() > .5)
            b = b.withFlicker();
        if (Math.random() > .5)
            b = b.withTrail();

        return b.build();
    }

    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.SKELETON);
    }

    public String getName() {
        return "Party Creeper";
    }
}
