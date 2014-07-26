package net.vaultcraft.vchub.user;

import net.vaultcraft.vcutils.user.User;
import org.bukkit.Bukkit;

import java.util.HashMap;

/**
 * Created by Connor on 7/24/14. Designed for the VCHub project.
 */

public enum UserPrefs {

    HIDE_PLAYERS(1, false),
    HUB_CHAT(2, true),
    PRIVATE_MESSAGES(3, true),
    FORCE_DAYTIME(4, true);

    private int serialNumber;
    private boolean def;

    private UserPrefs(int sN, boolean def) {
        this.serialNumber = sN;
        this.def = def;
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public boolean getDefault() {
        return def;
    }

    public static HashMap<UserPrefs, Boolean> gatherUserdata(User user) {
        HashMap<UserPrefs, Boolean> map = new HashMap<>();
        HashMap<String, String> userdata = user.getAllUserdata();

        for (UserPrefs keys : values()) {
            if (userdata.containsKey(keys.getSerialNumber()+""))
                map.put(keys, Boolean.valueOf(userdata.get(keys.getSerialNumber()+"")));
            else
                map.put(keys, keys.getDefault());
        }

        return map;
    }
}
