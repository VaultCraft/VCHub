package net.vaultcraft.vchub.listener;

import com.google.common.collect.Lists;
import net.vaultcraft.vchub.VCHub;
import net.vaultcraft.vchub.VCItems;
import net.vaultcraft.vchub.handler.PlayerVisibilityHandler;
import net.vaultcraft.vchub.user.UserPrefs;
import net.vaultcraft.vcutils.VCUtils;
import net.vaultcraft.vcutils.item.inventory.InventoryBuilder;
import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.List;

/**
 * Created by Connor on 7/24/14. Designed for the VCHub project.
 */

public class MicroPlugin_PrefsHandler implements Listener {

    /**
     * Inventory format:
     *
     * " a  b  c  d "
     * "            "
     * " w  x  y  z "
     *
     */
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, VCHub.getInstance());
    }

    public void onDisable() {
        for (Player open : open_inv) {
            open.closeInventory();
        }
    }

    private static List<Player> open_inv = Lists.newArrayList();

    private static final ItemStack chatToggleIco = VCItems.build(Material.SIGN, "&cToggle chat");
    private static final ItemStack privateMessageIco = VCItems.build(Material.PAPER, "&cToggle private messages");
    private static final ItemStack torchON = VCItems.build(Material.REDSTONE_TORCH_ON, "&cToggle player visibility");
    private static final ItemStack torchOFF = VCItems.build(Material.LEVER, "&cToggle player visibility");
    private static final ItemStack pumpkinLIT = VCItems.build(Material.JACK_O_LANTERN, "&cToggle daylight");
    private static final ItemStack pumpkinOFF = VCItems.build(Material.PUMPKIN, "&cToggle daylight");

    @EventHandler
    public void onPlayerInteractEvent(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) || event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            if (!(player.getItemInHand().equals(VCItems.USER_SETTINGS)))
                return;

            User user = User.fromPlayer(player);
            HashMap<UserPrefs, Boolean> prefs = UserPrefs.gatherUserdata(user);

            event.setCancelled(true);
            InventoryBuilder builder = new InventoryBuilder();
            builder.shape(new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " "},
                    new String[]{" ", "a", " ", "b", " ", "c", " ", "d", " "},
                    new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " "},
                    new String[]{" ", "w", " ", "x", " ", "y", " ", "z", " "},
                    new String[]{" ", " ", " ", " ", " ", " ", " ", " ", " "});

            builder.setChar('a', (prefs.get(UserPrefs.HIDE_PLAYERS) ? torchON : torchOFF));
            builder.setChar('b', chatToggleIco);
            builder.setChar('c', privateMessageIco);
            builder.setChar('d', (prefs.get(UserPrefs.FORCE_DAYTIME) ? pumpkinLIT : pumpkinOFF));

            builder.setChar('w', fromBoolean(prefs.get(UserPrefs.HIDE_PLAYERS)));
            builder.setChar('x', fromBoolean(prefs.get(UserPrefs.HUB_CHAT)));
            builder.setChar('y', fromBoolean(prefs.get(UserPrefs.PRIVATE_MESSAGES)));
            builder.setChar('z', fromBoolean(prefs.get(UserPrefs.FORCE_DAYTIME)));

            Inventory open = builder.build("&cUser Settings...");
            player.openInventory(open);
            open_inv.add(player);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        //save prefs
        if (open_inv.contains(event.getPlayer())) {
            Player player = (Player)event.getPlayer();

            User user = User.fromPlayer(player);
            for (UserPrefs pref : UserPrefs.values()) {
                for (int slot : represents.keySet()) {
                    user.addUserdata(represents.get(slot).getSerialNumber()+"", fromStack(event.getInventory().getItem(slot))+"");
                }
            }
            open_inv.remove(player);
        }
    }

    private static HashMap<Integer, UserPrefs> represents = new HashMap<>();
    static {
        represents.put(28, UserPrefs.HIDE_PLAYERS);
        represents.put(30, UserPrefs.HUB_CHAT);
        represents.put(32, UserPrefs.PRIVATE_MESSAGES);
        represents.put(34, UserPrefs.FORCE_DAYTIME);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (open_inv.contains(player)) {
            //check clicked item
            if (represents.containsKey(event.getSlot())) {
                ItemStack stack = event.getCurrentItem();
                boolean turnOff = (stack.getData().getData() == (byte)10);
                if (turnOff) {
                    ItemStack create = fromBoolean(false);
                    event.getInventory().setItem(event.getSlot(), create);
                } else {
                    ItemStack create = fromBoolean(true);
                    event.getInventory().setItem(event.getSlot(), create);
                }

                if (event.getSlot() == 28) {
                    event.getInventory().setItem(10, (turnOff ? torchOFF : torchON));
                } else if (event.getSlot() == 34) {
                    event.getInventory().setItem(16, (turnOff ? pumpkinOFF : pumpkinLIT));
                }

                User user = User.fromPlayer(player);
                switch (represents.get(event.getSlot())) {
                    case HIDE_PLAYERS:
                        PlayerVisibilityHandler.getInstance().update(user);
                        break;
                    case HUB_CHAT:
                        user.setChatVisible(!turnOff);
                        break;
                    case PRIVATE_MESSAGES:
                        user.setPrivateMessaging(!turnOff);
                        break;
                    case FORCE_DAYTIME:
                        user.addUserdata(UserPrefs.FORCE_DAYTIME.getSerialNumber()+"", (!turnOff)+"");
                }
            }
            event.setCancelled(true);
        }
    }

    private static String valueOf(boolean bool) {
        return bool ? "on" : "off";
    }

    private static ItemStack fromBoolean(boolean value) {
        ItemStack onOff = new ItemStack(Material.INK_SACK, 1, (short)0, (byte)(value ? 10 : 8));
        ItemMeta meta = onOff.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', (value ? "&aOn" : "&cOff")));
        onOff.setItemMeta(meta);
        return onOff;
    }

    private static boolean fromStack(ItemStack stack) {
        byte data = stack.getData().getData();
        return (data == (byte)10 ? true : false);
    }
}
