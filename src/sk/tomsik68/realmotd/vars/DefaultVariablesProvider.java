package sk.tomsik68.realmotd.vars;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scoreboard.Team;

import sk.tomsik68.realmotd.RealMotd;

public final class DefaultVariablesProvider extends VariableProvider {

    public DefaultVariablesProvider(Server server) {
        super(server);
    }

    @Override
    public Map<String, Variable> provide() {
        RealMotd plugin = (RealMotd) server.getPluginManager().getPlugin("RealMotd");
        HashMap<String, Variable> result = new HashMap<String, Variable>();
        result.put("player", new PlayerGetterVariable(plugin, "getName"));
        result.put("nick", new PlayerGetterVariable(plugin, "getDisplayName"));
        result.put("time", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Long.toString(player.getWorld().getTime());
            }
        });
        result.put("timestat", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                long time = player.getWorld().getTime();
                String timeStat = "";
                if (time < 6000l)
                    timeStat = plugin.getTranslation("time.morning");
                if (time < 12000l && time > 6000l)
                    timeStat = plugin.getTranslation("time.day");
                if (time < 18000l && time > 12000l)
                    timeStat = plugin.getTranslation("time.evening");
                if (time < 24000l && time > 18000l)
                    timeStat = plugin.getTranslation("time.night");
                return timeStat;
            }
        });
        result.put("ptime", new PlayerGetterVariable(plugin, "getPlayerTime"));
        result.put("ptimestat", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                long time = player.getPlayerTime();
                String timeStat = "";
                if (time < 6000l)
                    timeStat = plugin.getTranslation("time.morning");
                if (time < 12000l && time > 6000l)
                    timeStat = plugin.getTranslation("time.day");
                if (time < 18000l && time > 12000l)
                    timeStat = plugin.getTranslation("time.evening");
                if (time < 24000l && time > 18000l)
                    timeStat = plugin.getTranslation("time.night");
                return timeStat;
            }
        });
        result.put("difficulty", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getTranslation("diff." + player.getWorld().getDifficulty().name().toLowerCase());
            }
        });
        result.put("day", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Long.toString(player.getWorld().getFullTime() / 24000l);
            }
        });
        result.put("world", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.getWorld().getName();
            }
        });
        result.put("weather", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return (player.getWorld().hasStorm() ? plugin.getTranslation("weather.raining") : plugin.getTranslation("weather.clear"));
            }
        });
        result.put("ip", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.getAddress().getHostName();
            }
        });
        result.put("playerlist", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                StringBuilder sb = new StringBuilder();
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    sb = sb.append(p.getDisplayName()).append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            }
        });
        result.put("nplayersonline", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return Integer.toString(player.getServer().getOnlinePlayers().size());
            }
        });
        result.put("nmaxplayers", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return Integer.toString(player.getServer().getMaxPlayers());
            }
        });
        result.put("serverip", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return player.getServer().getIp();
            }
        });
        result.put("serverport", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return Integer.toString(player.getServer().getPort());
            }
        });
        result.put("serverid", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return plugin.getServer().getServerId();
            }
        });
        result.put("allowflight", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return plugin.getServer().getAllowFlight() ? plugin.getTranslation("flight.allowed") : plugin.getTranslation("flight.denied");
            }
        });
        result.put("allowednether", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return plugin.getServer().getAllowNether() ? plugin.getTranslation("nether.allowed") : plugin.getTranslation("nether.denied");
            }
        });
        result.put("allowedend", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                return plugin.getServer().getAllowEnd() ? plugin.getTranslation("end.allowed") : plugin.getTranslation("end.denied");
            }
        });
        result.put("env", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getTranslation("env.".concat(player.getWorld().getEnvironment().name().toLowerCase()));
            }
        });
        result.put("whitelist", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                StringBuilder sb = new StringBuilder();
                for (OfflinePlayer p : player.getServer().getWhitelistedPlayers()) {
                    sb = sb.append(p.getName()).append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            }
        });
        result.put("banlist", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                StringBuilder sb = new StringBuilder();
                for (OfflinePlayer p : player.getServer().getBannedPlayers()) {
                    sb = sb.append(p.getName()).append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            }
        });
        result.put("worlds", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                StringBuilder sb = new StringBuilder();
                for (World world : plugin.getServer().getWorlds()) {
                    sb = sb.append(world.getName()).append(',');
                }
                if (sb.length() > 0)
                    sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            }
        });
        result.put("d", new CalendarVariable(plugin, "%02d", Calendar.DAY_OF_MONTH));
        result.put("mo", new CalendarVariable(plugin, "%02d", Calendar.MONTH));
        result.put("yr", new CalendarVariable(plugin, "%04d", Calendar.YEAR));
        result.put("h", new CalendarVariable(plugin, "%02d", Calendar.HOUR_OF_DAY));
        result.put("mi", new CalendarVariable(plugin, "%02d", Calendar.MINUTE));
        result.put("s", new CalendarVariable(plugin, "%02d", Calendar.SECOND));

        result.put("lev", new PlayerGetterVariable(plugin, "getLevel"));
        result.put("food", new PlayerGetterVariable(plugin, "getFoodLevel"));
        result.put("exp", new PlayerGetterVariable(plugin, "getExp"));
        result.put("totalexp", new PlayerGetterVariable(plugin, "getTotalExperience"));
        result.put("expprog", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                // thank you MC wiki
                double e = (3.5 * (player.getLevel() + 1 * (player.getLevel() + 2) - player.getLevel() * (player.getLevel() + 1)));
                return Double.toString(player.getExp() / e * 100d);
            }
        });
        result.put("pvp", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.getWorld().getPVP() ? plugin.getTranslation("pvp.allowed") : plugin.getTranslation("pvp.denied");
            }
        });
        result.put("x", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Double.toString(player.getLocation().getX());
            }
        });
        result.put("y", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Double.toString(player.getLocation().getY());
            }
        });
        result.put("z", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Double.toString(player.getLocation().getZ());
            }
        });
        result.put("plugins", new Variable(plugin) {
            @Override
            public boolean requiresPlayer() {
                return false;
            }

            @Override
            public String getValue(Player player) {
                Plugin[] plugins = player.getServer().getPluginManager().getPlugins();
                StringBuilder sb = new StringBuilder();
                for (Plugin plug : plugins) {
                    sb = sb.append(plug.getDescription().getName()).append(',');
                }
                sb = sb.deleteCharAt(sb.length() - 1);
                return sb.toString();
            }
        });
        result.put("op", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.isOp() ? plugin.getTranslation("op.is") : plugin.getTranslation("op.isnt");
            }
        });
        result.put("mode", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getTranslation("mode.".concat(player.getGameMode().name().toLowerCase()));
            }
        });
        result.put("team", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                Team t = getTeam(player);
                if (t != null)
                    return t.getDisplayName();
                else
                    return plugin.getTranslation("team.none");
            }

            private Team getTeam(Player player) {
                Set<Team> teams = player.getServer().getScoreboardManager().getMainScoreboard().getTeams();
                for (Team team : teams) {
                    if(team.hasEntry(player.getName())){
                        return team;
                    }
                }
                return null;
            }
        });
        return result;
    }
}
