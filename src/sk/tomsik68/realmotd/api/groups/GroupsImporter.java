package sk.tomsik68.realmotd.api.groups;

import org.bukkit.Server;

public abstract class GroupsImporter {
    public GroupsImporter() {

    }

    public abstract boolean isAvailable(Server server);

    public abstract void importGroups(Server server, GroupsRegistry groupsRegistry);
}
