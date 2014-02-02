package sk.tomsik68.realmotd.api.groups;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class GroupsRegistry {
    private final HashMap<String, String> playerToGroup = new HashMap<String, String>();
    private final HashMap<String, List<String>> groupToPlayers = new HashMap<String, List<String>>();
    private final File dataFile;

    public GroupsRegistry(File dataFolder) {
        dataFile = new File(dataFolder, "groups.yml");
    }

    public void load() {
        playerToGroup.clear();
        if (dataFile.exists()) {
            YamlConfiguration cfg = YamlConfiguration.loadConfiguration(dataFile);
            ConfigurationSection groupsSection = cfg.getConfigurationSection("groups");
            Set<String> keys = groupsSection.getKeys(false);
            for (String group : keys) {
                List<String> playersInGroup = groupsSection.getConfigurationSection(group).getStringList("members");
                groupToPlayers.put(group, playersInGroup);
                for(String player : playersInGroup){
                    playerToGroup.put(player, group);
                }
            }
        }
    }

    public void save() throws IOException {
        if (!dataFile.exists())
            dataFile.createNewFile();
        YamlConfiguration config = new YamlConfiguration();
        config.createSection("groups");
        config.set("groups", groupToPlayers);
        config.save(dataFile);
        
    }

    public String getGroupName(Player player) {
        // NULL protection
        if(!playerToGroup.containsKey(player.getName()))
            return "";
        return playerToGroup.get(player.getName());
    }

    public void addPlayer(String playerName, String group) {
        playerToGroup.put(playerName, group);
        if(!groupToPlayers.containsKey(group)){
            groupToPlayers.put(group, new ArrayList<String>());
        }
        groupToPlayers.get(group).add(playerName);
    }

    public Collection<String> getGroups() {
        return groupToPlayers.keySet();
    }

}
