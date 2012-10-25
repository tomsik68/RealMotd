package sk.tomsik68.realmotd;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Calendar;

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

public class RMMotdManager implements MotdManager {
    public RMMotdManager() {

    }

    @Override
    public File getMotdFile(Player player, int month, int day, boolean wspec, boolean gspec, boolean random) {
        if (month < 0 && day < 0) {
            return getDefaultMotdFile();
        }
        RealMotd plugin = (RealMotd) player.getServer().getPluginManager().getPlugin("RealMotd");
        StringBuilder path = new StringBuilder();
        path = path.append(plugin.getDataFolder().getAbsolutePath());
        path = path.append(File.separatorChar);
        path = path.append("messages");
        path = path.append(File.separatorChar);
        /*
         * if (gspec) { if (RealMotd.ph == null) path =
         * path.append(player.isOp() ? "ops" : "players"); else { path =
         * path.append(RealMotd.ph.getGroups(player.getWorld().getName(),
         * player.getName())[0]); } path = path.append(File.separatorChar); }
         */
        if (wspec) {
            path = path.append(player.getWorld().getName());
            path = path.append(File.separatorChar);
        }
        if (random) {
            path = path.append(new File(path.toString()).list()[0]);
        } else
            path = path.append("motd_").append(month).append("_").append(day).append(".txt");
        return new File(path.toString());
    }

    public File getDefaultMotdFile() {
        return new File(Bukkit.getServer().getPluginManager().getPlugin("RealMotd").getDataFolder(), "messages".concat(File.separator).concat("motd.txt"));
    }

