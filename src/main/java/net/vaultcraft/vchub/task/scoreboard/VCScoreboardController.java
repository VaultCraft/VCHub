package net.vaultcraft.vchub.task.scoreboard;

import net.vaultcraft.vcutils.scoreboard.*;
import org.bukkit.entity.Player;

/**
 * Created by Connor on 9/2/14. Designed for the VCHub project.
 */

public class VCScoreboardController {

    private VCScoreboard board;
    private VCObjective current;

    public VCScoreboardController(Player player, VCObjective current) {
        board = new VCScoreboard(player);
        this.current = current;
        current.addScoreboardAndDisplay(board, VCDisplay.SIDEBAR);
    }

    public void update(VCObjective objective) {
        this.current = objective;
        objective.addScoreboard(board);
    }

    public VCObjective getCurrentObjective() {
        return current;
    }

    public VCScoreboard getScoreboard() {
        return board;
    }
}
