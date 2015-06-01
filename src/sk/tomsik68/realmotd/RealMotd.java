package sk.tomsik68.realmotd;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import fr.xephi.authme.AuthMe;
import fr.xephi.authme.api.API;
import fr.xephi.authme.events.LoginEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.java.JavaPlugin;

import sk.tomsik68.realmotd.api.MotdManager;
import sk.tomsik68.realmotd.api.PiiMotdService;
import sk.tomsik68.realmotd.api.groups.GroupsRegistry;
import sk.tomsik68.realmotd.decor.RainbowDecorator;
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
        File messagesDir = new File(getDataFolder(), "messages");
        if (!messagesDir.exists())
            messagesDir.mkdir();
        cfg.load(this);

        groups.load();
        handler = new RMMotdManager(cfg, groups);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);

        RealMotdCommand cmdExec = new RealMotdCommand(this);
        getCommand("realmotd").setExecutor(cmdExec);
        getCommand("motd").setExecutor(cmdExec);

        // If we have PII...
        try {
            Class.forName("sk.tomsik68.pii.PIIPlugin");
            getServer().getServicesManager().register(PiiMotdService.class, new PiiMotdService(handler), this, ServicePriority.Normal);
            getLogger().info("PII service registered.");
        } catch (Exception e) {
        }
        VariablesManager.instance.reloadVariables(this);

        // look for authme
        try {
            if (cfg.isAuthMeEnabled()) {
                AuthMe authMe = API.hookAuthMe();
            }
        } catch(Exception e) {
            log.warning("authme-wait-login is set to true, but AuthMe was not detected.");
            log.warning("Your MOTD will not work properly!");
        }

        MotdDecoratorRegistry.instance.register("rainbow",new RainbowDecorator(cfg.getRainbowColors()));
    }

    @Override
    public void onDisable() {
        try {
            log.info("Saving groups...");
            groups.save();
            log.info("OK.");
        } catch (IOException e) {
            log.severe("Could not save groups: ");
            e.printStackTrace();
        }
    }

    public String getTranslation(String key) {
        return cfg.getTranslation(key);
    }

    public void sendMotd(Player player) {
        handler.sendMotd(player);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        sendMessage(event.getPlayer());

    }

    private void sendMessage(final Player player) {
        int delay = cfg.getDelay() * 20;
        if (delay <= 0)
            sendMotd(player);
        else if (delay > 0) {
            getServer().getScheduler().runTaskLater(this, new Runnable() {
                @Override
                public void run() {
                    sendMotd(player);
                }
            }, delay);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerSwitchWorld(final PlayerTeleportEvent event) {
        if (cfg.isWorldSpecific() && !event.getFrom().getWorld().equals(event.getTo().getWorld())) {
            sendMessage(event.getPlayer());
        }
    }

    public void loadConfig() {
        cfg.load(this);
    }

    public GroupsRegistry getGroupRegistry() {
        return groups;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerAuthMeLogin(final LoginEvent event) {
        if (cfg.isAuthMeEnabled())
            sendMessage(event.getPlayer());
    }
}