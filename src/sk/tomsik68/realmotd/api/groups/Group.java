package sk.tomsik68.realmotd.api.groups;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;

public class Group {
    private final String name;
    private final HashSet<String> players = new HashSet<String>();

    public Group(String name) {
        this.name = name;
    }

    public Group(ConfigurationSection cs) {
        name = cs.getName();
        if (cs.get("members") != null) {
            players.addAll(cs.getStringList("members"));
        }
    }

    public boolean has(String pl) {
        return players.contains(pl);
    }

    public String getName() {
        return name;
    }

    public Set<String> getMembers() {
        return players;
    }
}
