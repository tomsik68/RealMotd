package sk.tomsik68.realmotd.vars;

import org.bukkit.entity.Player;

import sk.tomsik68.realmotd.RealMotd;

public abstract class Variable {
    protected final RealMotd plugin;

    public Variable(final RealMotd plugin) {
        this.plugin = plugin;
    }

    public abstract String getValue(Player player);
}