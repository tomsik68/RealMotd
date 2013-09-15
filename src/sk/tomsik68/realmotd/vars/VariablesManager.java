package sk.tomsik68.realmotd.vars;

import java.util.Calendar;
import java.util.HashMap;

import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.realmotd.RealMotd;
import sk.tomsik68.realmotd.Variable;

public class VariablesManager {
    public static VariablesManager instance = new VariablesManager();
    private HashMap<String, Variable> vars = new HashMap<String, Variable>();

    private VariablesManager() {

    }

    public HashMap<String, Variable> getVariables() {
        return vars;
    }

    public void registerVariable(String name, Variable var) {
        if (vars.containsKey(name))
            RealMotd.log.warning(String.format("[RealMotd] Variable name conflict at %s: %s vs %s ", name, var.getClass().getName(), vars.get(name).getClass().getName()));
        vars.put(name, var);
    }

    public void initDefaultVariables(RealMotd plugin) {
        registerVariable("player", new PlayerGetterVariable(plugin, "getName"));
        registerVariable("nick", new PlayerGetterVariable(plugin, "getDisplayName"));
        registerVariable("time", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Long.toString(player.getWorld().getTime());
            }
        });
        registerVariable("timestat", new Variable(plugin) {
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
        registerVariable("ptime", new PlayerGetterVariable(plugin, "getPlayerTime"));
        registerVariable("ptimestat", new Variable(plugin) {
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
        registerVariable("difficulty", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getTranslation("diff." + player.getWorld().getDifficulty().name().toLowerCase());
            }
        });
        registerVariable("day", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Long.toString(player.getWorld().getFullTime() / 24000l);
            }
        });
        registerVariable("world", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.getWorld().getName();
            }
        });
        registerVariable("weather", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return (player.getWorld().hasStorm() ? plugin.getTranslation("weather.raining") : plugin.getTranslation("weather.clear"));
            }
        });
        registerVariable("ip", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.getAddress().getHostName();
            }
        });
        registerVariable("playerlist", new Variable(plugin) {
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
        registerVariable("nplayersonline", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Integer.toString(player.getServer().getOnlinePlayers().length);
            }
        });
        registerVariable("nmaxplayers", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Integer.toString(player.getServer().getMaxPlayers());
            }
        });
        registerVariable("serverip", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.getServer().getIp();
            }
        });
        registerVariable("serverport", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Integer.toString(player.getServer().getPort());
            }
        });
        registerVariable("serverid", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getServer().getServerId();
            }
        });
        registerVariable("allowflight", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getServer().getAllowFlight() ? plugin.getTranslation("flight.allowed") : plugin.getTranslation("flight.denied");
            }
        });
        registerVariable("allowednether", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getServer().getAllowNether() ? plugin.getTranslation("nether.allowed") : plugin.getTranslation("nether.denied");
            }
        });
        registerVariable("allowedend", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getServer().getAllowEnd() ? plugin.getTranslation("end.allowed") : plugin.getTranslation("end.denied");
            }
        });
        registerVariable("env", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return plugin.getTranslation("env.".concat(player.getWorld().getEnvironment().name().toLowerCase()));
            }
        });
        registerVariable("whitelist", new Variable(plugin) {
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
        registerVariable("banlist", new Variable(plugin) {
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
        registerVariable("worlds", new Variable(plugin) {
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
        registerVariable("d", new CalendarVariable(plugin, Calendar.DAY_OF_MONTH));
        registerVariable("mo", new CalendarVariable(plugin, Calendar.MONTH));
        registerVariable("yr", new CalendarVariable(plugin, Calendar.YEAR));
        registerVariable("h", new CalendarVariable(plugin, Calendar.HOUR_OF_DAY));
        registerVariable("mi", new CalendarVariable(plugin, Calendar.MINUTE));
        registerVariable("s", new CalendarVariable(plugin, Calendar.SECOND));

        registerVariable("lev", new PlayerGetterVariable(plugin, "getLevel"));
        registerVariable("food", new PlayerGetterVariable(plugin, "getFoodLevel"));
        registerVariable("exp", new PlayerGetterVariable(plugin, "getExp"));
        registerVariable("totalexp", new PlayerGetterVariable(plugin, "getTotalExperience"));
        registerVariable("expprog", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                double e = (3.5 * (player.getLevel() + 1 * (player.getLevel() + 2) - player.getLevel() * (player.getLevel() + 1)));
                return Double.toString(player.getExp() / e * 100d);
            }
        });
        registerVariable("pvp", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return player.getWorld().getPVP() ? plugin.getTranslation("pvp.allowed") : plugin.getTranslation("pvp.denied");
            }
        });
        registerVariable("x", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Double.toString(player.getLocation().getX());
            }
        });
        registerVariable("y", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Double.toString(player.getLocation().getY());
            }
        });
        registerVariable("z", new Variable(plugin) {
            @Override
            public String getValue(Player player) {
                return Double.toString(player.getLocation().getZ());
            }
        });
        registerVariable("plugins", new Variable(plugin) {
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
        registerVariable("op",new Variable(plugin){
            @Override
            public String getValue(Player player) {
                return player.isOp() ? plugin.getTranslation("op.is") : plugin.getTranslation("op.isnt");
            }
        });
        registerVariable("mode",new Variable(plugin){
            @Override
            public String getValue(Player player) {
                return plugin.getTranslation("mode.".concat(player.getGameMode().name().toLowerCase()));
            }
        });
    }
}
