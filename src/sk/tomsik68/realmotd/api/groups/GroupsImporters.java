package sk.tomsik68.realmotd.api.groups;

import java.util.Collection;
import java.util.HashMap;

public class GroupsImporters {
    public static final GroupsImporters instance = new GroupsImporters();
    private HashMap<String, GroupsImporter> importers = new HashMap<String, GroupsImporter>();

    private GroupsImporters() {
        importers.put("vault", new VaultGroupsImporter());
    }

    public GroupsImporter getImporter(String name) {
        if (!importers.containsKey(name))
            return null;
        return importers.get(name);
    }

    public void register(String name, GroupsImporter importer) {
        importers.put(name, importer);
    }

    public Collection<String> getImporters() {
        return importers.keySet();
    }
}
