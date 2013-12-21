package sk.tomsik68.realmotd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.realmotd.api.IMotdDecorator;
import sk.tomsik68.realmotd.api.MessagesManager;
import sk.tomsik68.realmotd.api.MotdManager;
import sk.tomsik68.realmotd.api.groups.Group;
import sk.tomsik68.realmotd.api.groups.GroupsRegistry;
import sk.tomsik68.realmotd.vars.Variable;
import sk.tomsik68.realmotd.vars.VariablesManager;

public class RMMotdManager implements MotdManager {
    private final ConfigFile config;
    private final Random rand;
    private final GroupsRegistry groups;
    private MessagesManager messages;
    private String subdirName;

    public RMMotdManager(ConfigFile cfg, GroupsRegistry groups) {
        this(cfg, groups, "messages");
    }

    public RMMotdManager(ConfigFile cfg, GroupsRegistry groups, String subdirName) {
        rand = new Random();
        this.config = cfg;
        this.groups = groups;
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("RealMotd");
        this.messages = new MessagesManager(cfg, plugin.getDataFolder(), subdirName);
    }

    public File getMotdFile(Player player, int month, int day) {
        RealMotd plugin = (RealMotd) player.getServer().getPluginManager().getPlugin("RealMotd");
        Group group = groups.getGroup(player);
        String groupName = "";
        if (group != null)
            groupName = group.getName();
        String world = player.getWorld().getName();
        EMotdMode mode = config.getMode();
        return messages.getMotdFile(plugin, mode, groupName, world, month, day);
    }

    @Override
    public void sendMotd(Player player) {
        RealMotd plugin = (RealMotd) player.getServer().getPluginManager().getPlugin("RealMotd");
        int month = Calendar.getInstance().get(Calendar.MONTH) + 1;
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        String motd = addVariables(getMotd(player, month, day), player, plugin);
        try {
            // code required PII, so I changed it to reflection... :(
            @SuppressWarnings("unchecked")
            Class<? extends Event> clazz = (Class<? extends Event>) Class.forName("sk.tomsik68.pii.event.MotdSendEvent");
            Event mse = clazz.getConstructor(CommandSender.class, String[].class).newInstance(player, motd.split("\n"));
            plugin.getServer().getPluginManager().callEvent(mse);
            if (((Cancellable) mse).isCancelled())
                return;
            player.sendMessage((String[]) clazz.getMethod("getMotd").invoke(mse));
        } catch (Exception ee) {
            player.sendMessage(motd.split("/n"));
        }
    }

    @Override
    public String addVariables(String motd, Player player, RealMotd plugin) {
        HashMap<String, Variable> variables = VariablesManager.instance.getVariables();

        // Swap properties at first, so translations work with colors,
        for (Entry<String, Variable> varEntry : variables.entrySet()) {
            if (motd.contains("%".concat(varEntry.getKey()).concat("%")))
                motd = motd.replace("%" + varEntry.getKey() + "%", varEntry.getValue().getValue(player));
        }
        Iterable<IMotdDecorator> decorators = MotdDecoratorRegistry.instance.getDecorators();
        for (IMotdDecorator decorator : decorators) {
            try {
                motd = decorator.decorate(motd);
            } catch (Exception e) {
                RealMotd.log.severe("An error has occured while decorating MOTD:" + decorator.getClass().getName() + " is probably broken :(");
                e.printStackTrace();
            }
        }
        // Permissions patch
        if (motd.contains(config.getPermissionIdentifier())) {
            for (String string : motd.split(" ")) {
                if (string.contains(config.getPermissionIdentifier()))
                    motd = motd.replace(string, "" + (player.hasPermission(string.replace(config.getPermissionIdentifier(), "")) ? plugin.getTranslation("permission.has") : plugin.getTranslation("permission.hasnt")));
            }
        }
        // Commands patch
        if (motd.contains(config.getCommandIdentifier())) {
            for (String string : motd.split(" ")) {
                if (string.contains(config.getCommandIdentifier())) {
                    FakeCommandSender fcs = new FakeCommandSender(player);
                    String result = "";
                    if (Bukkit.dispatchCommand(fcs, string.replace(config.getCommandIdentifier(), ""))) {
                        result = fcs.getText();
                    } else {
                        result = plugin.getTranslation("comm.fail");
                    }

                    motd = motd.replace(string, result);
                }
            }
        }

        return motd;
    }

    @Override
    public String getMotd(Player player, int month, int day) {
        String motd = "";
        File dest = getMotdFile(player, month, day);
        if (!dest.exists()) {
            try {
                messages.getDefaultMotdFile().createNewFile();
                PrintWriter pw = new PrintWriter(new FileWriter(messages.getDefaultMotdFile()));
                pw.println("&yellowHello %player%!");
                pw.println("&greenThis is default MOTD of RealMotd by &goldTomsik68");
                pw.println("&redTo change it, go to &gray" + messages.getDefaultMotdFile().getPath());
                pw.flush();
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            motd = "&yellowHello &gray%player%!/n&greenThis is default MOTD of RealMotd by &goldTomsik68/n&redTo change it, go to &gray" + messages.getDefaultMotdFile().getPath();
        } else {
            motd = Util.readFile(dest);
        }
        return motd;
    }

    @Override
    public EMotdMode getMode() {
        return config.getMode();
    }

    @Override
    public void setMOTD(String[] motd, String world, String group, int month, int day) throws IOException {
        StringBuilder relativePath = new StringBuilder(subdirName).append(File.separator);
        if (group.length() > 0) {
            relativePath = relativePath.append(group).append(File.separator);
        }
        if (world.length() > 0) {
            relativePath = relativePath.append(world).append(File.separator);
        }
        relativePath = relativePath.append("motd");
        if (month != -1 && day != -1) {
            relativePath = relativePath.append("_").append(month).append('_').append(day);
        }
        relativePath = relativePath.append(".txt");
        Plugin pl = Bukkit.getPluginManager().getPlugin("RealMotd");
        File dest = new File(pl.getDataFolder(), relativePath.toString());
        if (!dest.exists()) {
            dest.mkdirs();
            dest.delete();
            dest.createNewFile();
        }
        Util.writeFile(dest, motd);
    }

}
