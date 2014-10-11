package net.vaultcraft.vchub.perks;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.chat.Prefix;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Connor on 7/26/14. Designed for the VCHub project.
 */

public class PerkHandler implements Listener {

    private LinkedHashMap<Perk, String> perks = new LinkedHashMap<>();
    private LinkedHashMap<Integer, Perk> slots = new LinkedHashMap<>();

    public PerkHandler() {
        perks.put(new DiscoSuitPerk(), "Disco Suit");
        perks.put(new WolfPerk(), "Wolf Companion");
        perks.put(new SlimePerk(), "Slime Cannon");
        perks.put(new PigmanPerk(), "Flamethrower");
        perks.put(new CreeperPerk(), "Party Creeper");
        perks.put(new SkeletonPerk(VCHub.getInstance()), "Skeleton King Bow");
        perks.put(new EndermanPerk(), "Ender Force");
        perks.put(new EnderdragonPerk(), "Ender Dragon");

        Inventory inv = Bukkit.createInventory(null, 18, PerkTitle.PERK_MENU.toString());
        perksMenu = inv;

        int slot = 0;
        for (Perk p : perks.keySet()) {
            inv.setItem(slot, p.getActivatorStack());
            slots.put(slot, p);
            slot++;
        }

        inv.setItem(inv.getSize() - 1, VCItems.build(Material.REDSTONE_BLOCK, "&c&lDisable current perk"));

        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());
    }

    private static Inventory perksMenu;
    private static List<Player> inv_open = Lists.newArrayList();
    private static HashMap<Player, Perk> confirm = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.PHYSICAL))
            return;

        if (player.getItemInHand().equals(VCItems.PICK_PERK)) {
            //open perks menu
            player.openInventory(perksMenu);
            inv_open.add(player);
        } else {
            if (confirm.containsKey(player) && player.getInventory().getHeldItemSlot() == 3) {
                //check perk
                Perk perk = confirm.get(player);
                if (!(perk.canUse(player))) {
                    Form.at(player, Prefix.ERROR, "You cannot use this perk!");
                    return;
                }
                if (perk.isUsing(player))
                    perk.stop(player);
                else
                    perk.start(player);

                if (perk.getActivatorStack().getType() != Material.BOW) {
                    player.setItemInHand(perk.getActivatorStack());
                    player.updateInventory();
                }

                if (perk instanceof DiscoSuitPerk)
                    event.setCancelled(true);
            }
        }
    }

    public static Perk getActivePerk(Player player) {
        return confirm.get(player);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player click = (Player) event.getWhoClicked();
        if (!(inv_open.contains(click)))
            return;

        event.setCancelled(true);
        if (event.getCurrentItem() == null)
            return;

        if (event.getCurrentItem().getType().equals(Material.REDSTONE_BLOCK)) {
            //disable current
            Perk has = confirm.remove(click);
            if (has == null)
                return;

            has.stop(click);
            Form.at(click, "You unequipped your current perk!");
            click.getInventory().setItem(3, new ItemStack(Material.AIR));
            click.getInventory().setItem(3, VCItems.NO_ACTIVE_PERK);
            click.updateInventory();
        }
        Perk perk = slots.get(event.getSlot());
        if (perk == null)
            return;

        if (!(perk.canUse(click))) {
            Form.at(click, Prefix.ERROR, "You do not have permission to use this perk!");
            return;
        }

        click.closeInventory();
        if (confirm.containsKey(click)) {
            Perk currentPerk = confirm.get(click);
            if (currentPerk.isUsing(click))
                currentPerk.stop(click);
        }
        confirm.put(click, perk);
        click.getInventory().setItem(3, perk.getActivatorStack());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        if (confirm.containsKey(event.getPlayer())) {
            Perk perk = confirm.get(event.getPlayer());
            if (perk.isUsing(event.getPlayer()))
                perk.stop(event.getPlayer());
            confirm.remove(event.getPlayer());
        }
    }

    enum PerkTitle {
        PERK_MENU(ChatColor.GREEN + "Perks Menu!");

        String s;

        PerkTitle(String s) {
            this.s = s;
        }

        public String toString() {
            return s;
        }
    }
}