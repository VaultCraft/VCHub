package net.vaultcraft.vchub.perks;

import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.LeatherArmorMeta;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Connor on 8/14/14. Designed for the VCHub project.
 */

public class DiscoSuitPerk implements Perk {

    private static ItemStack active = VCItems.build(Material.LEATHER_CHESTPLATE, "&f&lDisco &7&lSuit", "&fEven you can be the", "&fhighlight of the evening!");
    private static Map<Player, DiscoTask> using = new HashMap<>();

    public ItemStack getActivatorStack() {
        return active;
    }

    public void stop(Player player) {
        if (using.containsKey(player)) {

            DiscoTask t = using.remove(player);
            t.alive = false;

            player.getEquipment().setHelmet(null);
            player.getEquipment().setChestplate(null);
            player.getEquipment().setLeggings(null);
            player.getEquipment().setBoots(null);

            player.updateInventory();
        }
    }

    public void start(Player player) {
        if (using.containsKey(player))
            return;

        player.getEquipment().setHelmet(setColor(new ItemStack(Material.LEATHER_HELMET), Color.RED));
        player.getEquipment().setChestplate(setColor(new ItemStack(Material.LEATHER_CHESTPLATE), Color.ORANGE));
        player.getEquipment().setLeggings(setColor(new ItemStack(Material.LEATHER_LEGGINGS), Color.YELLOW));
        player.getEquipment().setBoots(setColor(new ItemStack(Material.LEATHER_BOOTS), Color.GREEN));

        player.updateInventory();

        using.put(player, new DiscoTask(player));
    }

    public boolean isUsing(Player player) {
        return using.containsKey(player);
    }

    public boolean canUse(Player player) {
        return true;
    }

    private static class DiscoTask implements Runnable {

        private boolean alive = true;

        public DiscoTask(Player player) {
            this.player = player;
            run();
        }

        private Player player;
        private int index = 0;
        private int cIndex = 0;

        private final static Color white = Color.BLACK;
        private final static Color[] colors = {Color.RED, Color.ORANGE, Color.YELLOW, Color.GREEN, Color.BLUE, Color.PURPLE};

        public void run() {
            if (!(player.isOnline()) || !alive)
                return;

            ItemStack[] contents = player.getEquipment().getArmorContents();

            Color pick = colors[((int)((cIndex+1)/4) >= colors.length ? cIndex=0 : ((int)(++cIndex/4)))];
            int armorIndex = ((++index) >= 4 ? index=0 : index);

            for (int i = 0; i < 4; i++) {
                contents[i] = setColor(contents[i], white); //white out
            }

            ItemStack equipment = setColor(contents[armorIndex], pick);
            equipment.addEnchantment(Enchantment.PROTECTION_ENVIRONMENTAL, 1);
            contents[armorIndex] = equipment;

            player.getEquipment().setArmorContents(contents);
            player.updateInventory();

            if (alive && player.isOnline())
                Bukkit.getScheduler().scheduleSyncDelayedTask(VCHub.getInstance(), this, 2);
        }
    }

    private static ItemStack setColor(ItemStack content, Color color) {
        LeatherArmorMeta meta = (LeatherArmorMeta)content.getItemMeta();
        if (meta == null || color == null)
            return content;

        meta.setColor(color);
        content.setItemMeta(meta);
        return content;
    }

    public String getName() {
        return "Disco Suit";
    }
}
