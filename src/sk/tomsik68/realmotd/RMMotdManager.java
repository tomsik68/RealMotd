package sk.tomsik68.realmotd;

import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Difficulty;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.realmotd.api.MotdManager;
import sk.tomsik68.realmotd.api.groups.Group;

public class RMMotdManager implements MotdManager {
    private final ConfigFile config;
    private final Random rand;
    private final GroupsRegistry groups;

    public RMMotdManager(ConfigFile cfg, GroupsRegistry groups) {
        rand = new Random();
        this.config = cfg;
        this.groups = groups;
    }

    @Override
    public File getMotdFile(Player player, int month, int day) {
        RealMotd plugin = (RealMotd) player.getServer().getPluginManager().getPlugin("RealMotd");
        Group group = groups.getGroup(player);
        switch (config.getMode()) {
        case DAILY:
            String path = plugin.getDataFolder().getAbsolutePath() + File.separator + "messages";
            boolean groupOnlyExists = false;
            boolean worldOnlyExists = false;
            boolean groupAndWorldExists = false;
            // Daily MOTD check
            if (config.isGroupSpecific()) {
                if (group != null)
                    groupOnlyExists = new File(path + File.separator + group.getName(), "motd_" + month + "_" + day + ".txt").exists();
            }
            if (config.isWorldSpecific()) {
                worldOnlyExists = new File(path + File.separator + player.getWorld().getName(), "motd_" + month + "_" + day + ".txt").exists();
            }
            if (config.isWorldSpecific() && config.isGroupSpecific()) {
                groupAndWorldExists = new File(path + File.separator + group.getName() + File.separator + player.getWorld().getName(), "motd_" + month + "_" + day + ".txt").exists();
            }
            if (groupAndWorldExists) {
                return new File(path + File.separator + group.getName() + File.separator + player.getWorld().getName(), "motd_" + month + "_" + day + ".txt");
            } else if (groupOnlyExists) {
                return new File(path + File.separator + group.getName(), "motd_" + month + "_" + day + ".txt");
            } else if (worldOnlyExists) {
                return new File(path + File.separator + player.getWorld().getName(), "motd_" + month + "_" + day + ".txt");
            }
            groupOnlyExists = false;
            worldOnlyExists = false;
            groupAndWorldExists = false;
            // Default MOTD check
            if (config.isGroupSpecific()) {
                if (group != null)
                    groupOnlyExists = new File(path + File.separator + group.getName(), "motd.txt").exists();
            }
            if (config.isWorldSpecific()) {
                worldOnlyExists = new File(path + File.separator + player.getWorld().getName(), "motd.txt").exists();
            }
            if (config.isWorldSpecific() && config.isGroupSpecific()) {
                groupAndWorldExists = new File(path + File.separator + group.getName() + File.separator + player.getWorld().getName(), "motd.txt").exists();
            }
            if (groupAndWorldExists) {
                return new File(path + File.separator + group.getName() + File.separator + player.getWorld().getName(), "motd.txt");
            } else if (groupOnlyExists) {
                return new File(path + File.separator + group.getName(), "motd.txt");
            } else if (worldOnlyExists) {
                return new File(path + File.separator + player.getWorld().getName(), "motd.txt");
            }
            break;
        case SINGLE:
            path = plugin.getDataFolder().getAbsolutePath() + File.separator + "messages";
            groupOnlyExists = false;
            worldOnlyExists = false;
            groupAndWorldExists = false;
            group = groups.getGroup(player);
            if (config.isGroupSpecific()) {
                if (group != null)
                    groupOnlyExists = new File(path + File.separator + group.getName(), "motd.txt").exists();
            }
            if (config.isWorldSpecific()) {
                worldOnlyExists = new File(path + File.separator + player.getWorld().getName(), "motd.txt").exists();
            }
            if (config.isWorldSpecific() && config.isGroupSpecific()) {
                groupAndWorldExists = new File(path + File.separator + group.getName() + File.separator + player.getWorld().getName(), "motd.txt").exists();
            }
            if (groupAndWorldExists) {
                return new File(path + File.separator + group.getName() + File.separator + player.getWorld().getName(), "motd.txt");
            } else if (groupOnlyExists) {
                return new File(path + File.separator + group.getName(), "motd.txt");
            } else if (worldOnlyExists) {
                return new File(path + File.separator + player.getWorld().getName(), "motd.txt");
            }
            return getDefaultMotdFile();
        case RANDOM:
            ArrayList<File> files = new ArrayList<File>();
            if (config.isGroupSpecific() && config.isWorldSpecific()) {
                files.addAll(Arrays.asList(new File(plugin.getDataFolder() + File.separator + "messages" + File.separator + group.getName() + File.separator + player.getWorld().getName()).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                })));
            }
            if (files.size() > 0) {
                return files.get(rand.nextInt(files.size()));
            }
            if (config.isGroupSpecific()) {
                files.addAll(Arrays.asList(new File(plugin.getDataFolder() + File.separator + "messages" + File.separator + group.getName()).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                })));
            }
            if (files.size() > 0) {
                return files.get(rand.nextInt(files.size()));
            }
            if (config.isWorldSpecific()) {
                files.addAll(Arrays.asList(new File(plugin.getDataFolder() + File.separator + "messages" + File.separator + player.getWorld().getName()).listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return name.endsWith(".txt");
                    }
                })));
            }
            if (files.size() > 0) {
                return files.get(rand.nextInt(files.size()));
            }
            files.addAll(Arrays.asList(new File(plugin.getDataFolder() + File.separator + "messages").listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith(".txt");
                }
            })));
            if (files.size() > 0) {
                return files.get(rand.nextInt(files.size()));
            }
            break;
        }
        return getDefaultMotdFile();
    }

    public File getDefaultMotdFile() {
        return new File(Bukkit.getServer().getPluginManager().getPlugin("RealMotd").getDataFolder(), "messages".concat(File.separator).concat("motd.txt"));
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
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("player", player.getName());
        properties.put("nick", player.getDisplayName());
        properties.put("time", "" + player.getWorld().getTime());
        String timeStat = "";
        if (motd.contains("%timestat%")) {
            if (player.getWorld().getTime() < 6000L)
                timeStat = plugin.getTranslation("time.morning");
            if (player.getWorld().getTime() < 12000L && timeStat.length() == 0)
                timeStat = plugin.getTranslation("time.day");
            if (player.getWorld().getTime() < 18000L && timeStat.length() == 0)
                timeStat = plugin.getTranslation("time.evening");
            if (player.getWorld().getTime() < 24000L && timeStat.length() == 0)
                timeStat = plugin.getTranslation("time.night");
            properties.put("timestat", timeStat);
        }
        properties.put("ptime", "" + player.getPlayerTime());
        String s = "";
        if (motd.contains("%ptimestat%")) {
            if (player.getPlayerTime() < 6000L)
                s = plugin.getTranslation("time.morning");
            if (player.getPlayerTime() < 12000L && s.length() == 0)
                s = plugin.getTranslation("time.day");
            if (player.getPlayerTime() < 18000L && s.length() == 0)
                s = plugin.getTranslation("time.evening");
            if (player.getPlayerTime() < 24000L && s.length() == 0)
                s = plugin.getTranslation("time.night");
        }
        properties.put("ptimestat", s);
        /*
         * if (RealMotd.ph != null) motd = motd.replace("%group%",
         * RealMotd.ph.getGroup(player.getWorld().getName(), player.getName()));
         */

        if (motd.contains("%difficulty%")) {
            s = plugin.getTranslation("diff." + player.getWorld().getDifficulty().name().toLowerCase());
            properties.put("difficulty", s);
        }
        if (motd.contains("%day%"))
            properties.put("day", "" + player.getWorld().getFullTime() / 1000L / 24L);
        properties.put("world", player.getWorld().getName());
        if (motd.contains("%weather%")) {
            String weather = "<unknown>";
            if (player.getWorld().hasStorm())
                weather = plugin.getTranslation("weather.raining");
            else
                weather = plugin.getTranslation("weather.clear");
            properties.put("weather", weather);
        }
        properties.put("ip", player.getAddress().getHostName());
        StringBuilder sb = new StringBuilder();
        if (motd.contains("%playerlist%")) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                sb = sb.append(p.getDisplayName()).append(',');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            properties.put("playerlist", sb.toString());
        }
        properties.put("nplayersonline", "" + plugin.getServer().getOnlinePlayers().length);
        properties.put("nmaxplayers", "" + plugin.getServer().getMaxPlayers());
        properties.put("serverip", "" + plugin.getServer().getIp());
        properties.put("serverport", "" + plugin.getServer().getPort());
        properties.put("serverid", "" + plugin.getServer().getServerId());
        s = "";
        s = (plugin.getServer().getAllowFlight() ? plugin.getTranslation("flight.allowed") : plugin.getTranslation("flight.denied"));
        properties.put("allowflight", "" + s);
        s = (plugin.getServer().getAllowNether() ? plugin.getTranslation("nether.allowed") : plugin.getTranslation("nether.denied"));
        properties.put("allowednether", "" + s);
        s = (plugin.getServer().getAllowEnd() ? plugin.getTranslation("end.allowed") : plugin.getTranslation("end.denied"));
        properties.put("allowend", s);
        properties.put("env", "" + plugin.getTranslation("env." + player.getWorld().getEnvironment().name().toLowerCase()));
        sb = new StringBuilder();
        if (motd.contains("%whitelist%")) {
            for (OfflinePlayer op : plugin.getServer().getWhitelistedPlayers()) {
                sb = sb.append(op.getName()).append(',');
            }
            if (sb.length() > 0)
                sb = sb.deleteCharAt(sb.length() - 1);
            properties.put("whitelist", "" + sb.toString());
        }
        sb = new StringBuilder();
        if (motd.contains("%banlist%")) {
            for (OfflinePlayer op : plugin.getServer().getBannedPlayers()) {
                sb = sb.append(op.getName()).append(',');
            }
            if (sb.length() > 0)
                sb = sb.deleteCharAt(sb.length() - 1);
            properties.put("banlist", "" + sb.toString());
        }
        if (motd.contains("%worlds%")) {
            sb = new StringBuilder();
            for (World world : plugin.getServer().getWorlds()) {
                sb = sb.append(diffToColor(world.getDifficulty()).toString()).append(world.getName()).append(ChatColor.WHITE).append(',');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            properties.put("worlds", "" + sb.toString());
        }
        properties.put("d", "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        properties.put("mo", "" + Calendar.getInstance().get(Calendar.MONTH));
        properties.put("yr", "" + Calendar.getInstance().get(Calendar.YEAR));

        properties.put("h", "" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
        properties.put("mi", "" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)));
        properties.put("s", "" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0" + Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)));

        properties.put("exp", "" + player.getExp());
        // thank you MC wiki :)
        int e = (int) (3.5 * (player.getLevel() + 1 * (player.getLevel() + 2) - player.getLevel() * (player.getLevel() + 1)));
        properties.put("expprog", "" + player.getExp() / e * 100);
        if (player.getWorld().getPVP()) {
            s = plugin.getTranslation("pvp.allowed");
        } else
            s = plugin.getTranslation("pvp.denied");
        properties.put("pvp", s);
        properties.put("x", "" + player.getLocation().getX());
        properties.put("y", "" + player.getLocation().getY());
        properties.put("z", "" + player.getLocation().getZ());
        properties.put("lev", "" + player.getLevel());
        properties.put("food", "" + player.getFoodLevel());
        properties.put("totalexp", "" + player.getTotalExperience());
        if (motd.contains("%plugins%")) {
            Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
            sb.delete(0, sb.length() - 1);
            for (Plugin plug : plugins) {
                sb = sb.append(plug.getDescription().getName()).append(',');
            }
            sb = sb.deleteCharAt(sb.length() - 1);
            properties.put("plugins", sb.toString());
        }
        s = "<unknown>";
        if (player.isOp())
            s = plugin.getTranslation("op.is");
        else
            s = plugin.getTranslation("op.isnt");
        properties.put("op", "" + s);
        s = plugin.getTranslation("mode." + player.getGameMode().name().toLowerCase());
        properties.put("mode", s);
        // Swap properties at first, so translations work with colors
        for (Entry<String, String> property : properties.entrySet()) {
            if (motd.contains(property.getKey()))
                motd = motd.replace("%" + property.getKey() + "%", property.getValue());
        }
        // Apply colors patch
        for (ChatColor cc : ChatColor.values()) {
            motd = motd.replace("&" + cc.name().toLowerCase(), cc.toString());
            motd = motd.replace("&" + cc.name(), cc.toString());
        }
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
        if (motd.contains("&rbow")) {
            while (motd.contains("&rbow")) {
                int index = motd.indexOf("&rbow") + 5;
                int endIndex = motd.indexOf(ChatColor.RESET.toString(), index);
                if(endIndex < 0){
                    endIndex = motd.length();
                }
                String substr = motd.substring(index, endIndex);
                StringBuilder replacement = new StringBuilder();
                for (int i = 0; i < substr.length(); ++i) {
                    char c = substr.charAt(i);
                    // don't break my newlines!!!
                    if(c == '/' && i <= substr.length() - 2 && substr.charAt(i+1) == 'n'){
                        i++;
                        replacement = replacement.append("/n");
                        continue;
                    }
                    replacement = replacement.append(config.getRainbowColors()[rand.nextInt(config.getRainbowColors().length)].toString()).append(c);
                }
                replacement = replacement.append(ChatColor.RESET.toString());
                motd = motd.replace("&rbow".concat(substr).concat(ChatColor.RESET.toString()), replacement.toString());
            }
        }
        if (motd.contains(config.getPermissionIdentifier())) {
            // Permissions patch
            for (String string : motd.split(" ")) {
                if (string.contains(config.getPermissionIdentifier()))
                    motd = motd.replace(string, "" + (player.hasPermission(string.replace(config.getPermissionIdentifier(), "")) ? plugin.getTranslation("permission.has") : plugin.getTranslation("permission.hasnt")));
            }
        }
        if (motd.contains(config.getCommandIdentifier())) {
            // Commands patch
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

    private ChatColor diffToColor(Difficulty diff) {
        switch (diff) {
        case EASY:
            return ChatColor.YELLOW;
        case HARD:
            return ChatColor.DARK_RED;
        case NORMAL:
            return ChatColor.GOLD;
        case PEACEFUL:
            return ChatColor.GREEN;
        }
        return ChatColor.WHITE;
    }

    @Override
    public EMotdMode getMode() {
        return config.getMode();
    }

}
