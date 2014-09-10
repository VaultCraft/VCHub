package net.vaultcraft.vchub.portal;

import net.vaultcraft.vcutils.protection.Area;

/**
 * Created by Connor on 9/9/14. Designed for the VCHub project.
 */

public class Portal {

    private Area area;
    private String destination;

    public Portal(String destination, Area area) {
        this.area = area;
        this.destination = destination;
    }
}
