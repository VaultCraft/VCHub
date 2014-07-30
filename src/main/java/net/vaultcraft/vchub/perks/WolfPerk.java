package net.vaultcraft.vchub.perks;

import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.user.Group;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by tacticalsk8er on 7/27/2014.
 */
public class WolfPerk implements Perk {

    private static ItemStack active = VCItems.build(Material.MONSTER_EGG, (byte) 95, "&8&lWolf Companion", "&fHave a pet wolf by your side!", "&fGroup: " + Group.WOLF.getName());
    private static volatile ConcurrentHashMap<Player, Wolf> using = new ConcurrentHashMap<>();

    @Override
    public ItemStack getActivatorStack() {
        return active;
    }

    @Override
    public void stop(Player player) {
        Wolf wolf = using.get(player);
        if (wolf != null)
            if (!wolf.isDead() || !wolf.isEmpty())
                wolf.remove();
        using.remove(player);
    }

    @Override
    public void start(Player player) {
        if (using.containsKey(player)) {
            stop(player);
            return;
        }
        Wolf wolf = (Wolf) player.getWorld().spawnEntity(player.getLocation(), EntityType.WOLF);
        wolf.setLeashHolder(player);
        wolf.setOwner(player);
        wolf.setTamed(true);
        wolf.setCustomName(player.getName() + "'s wolf");
        wolf.setCustomNameVisible(true);
        Form.at(player, "You have a new companion!");
        using.put(player, wolf);
    }

    @Override
    public boolean isUsing(Player player) {
        return using.containsKey(player);
    }

    @Override
    public boolean canUse(Player player) {
        return User.fromPlayer(player).getGroup().hasPermission(Group.WOLF);
    }
}
