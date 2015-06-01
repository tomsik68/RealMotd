package sk.tomsik68.realmotd.vars;

import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;

import sk.tomsik68.realmotd.RealMotd;

final class ScoreboardVariable extends Variable {
    private final Objective objective;

    public ScoreboardVariable(RealMotd plugin, Objective objective) {
        super(plugin);
        this.objective = objective;
    }

    @Override
    public String getValue(Player player) {
        Score score = objective.getScore(player);
        if (score != null)
            return Integer.toString(score.getScore());
        else
            return "0";
    }

}
