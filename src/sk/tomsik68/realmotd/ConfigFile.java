package sk.tomsik68.realmotd;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public class ConfigFile {
    private FileConfiguration config;
    private final File configFile;

    public ConfigFile(File dataFolder) {
        configFile = new File(dataFolder, "config.yml");
    }

    public void load(Plugin plugin) {
        if (configFile.exists())
            config = YamlConfiguration.loadConfiguration(configFile);
        else {
            config = YamlConfiguration.loadConfiguration(plugin.getResource("defconfig.yml"));
            save();
        }
    }

    public void save() {
        if (!configFile.exists()) {
            try {
                configFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            config.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getTranslation(String key) {
        return config.getString("transl." + key, "<" + key + ">");
    }

    public EMotdMode getMode() {
        return EMotdMode.valueOf(config.getString("motd.mode", EMotdMode.DAILY.name()).toUpperCase());
    }

    public String getPermissionIdentifier() {
        return config.getString("permission");
    }

    public String getCommandIdentifier() {
        return config.getString("command");
    }

    public boolean isGroupSpecific() {
        return config.getBoolean("motd.group-specific");
    }

    public boolean isWorldSpecific() {
        return config.getBoolean("motd.world-specific");
    }

    public ChatColor[] getRainbowColors() {
        List<String> colors = config.getStringList("rainbow-colors");
        if (colors == null) {
            return new ChatColor[] { ChatColor.RED, ChatColor.GREEN, ChatColor.BLUE };
        }
        ChatColor[] result = new ChatColor[colors.size()];
        int i = 0;
        for (String s : colors) {
            ChatColor cc = ChatColor.valueOf(s.toUpperCase());
            result[i] = cc;
            i++;
        }
        return result;
    }

    public int getDelay() {
        return config.getInt("motd.delay", 0);
    }
}