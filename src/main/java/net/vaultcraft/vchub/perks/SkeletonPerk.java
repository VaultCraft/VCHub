package net.vaultcraft.vchub.perks;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.logging.Logger;
import net.vaultcraft.vcutils.uncommon.FireworkEffectPlayer;
import net.vaultcraft.vcutils.uncommon.Particles;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

/**
 * Created by tacticalsk8er on 7/30/2014.
 */
public class SkeletonPerk implements Perk, Listener {

    private static ItemStack active = VCItems.build(Material.BOW, "&f&lKing Skeleton Bow", "&fPrepare your bow for the", "&fmaster of all projectiles", "&fShoots up to 5 arrows at once!");

    public SkeletonPerk(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        active.addEnchantment(Enchantment.ARROW_INFINITE, 1);
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

    }

    @Override
    public boolean isUsing(Player player) {
        return false;
    }

    @Override
    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.SKELETON);
    }

    @EventHandler
    public void onBowFire(EntityShootBowEvent e) {
        if(!(e.getEntity() instanceof Player))
            return;
        Player player = (Player) e.getEntity();
        if(!player.getItemInHand().getItemMeta().getDisplayName().equals(active.getItemMeta().getDisplayName()))
            return;
        int arrows = (int) (e.getForce() / .2);
        float spread = .5f;
        e.getProjectile().remove();
        for(int i = 0; i < arrows; i++) {
            Vector vector = player.getEyeLocation().getDirection().multiply(1.5).add(new Vector((Math.random() * spread) - (spread / 2), (Math.random() * spread) - (spread / 2), (Math.random() * spread) - (spread / 2)));
            Arrow arrow = player.launchProjectile(Arrow.class, vector.multiply(1.5));
            Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), new SkeletonPerkTask(arrow, 40), 1l);
        }
        e.setCancelled(true);
    }

    @EventHandler
    public void onItemPickup(PlayerPickupItemEvent e) {
        if(e.getItem().getItemStack().getType() == Material.ARROW)
            e.setCancelled(true);
    }

    private class SkeletonPerkTask implements Runnable {

        private Arrow arrow;
        private int delay;

        public SkeletonPerkTask(Arrow arrow, int delay) {
            this.arrow = arrow;
            this.delay = delay;
        }

        @Override
        public void run() {
            if(delay == 0) {
                arrow.remove();
                try {
                    FireworkEffectPlayer.playFirework(arrow.getWorld(), arrow.getLocation(), random());
                } catch (Exception e) {
                    Logger.error(VCHub.getInstance(), e);
                }
                return;
            }
            delay--;
            Particles.FIREWORKS_SPARK.sendToLocation(arrow.getLocation(), 0, 0, 0, 0, 1);
            Bukkit.getScheduler().runTaskLater(VCHub.getInstance(), this, 1l);
        }
    }

    private static FireworkEffect random() {
        Color[] c = {Color.WHITE, Color.BLACK, Color.GRAY};
        return FireworkEffect.builder().withColor(c[(int)(Math.random()*c.length)]).withFade(c[(int)(Math.random()*c.length)]).with(FireworkEffect.Type.BALL_LARGE).withTrail().build();
    }

    public String getName() {
        return "King Bow";
    }
}