    @Override
    public void sendMotd(Player player) {
        RealMotd plugin = (RealMotd) player.getServer().getPluginManager().getPlugin("RealMotd");
        int month = Calendar.getInstance().get(Calendar.MONTH);
        int day = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
        boolean wspec = plugin.getConfig().getBoolean("motd.world-specific", false);
        boolean gspec = plugin.getConfig().getBoolean("motd.group-specific", false);
        String motd = getMotd(player, month, day, wspec, gspec, plugin.getConfig().getBoolean("motd.random", false));
        motd = motd.replace("%player%", player.getDisplayName() == null ? player.getName() : player.getDisplayName());
        motd = motd.replace("%time%", "" + player.getWorld().getTime());
        /*
         * if (RealMotd.ph != null) motd = motd.replace("%group%",
         * RealMotd.ph.getGroup(player.getWorld().getName(), player.getName()));
         */
        String s = "";
        if (motd.contains("%timestat%")) {
            if (player.getWorld().getTime() < 6000L)
                s = plugin.getTranslation("time.morning");
            if (player.getWorld().getTime() < 12000L && s.equals(""))
                s = plugin.getTranslation("time.day");
            if (player.getWorld().getTime() < 18000L && s.equals(""))
                s = plugin.getTranslation("time.evening");
            if (player.getWorld().getTime() < 24000L && s.equals(""))
                s = plugin.getTranslation("time.night");
            motd = motd.replace("%timestat%", s);
        }
        s = "";
        if (motd.contains("%ptime%")) {
            motd = motd.replace("%ptime%", "" + player.getPlayerTime());
        }
        if(motd.contains("%ptimestat%")){
            if (player.getPlayerTime() < 6000L)
                s = plugin.getTranslation("time.morning");
            if (player.getPlayerTime() < 12000L && s.equals(""))
                s = plugin.getTranslation("time.day");
            if (player.getPlayerTime() < 18000L && s.equals(""))
                s = plugin.getTranslation("time.evening");
            if (player.getPlayerTime() < 24000L && s.equals(""))
                s = plugin.getTranslation("time.night");
            motd = motd.replace("%ptimestat%", s);
        }
        if (motd.contains("%difficulty%")) {
            s = plugin.getTranslation("diff." + player.getWorld().getDifficulty().name().toLowerCase());
            motd = motd.replace("%difficulty%", s);
        }
        if(motd.contains("%day%"))
            motd = motd.replace("%day%", "" + player.getWorld().getFullTime() / 1000L / 24L);
        if(motd.contains("%world%"))
            motd = motd.replace("%world%", player.getWorld().getName());
        if (motd.contains("%weather%")) {
            String weather = "<unknown>";
            if (player.getWorld().hasStorm())
                weather = plugin.getTranslation("weather.raining");
            else
                weather = plugin.getTranslation("weather.clear");
            motd = motd.replace("%weather%", weather);
        }
        if (motd.contains("%ip%"))
            motd = motd.replace("%ip%", player.getAddress().getHostName());
        StringBuilder sb = new StringBuilder();
        if (motd.contains("%playerlist%")) {
            for (Player p : plugin.getServer().getOnlinePlayers()) {
                sb = sb.append(p.getDisplayName()).append(',');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            motd = motd.replace("%playerlist%", sb.toString());
        }
        motd = motd.replace("%nplayersonline%", "" + plugin.getServer().getOnlinePlayers().length);
        motd = motd.replace("%nmaxplayers%", "" + plugin.getServer().getMaxPlayers());
        motd = motd.replace("%serverip%", "" + plugin.getServer().getIp());
        motd = motd.replace("%serverport%", "" + plugin.getServer().getPort());
        motd = motd.replace("%serverid%", "" + plugin.getServer().getServerId());
        s = "";
        s = (plugin.getServer().getAllowFlight() ? plugin.getTranslation("flight.allowed") : plugin.getTranslation("flight.denied"));
        motd = motd.replace("%allowflight%", "" + s);
        s = (plugin.getServer().getAllowNether() ? plugin.getTranslation("nether.allowed") : plugin.getTranslation("nether.denied"));
        motd = motd.replace("%allowednether%", "" + s);
        s = (plugin.getServer().getAllowEnd() ? plugin.getTranslation("end.allowed") : plugin.getTranslation("end.denied"));
        motd = motd.replace("%allowend%", s);
        motd = motd.replace("%env%", "" + plugin.getTranslation("env." + player.getWorld().getEnvironment().name().toLowerCase()));
        sb = new StringBuilder();
        if (motd.contains("%whitelist%")) {
            for (OfflinePlayer op : plugin.getServer().getWhitelistedPlayers()) {
                sb = sb.append(op.getName()).append(',');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            motd = motd.replace("%whitelist%", "" + sb.toString());
        }
        sb = new StringBuilder();
        if (motd.contains("%banlist%")) {
            for (OfflinePlayer op : plugin.getServer().getBannedPlayers()) {
                sb = sb.append(op.getName()).append(',');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            motd = motd.replace("%banlist%", "" + sb.toString());
        }
        if (motd.contains("%worlds%")) {
            sb = new StringBuilder();
            for (World world : plugin.getServer().getWorlds()) {
                sb = sb.append(diffToColor(world.getDifficulty()).toString()).append(world.getName()).append(ChatColor.WHITE).append(',');
            }
            if (sb.length() > 0)
                sb.deleteCharAt(sb.length() - 1);
            motd = motd.replace("%worlds%", "" + sb.toString());
        }
        motd = motd.replace("%d%", "" + Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        motd = motd.replace("%mo%", "" + Calendar.getInstance().get(Calendar.MONTH));
        motd = motd.replace("%yr%", "" + Calendar.getInstance().get(Calendar.YEAR));

        motd = motd.replace("%h%", "" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0"+Calendar.getInstance().get(Calendar.HOUR_OF_DAY) : Calendar.getInstance().get(Calendar.HOUR_OF_DAY)));
        motd = motd.replace("%mi%", "" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0"+Calendar.getInstance().get(Calendar.MINUTE) : Calendar.getInstance().get(Calendar.MINUTE)));
        motd = motd.replace("%s%", "" + (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 10 ? "0"+Calendar.getInstance().get(Calendar.SECOND) : Calendar.getInstance().get(Calendar.SECOND)));

        motd = motd.replace("%exp%", "" + player.getExp());
        //thank you MC wiki :)
        int e = (int) (3.5 * (player.getLevel() + 1 * (player.getLevel() + 2) - player.getLevel() * (player.getLevel() + 1)));
        motd = motd.replace("%expprog%", "" + player.getExp() / e * 100);
        if (player.getWorld().getPVP()) {
            s = plugin.getTranslation("pvp.allowed");
        } else
            s = plugin.getTranslation("pvp.denied");
        motd = motd.replace("%pvp%", s);
        motd = motd.replace("%x%", "" + player.getLocation().getX());
        motd = motd.replace("%y%", "" + player.getLocation().getY());
        motd = motd.replace("%z%", "" + player.getLocation().getZ());
        motd = motd.replace("%lev%", "" + player.getLevel());
        motd = motd.replace("%food%", "" + player.getFoodLevel());
        motd = motd.replace("%totalexp%", "" + player.getTotalExperience());
        if (motd.contains("%plugins%")) {
            Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
            sb.delete(0, sb.length() - 1);
            for (Plugin plug : plugins) {
                sb = sb.append(plug.getDescription().getName()).append(',');
            }
            sb = sb.deleteCharAt(sb.length() - 1);
            motd = motd.replace("%plugins%", sb.toString());
        }
        s = "<unknown>";
        if (player.isOp())
            s = plugin.getTranslation("op.is");
        else
            s = plugin.getTranslation("op.isnt");
        motd = motd.replace("%op%", "" + s);
        s = plugin.getTranslation("mode." + player.getGameMode().name().toLowerCase());
        motd = motd.replace("%mode%", s);
        motd = motd.replace("", "");
        // Apply colors patch
        for (ChatColor cc : ChatColor.values()) {
            motd = motd.replace("&" + cc.name().toLowerCase(), cc.toString());
            motd = motd.replace("&" + cc.name().toUpperCase(), cc.toString());
        }
        if (motd.contains("&bo")) {
            motd = motd.replaceAll("&bold", ChatColor.BOLD.toString());
        }
        if (motd.contains("&it")) {
            motd = motd.replaceAll("&it", ChatColor.ITALIC.toString());
        }
        if (motd.contains("&str")) {
            motd = motd.replaceAll("&str", ChatColor.STRIKETHROUGH.toString());
        }
        if (motd.contains("&ran")) {
            motd = motd.replaceAll("&ran", "§k blahblahblah");
        }
        if (motd.contains("&un")) {
            motd = motd.replaceAll("&un", ChatColor.UNDERLINE.toString());
        }
        if (motd.contains("&no")) {
            motd = motd.replaceAll("&no", ChatColor.RESET.toString());
        }
        if (motd.contains("^")) {
            // Permissions patch
            for (String string : motd.split(" ")) {
                if (string.contains(plugin.getProperty("permission", "^")))
                    motd = motd.replace(string, "" + (player.hasPermission(string.replace(plugin.getProperty("permission", "^"), "")) ? plugin.getTranslation("permission.has") : plugin.getTranslation("permission.hasnt")));
            }
        }
        if (motd.contains(plugin.getProperty("command", ">"))) {
            // Commands patch
            for (String string : motd.split(" ")) {
                if (string.contains(plugin.getProperty("command", ">"))) {
                    FakeCommandSender fcs = new FakeCommandSender(player);
                    String result = "";
                    if (Bukkit.dispatchCommand(fcs, string.replace(plugin.getProperty("command", ">"), ""))) {
                        result = fcs.getText();
                    } else {
                        result = plugin.getTranslation("comm.fail");
                    }

                    motd = motd.replace(string, result);
                }
            }
        }
        try {
            //code required PII, so I changed it to reflection... :(
            Class<? extends Event> clazz = (Class<? extends Event>) Class.forName("sk.tomsik68.pii.MotdSendEvent");
            Event mse = clazz.getConstructor(CommandSender.class,String[].class).newInstance(player,motd.split("\n")); 
            plugin.getServer().getPluginManager().callEvent(mse);
            if (((Cancellable)mse).isCancelled())
                return;
            player.sendMessage((String[])clazz.getMethod("getMotd").invoke(mse));
        } catch (Exception ee) {
            player.sendMessage(motd.split("/n"));
        }
    }

    @Override
    public String getMotd(Player player, int month, int day, boolean wspec, boolean gspec, boolean random) {
        String motd = "<nothing>";
        if (!getMotdFile(player, month, day, wspec, gspec, random).exists()) {
            if (getDefaultMotdFile().exists()) {
                motd = Util.readFile(getDefaultMotdFile());
            } else {
                try {
                    getDefaultMotdFile().createNewFile();
                    PrintWriter pw = new PrintWriter(new FileWriter(getDefaultMotdFile()));
                    pw.println("&yellowHello %player%!");
                    pw.println("&greenThis is default MOTD of RealMotd by &goldTomsik68");
                    pw.println("&redTo change it, go to &gray" + getDefaultMotdFile().getAbsolutePath());
                    pw.flush();
                    pw.close();
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                motd = "&yellowHello &gray%player%!/n&greenThis is default MOTD of RealMotd by &goldTomsik68/n&redTo change it, go to &gray" + getDefaultMotdFile().getAbsolutePath();
            }
        } else {
            motd = Util.readFile(getMotdFile(player, month, day, wspec, gspec, random));
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

}
