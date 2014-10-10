package net.vaultcraft.vchub.commands;

import net.vaultcraft.vcutils.command.ICommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import net.vaultcraft.vcutils.user.Group;

/**
 * Created by CraftFest on 10/9/2014.
 */
public class VCRules extends ICommand {

    public VCRules(String name, Group permission, String... aliases) {
        super(name, permission, aliases);
    }

    @Override
    public void processCommand(Player player, String[] strings) {
        player.sendMessage(_("&5&l&nVaultCraft Rules"));
        player.sendMessage(_(""));
        player.sendMessage(_("&71. &dListen to all the staff members."));
        player.sendMessage(_("&72. &dKeep strong language to a minimum."));
        player.sendMessage(_("&73. &dDo not advertise or spam the chat."));
        player.sendMessage(_("&74. &dDo not disrespect other players."));
        player.sendMessage(_("&75. &dDo not scam other players."));
        player.sendMessage(_("&76. &dDo not exploit glitches. Report them."));
        player.sendMessage(_("&77. &dDo not use hacked clients or unfair mods."));
    }

    private String _(String str) {
        return ChatColor.translateAlternateColorCodes('&', str);
    }

}
