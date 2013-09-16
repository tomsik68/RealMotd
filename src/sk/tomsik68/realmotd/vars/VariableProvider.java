package sk.tomsik68.realmotd.vars;

import java.util.Map;

import org.bukkit.Server;
import org.bukkit.entity.Player;

public abstract class VariableProvider {
    protected final Server server;

    public VariableProvider(Server server) {
        this.server = server;
    }

    public abstract Map<String, Variable> provide();
}
