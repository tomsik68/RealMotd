package sk.tomsik68.realmotd;

import org.bukkit.entity.Player;

public abstract class Variable {
    protected final RealMotd plugin;

    public Variable(final RealMotd plugin) {
        this.plugin = plugin;
    }

    public abstract String getValue(Player player);
}