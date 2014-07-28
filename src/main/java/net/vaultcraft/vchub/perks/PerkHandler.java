package net.vaultcraft.vchub.perks;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vcutils.chat.Form;
import net.vaultcraft.vcutils.chat.Prefix;
import net.vaultcraft.vcutils.item.inventory.Menu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 7/26/14. Designed for the VCHub project.
 */

public class PerkHandler implements Listener {

    private HashMap<Perk, String> perks = new HashMap<>();
    private HashMap<Integer, Perk> slots = new HashMap<>();

    public PerkHandler() {
        perks.put(new SilverfishPerk(), "Silverfish Hat");

        Inventory inv = Bukkit.createInventory(null, (int)((double)perks.size()/9.0)+9, PerkTitle.PERK_MENU.toString());
        perksMenu = new Menu(inv);

        int slot = 0;
        for (Perk p : perks.keySet()) {
            inv.setItem(slot, p.getActivatorStack());
            slots.put(slot, p);
            slot++;
        }

        for (Perk key : perks.keySet()) {
            String title = perks.get(key);
            Menu make = new Menu(getAcceptInventory(key));
            make.setParent(perksMenu);
            perksMenu.addChild(title, make);
        }
        inv.setItem(inv.getSize()-1, VCItems.build(Material.REDSTONE_BLOCK, "&c&lDisable current perk"));

        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());
    }

    static ItemStack ACCEPT = VCItems.build(Material.EMERALD_BLOCK, "&a&lACCEPT");
    static ItemStack DENY = VCItems.build(Material.REDSTONE_BLOCK, "&c&lCANCEL");

    private static Inventory getAcceptInventory(Perk perk) {
        Inventory make = Bukkit.createInventory(null, 9*6, PerkTitle.EQUIP_PERK.toString());
        for (int i = 27; i <= 45; i+=9) {
            for (int x = 0; x < 3; x++) {
                make.setItem(i+x, ACCEPT);
                make.setItem(i+x+6, DENY);
            }
        }
        make.setItem(13, perk.getActivatorStack().clone());
        return make;
    }

    private static Menu perksMenu;
    private static HashMap<Player, Menu> open = new HashMap<>();
    private static HashMap<Player, Perk> confirm = new HashMap<>();

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getItemInHand().equals(VCItems.PICK_PERK)) {
            //open perks menu
            player.openInventory(perksMenu.getActual());
            open.put(player, perksMenu);
        } else {
            if (confirm.containsKey(player) && player.getInventory().getHeldItemSlot() == 3) {
                //check perk
                Perk perk = confirm.get(player);
                if (!(perk.canUse(player))) {
                    Form.at(player, Prefix.ERROR, "You cannot use this perk!");
                    return;
                }

                perk.start(player);
            }
        }
    }

    public static Perk getActivePerk(Player player) {
        return confirm.get(player);
    }

    @EventHandler
    public void onInvClick(InventoryClickEvent event) {
        Player click = (Player)event.getWhoClicked();
        if (open.containsKey(click)) {
            if (open.get(click).getActual().getTitle().equals(PerkTitle.EQUIP_PERK.toString())) {
                event.setCancelled(true);
                Perk perk = confirm.get(click);
                if (perk == null)
                    return;

                Material clicked = event.getCurrentItem().getType();
                if (clicked.equals(Material.REDSTONE_BLOCK)) {
                    open.remove(click);
                    confirm.remove(click);
                    click.closeInventory();
                    click.openInventory(perksMenu.getActual());
                    open.put(click, perksMenu);
                    return;
                } else if (clicked.equals(Material.EMERALD_BLOCK)) {
                    open.remove(click);
                    click.closeInventory();
                    click.getInventory().setItem(3, perk.getActivatorStack());
                    click.updateInventory();
                    Form.at(click, "You equipped the &e"+perks.get(perk)+ Prefix.VAULT_CRAFT.getChatColor()+" perk!");
                    return;
                }
            } else if (open.get(click).getActual().getTitle().equals(PerkTitle.PERK_MENU.toString())) {
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
                Menu child = open.remove(click).getChild(perks.get(perk));
                click.openInventory(child.getActual());
                open.put(click, child);
                confirm.put(click, perk);
            }
        }
    }

    enum PerkTitle {
        EQUIP_PERK(ChatColor.GREEN+"Equip perk?"),
        PERK_MENU(ChatColor.GREEN+"Perks menu!");

        String s;
        PerkTitle(String s) { this.s = s; }
        public String toString() {
            return s;
        }
    }
}