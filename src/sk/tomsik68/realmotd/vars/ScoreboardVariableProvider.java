package sk.tomsik68.realmotd.vars;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.Server;
import org.bukkit.scoreboard.Objective;

import sk.tomsik68.realmotd.RealMotd;

public class ScoreboardVariableProvider extends VariableProvider {

    public ScoreboardVariableProvider(Server server) {
        super(server);
    }

    @Override
    public Map<String, Variable> provide() {
        Map<String, Variable> result = new HashMap<String, Variable>();
        Set<Objective> objectives = server.getScoreboardManager().getMainScoreboard().getObjectives();
        for (Objective objective : objectives) {
            result.put("score_".concat(objective.getName()), new ScoreboardVariable((RealMotd) server.getPluginManager().getPlugin("RealMotd"),objective));
        }
        return result;
    }

}
