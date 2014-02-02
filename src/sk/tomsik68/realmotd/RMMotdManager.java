package sk.tomsik68.realmotd;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.realmotd.api.EMotdMode;
import sk.tomsik68.realmotd.api.IMotdDecorator;
import sk.tomsik68.realmotd.api.FilesManager;
import sk.tomsik68.realmotd.api.MotdManager;
import sk.tomsik68.realmotd.api.groups.GroupsRegistry;
import sk.tomsik68.realmotd.vars.Variable;
import sk.tomsik68.realmotd.vars.VariablesManager;

public class RMMotdManager implements MotdManager {
    private final ConfigFile config;
    private final GroupsRegistry groups;
    private FilesManager messages;
    private String subdirName;

    public RMMotdManager(ConfigFile cfg, GroupsRegistry groups) {
        this(cfg, groups, "messages");
    }

    public RMMotdManager(ConfigFile cfg, GroupsRegistry groups, String subdirName) {
        this.config = cfg;
        this.groups = groups;
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("RealMotd");
        this.messages = new FilesManager(cfg, plugin.getDataFolder(), subdirName, "motd", "txt");
    }

    public File getMotdFile(Player player, int month, int day) {
        String groupName = groups.getGroupName(player);

        String world = player.getWorld().getName();
        EMotdMode mode = config.getMode();
        return messages.getMotdFile(mode, groupName, world, month, day);
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
            String variableStr = "%".concat(varEntry.getKey()).concat("%");
            if (motd.contains(variableStr)) {
                if (player == null && !varEntry.getValue().requiresPlayer()) {
                    RealMotd.log.warning(String.format("You can't use variable %s[class='%s'] as it requires player.", varEntry.getKey(), varEntry.getValue()));
                } else {
                    motd = motd.replace(variableStr, varEntry.getValue().getValue(player));
                }
            }

        }
        String[] decoratorNames = config.getDecorators();
        for (String decoName : decoratorNames) {
            try {
                IMotdDecorator decorator = MotdDecoratorRegistry.instance.getDecorator(decoName);
                String s = decorator.decorate(motd);
                Validate.notNull(s, "Decorator returned null string.");
                Validate.isTrue(!s.isEmpty(), "Decorator returned an empty string.");
                motd = s;
            } catch (Exception e) {
                RealMotd.log.severe("An error has occured while decorating MOTD:" + decoName + " is probably broken :(");
                e.printStackTrace();
            }
        }
        // Permissions patch
        if (motd.contains(config.getPermissionIdentifier())) {
            for (String string : motd.split(" ")) {
                if (string.contains(config.getPermissionIdentifier()))
                    motd = motd.replace(
                            string,
                            ""
                                    + (player.hasPermission(string.replace(config.getPermissionIdentifier(), "")) ? plugin
                                            .getTranslation("permission.has") : plugin.getTranslation("permission.hasnt")));
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
            motd = "&yellowHello &gray%player%!/n&greenThis is default MOTD of RealMotd by &goldTomsik68/n&redTo change it, go to &gray"
                    + messages.getDefaultMotdFile().getPath();
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
