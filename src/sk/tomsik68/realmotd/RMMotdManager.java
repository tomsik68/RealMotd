package sk.tomsik68.realmotd;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.realmotd.api.MotdManager;
import sk.tomsik68.realmotd.api.groups.Group;
import sk.tomsik68.realmotd.api.groups.GroupsRegistry;
import sk.tomsik68.realmotd.vars.Variable;
import sk.tomsik68.realmotd.vars.VariablesManager;

public class RMMotdManager implements MotdManager {
    private final ConfigFile config;
    private final Random rand;
    private final GroupsRegistry groups;
    private final String subdirName;

    public RMMotdManager(ConfigFile cfg, GroupsRegistry groups) {
        this(cfg, groups, "messages");
    }

    public RMMotdManager(ConfigFile cfg, GroupsRegistry groups, String subdirName) {
        rand = new Random();
        this.config = cfg;
        this.groups = groups;
        this.subdirName = subdirName;
    }

    @Override
    public File getMotdFile(Plugin plugin, EMotdMode mode, String group, String world, int month, int day) {
        List<File> potentialFiles = getPotentialFiles(plugin, mode, group, world, month, day);
        for (File file : potentialFiles) {
            if (file.exists()) {
                return file;
            }
        }
        // backup can be found in oldswitch.txt
        return getDefaultMotdFile();
    }

    private List<File> getPotentialFiles(Plugin plugin, EMotdMode mode, String group, String world, int month, int day) {
        String basePath = plugin.getDataFolder().getAbsolutePath() + File.separator + subdirName;
        List<File> result = new ArrayList<File>();
        if (mode == EMotdMode.DAILY) {
            result.addAll(getDailyMotdFiles(basePath, group, world, month, day));
        } else if (mode == EMotdMode.RANDOM) {
            result.addAll(getRandomMotdFiles(basePath, group, world));
        }
        result.addAll(getSingleMotdFiles(basePath, group, world));
        return result;
    }

