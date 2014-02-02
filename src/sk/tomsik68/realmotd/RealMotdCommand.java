package sk.tomsik68.realmotd;

import java.util.Collection;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import sk.tomsik68.realmotd.api.groups.GroupsImporter;
import sk.tomsik68.realmotd.api.groups.GroupsImporters;

public class RealMotdCommand implements CommandExecutor {
    private final RealMotd plugin;

    public RealMotdCommand(RealMotd rm) {
        plugin = rm;
    }

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("realmotd") && sender.isOp()) {
            boolean result = false;
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    plugin.loadConfig();
                    plugin.getGroupRegistry().load();
                    result = true;
                } else if (args[0].equalsIgnoreCase("groups")) {
                    sendGroups(sender);
                    result = true;
                } else if (args[0].equalsIgnoreCase("importgroups")) {
                    importGroups(sender, args);
                    result = true;
                } else if (args[0].equalsIgnoreCase("set")) {
                    setMotd(sender, args);
                    result = true;
                }else
                    result = false;
            }
            return result;
        } else if (command.getName().equalsIgnoreCase("motd")) {
            if (sender instanceof Player) {
                plugin.sendMotd((Player) sender);
            } else {
                sender.sendMessage(ChatColor.RED + "[RealMotd] MOTD can only be sent to players now.");
            }
            return true;
        }
        return false;
    }

    private void importGroups(CommandSender sender, String[] args) {
        if (args.length > 1) {
            GroupsImporter importer = GroupsImporters.instance.getImporter(args[1]);
            if (importer == null) {
                sender.sendMessage(ChatColor.RED + "[RealMotd] Your importer name is invalid.");
                return;
            }
            if (!importer.isAvailable(sender.getServer())) {
                sender.sendMessage(ChatColor.RED + "[RealMotd] Looks like this importer is not available right now. You can try a different one.");
                return;
            }
            sender.sendMessage(ChatColor.GREEN + "[RealMotd] RealMotd is now importing your groups from '" + args[1] + "'. Please wait as this may take more time depending on how much players play on your server.");
            importer.importGroups(sender.getServer(), plugin.getGroupRegistry());
            sender.sendMessage(ChatColor.GREEN + "[RealMotd] Group importing is now done. It's recommended to reload server or at least RealMotd for better performance :)");
        } else {
            StringBuilder sb = new StringBuilder(ChatColor.RED.toString());
            sb = sb.append("[RealMotd] Please select importer. Available importers: ");
            for (String importer : GroupsImporters.instance.getImporters()) {
                sb = sb.append(importer).append(',');
            }
            if (sb.length() > 0)
                sb = sb.deleteCharAt(sb.length() - 1);
            sender.sendMessage(sb.toString());
        }
    }

    private void sendGroups(CommandSender sender) {
        Collection<String> gs = plugin.getGroupRegistry().getGroups();
        StringBuilder sb = new StringBuilder();
        for (String g : gs) {
            sb = sb.append(g).append(',');
        }
        if (sb.length() > 0) {
            sb = sb.deleteCharAt(sb.length() - 1);
        }
        sender.sendMessage("Groups: " + sb.toString());
    }

    private void setMotd(CommandSender sender, String[] args) {
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
            RealMotd.handler.setMOTD(sb.toString().split("/n"), world, group, month, day);
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}
