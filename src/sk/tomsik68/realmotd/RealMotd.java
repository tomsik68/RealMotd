package sk.tomsik68.realmotd;

import java.io.File;
import java.util.List;
import java.util.logging.Logger;

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
import sk.tomsik68.realmotd.api.groups.GroupsRegistry;
import sk.tomsik68.realmotd.vars.VariablesManager;

/**
 * RealMotd for Bukkit
 * 
 * @author Tomsik68
 */
public class RealMotd extends JavaPlugin implements Listener {

    public static MotdManager handler;
    public static Logger log;
    private ConfigFile cfg;
    private GroupsRegistry groups;

    public RealMotd() {
        super();
    }

    @Override
    public void onEnable() {
        log = getLogger();
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
        VariablesManager.instance.initDefaultVariables(this);
    }

    public String getTranslation(String key) {
        return cfg.getTranslation(key);
    }

    public void sendMotd(Player player) {
        handler.sendMotd(player);
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("realmotd") && sender.isOp()) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    cfg.load(this);
                    groups.load();
                } else if (args[0].equalsIgnoreCase("groups")) {
                    List<Group> gs = groups.getGroups();
                    StringBuilder sb = new StringBuilder();
                    for (Group g : gs) {
                        sb = sb.append(g.getName()).append(',');
                    }
                    if (sb.length() > 0) {
                        sb = sb.deleteCharAt(sb.length() - 1);
                    }
                    sender.sendMessage("Groups: " + sb.toString());
                } else if (args[0].equalsIgnoreCase("set")) {
                    String world = "";
                    String group = "";
                    int month = -1, day = -1;
                    int i;
                    for (i = 1; i < args.length && !args[i].equalsIgnoreCase(";"); ++i) {
                        String arg = args[i];
                        if (arg.equalsIgnoreCase("-month") && i + 1 < args.length) {
                            if (isInt(args[i + 1])) {
                                month = getInt(args[i + 1]);
                            }
                        } else if (arg.equalsIgnoreCase("-day") && i + 1 < args.length) {
                            if (isInt(args[i + 1])) {
                                day = getInt(args[i + 1]);
                            }
                        } else if (arg.equalsIgnoreCase("-world") && i + 1 < args.length) {
                            world = args[i + 1];
                        } else if (arg.equalsIgnoreCase("-group") && i + 1 < args.length) {
                            group = args[i + 1];
                        }
                    }
                    i += 1;
                    StringBuilder sb = new StringBuilder();
                    for (; i < args.length; ++i) {
                        sb = sb.append(args[i]).append(' ');
                    }
                    if (sb.length() > 0)
                        sb = sb.deleteCharAt(sb.length() - 1);
                    try {
                        handler.setMOTD(sb.toString().split("/n"), world, group, month, day);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return true;
            } else
                return false;
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

    private int getInt(String string) {
        return Integer.parseInt(string);
    }

    private boolean isInt(String string) {
        try {
            Integer.parseInt(string);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (cfg.getDelay() == 0)
            sendMotd(event.getPlayer());
        else if (cfg.getDelay() > 0) {
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    sendMotd(event.getPlayer());
                }
            }, cfg.getDelay());
        }
    }
}