    private Collection<? extends File> getRandomMotdFiles(String basePath, String group, String world) {
        List<File> result = new ArrayList<File>();
        if (config.isGroupSpecific()) {
            if (config.isWorldSpecific()) {
                result.addAll(Arrays.asList(new File(getGroupFolder(basePath, group), world).listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(File arg0, String arg1) {
                        return arg1.endsWith(".txt");
                    }
                })));
            }
            result.addAll(Arrays.asList(getGroupFolder(basePath, group).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(".txt");
                }
            })));
        }
        if (config.isWorldSpecific()) {
            result.addAll(Arrays.asList(new File(basePath, world).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    return arg1.endsWith(".txt");
                }
            })));
        }
        result.addAll(Arrays.asList(new File(basePath).listFiles(new FilenameFilter() {

            @Override
            public boolean accept(File arg0, String arg1) {
                return arg1.endsWith(".txt");
            }
        })));
        Collections.shuffle(result, new Random());
        return result;
    }

    private Collection<? extends File> getSingleMotdFiles(String basePath, String group, String world) {
        List<File> result = new ArrayList<File>();
        if (config.isGroupSpecific()) {
            if (config.isWorldSpecific()) {
                result.add(new File(getGroupFolder(basePath, group), world + File.separator + "motd.txt"));
            }
            result.add(new File(getGroupFolder(basePath, group), "motd.txt"));
        }
        if (config.isWorldSpecific()) {
            result.add(new File(basePath, world + File.separator + "motd.txt"));
        }
        result.add(new File(basePath, "motd.txt"));
        return result;

    }

    private Collection<? extends File> getDailyMotdFiles(String basePath, String group, String world, int month, int day) {
        List<File> result = new ArrayList<File>();
        if (config.isGroupSpecific()) {
            if (config.isWorldSpecific()) {
                result.add(new File(getGroupFolder(basePath, group), world + File.separator + "motd_" + month + "_" + day + ".txt"));
            }
            result.add(new File(getGroupFolder(basePath, group), "motd_" + month + "_" + day + ".txt"));
        }
        if (config.isWorldSpecific()) {
            result.add(new File(basePath, world + File.separator + "motd_" + month + "_" + day + ".txt"));
        }
        result.add(new File(basePath, "motd_" + month + "_" + day + ".txt"));
        return result;
    }

    private File getGroupFolder(String basePath, String group) {
        return new File(basePath, group);
    }

    @Override
    public File getMotdFile(Player player, int month, int day) {
        RealMotd plugin = (RealMotd) player.getServer().getPluginManager().getPlugin("RealMotd");
        Group group = groups.getGroup(player);
        String groupName = "";
        if (group != null)
            groupName = group.getName();
        String world = player.getWorld().getName();
        EMotdMode mode = config.getMode();
        return getMotdFile(plugin, mode, groupName, world, month, day);
    }

    public File getDefaultMotdFile() {
        return new File(Bukkit.getServer().getPluginManager().getPlugin("RealMotd").getDataFolder(), subdirName.concat(File.separator).concat("motd.txt"));
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
        // Apply colors patch
        for (ChatColor cc : ChatColor.values()) {
            motd = motd.replace("&" + cc.name().toLowerCase(), cc.toString());
            motd = motd.replace("&" + cc.name(), cc.toString());
            motd = motd.replace("&" + cc.ordinal(), cc.toString());
        }
        // some formatting
        if (motd.contains("&bo")) {
            motd = motd.replaceAll("&bo", ChatColor.BOLD.toString());
        }
        if (motd.contains("&it")) {
            motd = motd.replaceAll("&it", ChatColor.ITALIC.toString());
        }
        if (motd.contains("&str")) {
            motd = motd.replaceAll("&str", ChatColor.STRIKETHROUGH.toString());
        }
        if (motd.contains("&ran")) {
            motd = motd.replaceAll("&ran", ChatColor.MAGIC.toString());
        }
        if (motd.contains("&un")) {
            motd = motd.replaceAll("&un", ChatColor.UNDERLINE.toString());
        }
        if (motd.contains("&no")) {
            motd = motd.replaceAll("&no", ChatColor.RESET.toString());
        }
        // rainbow!
        if (motd.contains("&rbow")) {
            while (motd.contains("&rbow")) {
                int index = motd.indexOf("&rbow") + 5;
                int endIndex = motd.indexOf(ChatColor.RESET.toString(), index);
                if (endIndex < 0) {
                    endIndex = motd.length();
                }
                String substr = motd.substring(index, endIndex);
                StringBuilder replacement = new StringBuilder();
                ChatColor[] rainbowColors = config.getRainbowColors();
                for (int i = 0; i < substr.length(); ++i) {
                    char c = substr.charAt(i);
                    // don't break my newlines!!!
                    if (c == '/' && i <= substr.length() - 2 && substr.charAt(i + 1) == 'n') {
                        i++;
                        replacement = replacement.append("/n");
                        continue;
                    }
                    replacement = replacement.append(rainbowColors[rand.nextInt(rainbowColors.length)].toString()).append(c);
                }
                replacement = replacement.append(ChatColor.RESET.toString());
                motd = motd.replace("&rbow".concat(substr).concat(ChatColor.RESET.toString()), replacement.toString());
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
                getDefaultMotdFile().createNewFile();
                PrintWriter pw = new PrintWriter(new FileWriter(getDefaultMotdFile()));
                pw.println("&yellowHello %player%!");
                pw.println("&greenThis is default MOTD of RealMotd by &goldTomsik68");
                pw.println("&redTo change it, go to &gray" + getDefaultMotdFile().getPath());
                pw.flush();
                pw.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            motd = "&yellowHello &gray%player%!/n&greenThis is default MOTD of RealMotd by &goldTomsik68/n&redTo change it, go to &gray" + getDefaultMotdFile().getPath();
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
        Bukkit.broadcastMessage(relativePath.toString());
        Util.writeFile(dest, motd);
    }

}
