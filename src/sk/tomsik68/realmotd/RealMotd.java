package sk.tomsik68.realmotd;

import java.io.File;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import sk.tomsik68.realmotd.api.MotdManager;
import sk.tomsik68.realmotd.api.PiiMotdService;
import sk.tomsik68.realmotd.api.groups.Group;

/**
 * RealMotd for Bukkit
 * 
 * @author Tomsik68
 */
public class RealMotd extends JavaPlugin implements Listener {

    public static MotdManager handler;
    private ConfigFile cfg;
    private GroupsRegistry groups;

    public RealMotd() {
        super();
    }

    @Override
    public void onEnable() {
        cfg = new ConfigFile(getDataFolder());
        groups = new GroupsRegistry(getDataFolder());
        if (!getDataFolder().exists()) {
            try {
                if (!getDataFolder().mkdir())
                    Runtime.getRuntime().exec("mkdir " + getDataFolder().getAbsolutePath());
            } catch (Exception e) {
                getLogger().severe("Directory for RealMotd could not be created:");
                e.printStackTrace();
            }
        }
        File file = new File(getDataFolder(), "messages");
        if (!file.exists())
            file.mkdir();
        cfg.load(this);
        groups.load();
        handler = new RMMotdManager(cfg, groups);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        getCommand("realmotd").setExecutor(this);
        getCommand("motd").setExecutor(this);
        // If we have PII...
        try {
            Class.forName("sk.tomsik68.pii.PIIPlugin");
            getServer().getServicesManager().register(PiiMotdService.class, new PiiMotdService(handler), this, ServicePriority.Normal);
            getLogger().info("PII service registered.");
        } catch (Exception e) {
        }
    }

    public String getTranslation(String key) {
        return cfg.getTranslation(key);
    }

    public void sendMotd(Player player) {
        handler.sendMotd(player);
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("realmotd")) {
            if (args.length == 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    cfg.load(this);
                }else if(args[0].equalsIgnoreCase("groups")){
                    List<Group> gs = groups.getGroups();
                    StringBuilder sb = new StringBuilder();
                    for(Group g : gs){
                        sb = sb.append(g.getName()).append(',');
                    }
                    if(sb.length() > 0){
                        sb = sb.deleteCharAt(sb.length() - 1);
                    }
                    sender.sendMessage("Groups: "+sb.toString());
                }
            }
            return true;
        } else if (command.getName().equalsIgnoreCase("motd")) {
            if (sender instanceof Player) {
                sendMotd((Player) sender);
            } else {
                sender.sendMessage(ChatColor.RED + "[RealMotd] MOTD can only be sent to players now.");
            }
            return true;
        }
        return false;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMotd(event.getPlayer());
    }
}
