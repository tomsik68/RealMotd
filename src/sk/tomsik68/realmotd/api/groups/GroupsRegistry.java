package sk.tomsik68.realmotd.api.groups;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;


public class GroupsRegistry {
    private final List<Group> groups = new ArrayList<Group>();
    private final File dataFile;

    public GroupsRegistry(File dataFolder) {
        dataFile = new File(dataFolder, "groups.yml");
    }

    public void load() {
        groups.clear();
        if (dataFile.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
            Set<String> keys = cfg.getConfigurationSection("groups").getKeys(false);
            for (String g : keys) {
                Group group = new Group(cfg.getConfigurationSection("groups." + g));
                groups.add(group);
            }
        }
    }
    public void save() throws IOException{
        if(!dataFile.exists())
            dataFile.createNewFile();
        YamlConfiguration config = new YamlConfiguration();
        config.createSection("groups");
        for(Group g : groups){
            config.createSection("groups."+g.getName());
            config.set("groups."+g.getName()+".members", new ArrayList<String>(g.getMembers()));
        }
        config.save(dataFile);
    }

    public Group getGroup(Player player) {
        for (Group g : groups) {
            if (g.has(player.getName())) {
                return g;
            }
        }
        
        return null;
    }

    public List<Group> getGroups() {
        return groups;
    }

}
