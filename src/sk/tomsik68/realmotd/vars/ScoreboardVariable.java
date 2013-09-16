package sk.tomsik68.realmotd.vars;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;

import sk.tomsik68.realmotd.RealMotd;

public class ScoreboardVariable extends Variable {
    private final Objective objective;

    public ScoreboardVariable(RealMotd plugin, Objective objective) {
        super(plugin);
        this.objective = objective;
    }

    @Override
    public String getValue(Player player) {
        return Integer.toString(objective.getScore(player).getScore());
    }

}
