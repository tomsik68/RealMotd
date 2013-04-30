package sk.tomsik68.realmotd;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import sk.tomsik68.realmotd.api.MotdManager;
import sk.tomsik68.realmotd.api.PiiMotdService;

/**
 * RealMotd for Bukkit
 * 
 * @author Tomsik68
 */
public class RealMotd extends JavaPlugin implements Listener {

    public static MotdManager handler;

    public RealMotd() {
        super();
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            try {
                getDataFolder().mkdir();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File file = new File(getDataFolder(), "messages");
        if (!file.exists())
            file.mkdir();
        if (!(new File(getDataFolder(), "config.yml").exists())) {
            System.out.println("[RealMotd] Configuration file not found. Generating a new one...");
            setConfigDefaults();
        }
        YamlConfiguration config = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "config.yml"));
        handler = new RMMotdManager(EMotdMode.valueOf(config.getString("motd.mode").toUpperCase()));
        PluginManager pm = getServer().getPluginManager();
        // Event registration
        // NOTE: All events are unregistered automatically after the plugin is
        // disabled
        pm.registerEvents(this, this);
        PluginDescriptionFile pdfFile = this.getDescription();
        getCommand("motd").setExecutor(this);
        // If we have PII...
        try {
            Class.forName("sk.tomsik68.pii.PIIPlugin");
            getServer().getServicesManager().register(PiiMotdService.class, new PiiMotdService(handler), this, ServicePriority.Normal);
            System.out.println("[RealMotd] Detected PII. Registered service. Thanks for using PII!");
        } catch (Exception e) {
        }
        System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
    }

    private void setConfigDefaults() {
        StringBuilder colors = new StringBuilder("Available colors:");
        for (ChatColor cc : ChatColor.values()) {
            colors = colors.append(cc.name().toLowerCase()).append(",");
        }
        colors.deleteCharAt(colors.length() - 1);
        YamlConfiguration config = new YamlConfiguration();
        config.options().header(colors.toString());
        // timestat translation
        config.set("transl.time.morning", "Morning");
        config.set("transl.time.day", "Day");
        config.set("transl.time.evening", "Evening");
        config.set("transl.time.night", "Night");
        // weather translation
        config.set("transl.weather.raining", "Raining");
        config.set("transl.weather.clear", "Clear");
        // mode translation
        config.set("transl.mode.creative", "Creative");
        config.set("transl.mode.survival", "Survival");
        // environment translation
        config.set("transl.env.nether", "Nether");
        config.set("transl.env.skylands", "Skylands");
        config.set("transl.env.normal", "Normal");
        config.set("transl.env.the_end", "The End");
        // difficulty translation
        config.set("transl.diff.peaceful", "Peaceful");
        config.set("transl.diff.easy", "Easy");
        config.set("transl.diff.normal", "Normal");
        config.set("transl.diff.hard", "Hard");
        // OP translation
        config.set("transl.op.is", "Operator");
        config.set("transl.op.isnt", "Player");
        // flight
        config.set("transl.flight.allowed", "can");
        config.set("transl.flight.denied", "can't");
        // nether
        config.set("transl.nether.allowed", "nether is allowed");
        config.set("transl.nether.denied", "denied");
        // permission translation
        config.set("transl.permission.has", "can");
        config.set("transl.permission.hasnt", "cant");
        config.set("permission", "^");
        // commands
        config.set("transl.comm.fail", "Command failed");
        config.set("command", ">");
        // some props
        config.set("motd.world-specific", false);
        config.set("motd.group-specific", false);
        config.set("motd.mode", "single");
        try {
            config.save(new File(getDataFolder(), "config.yml"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onDisable() {
        System.out.println(this.getDescription().getName() + " is disabled!");
    }

    public String getTranslation(String key) {
        return getProperty("transl.".concat(key), "<undefined>");
    }

    public String getProperty(String key, String def) {
        return getConfig().getString(key, def);
    }

    public void sendMotd(Player player) {
        handler.sendMotd(player);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        //setting default motd
        if (args.length > 0 && (sender.hasPermission("rm.set") || sender.isOp()) && sender instanceof Player) {
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++) {
                sb = sb.append(args[i]).append(' ');
            }
            Util.writeFile(handler.getMotdFile((Player) sender, -1, -1, false, false), sb.toString().split("/n"));
            return true;
        } else if (!sender.hasPermission("rm.set") && args.length > 0) {
            sender.sendMessage(ChatColor.RED + "[RealMotd] You need permission to set motd in-game.");
            return true;
        } else if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can set MOTD.");
        }
        // setting day & month-specific motd
        if (args.length > 3 && sender instanceof Player) {
            StringBuilder sb = new StringBuilder();
            for (int i = 4; i < args.length; i++) {
                sb = sb.append(args[i]).append(' ');
            }
            Util.writeFile(handler.getMotdFile((Player) sender, Util.getInt(args[1]), Util.getInt(args[2]), getConfig().getBoolean("motd.world-specific", false), getConfig().getBoolean("motd-group-specific", false)), sb.toString().split("/n"));
            sender.sendMessage(ChatColor.GREEN + "[RealMotd] MOTD was set.");
            return true;
        }
        if (sender instanceof Player) {

            sendMotd((Player) sender);
        } else {
            sender.sendMessage(ChatColor.RED + "[RealMotd] MOTD can only be sent to players now. Only players can set MOTD.");
        }
        return true;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(PlayerJoinEvent event) {
        sendMotd(event.getPlayer());
    }
}
