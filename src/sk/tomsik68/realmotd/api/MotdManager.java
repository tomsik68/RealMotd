package sk.tomsik68.realmotd.api;

import java.io.File;
import java.io.IOException;

import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import sk.tomsik68.realmotd.EMotdMode;
import sk.tomsik68.realmotd.RealMotd;

public interface MotdManager {
    public EMotdMode getMode();

    public File getMotdFile(Player player, int month, int day);

    /**
     * 
     * @param player
     * @param world
     * @param month
     * @param day
     * @param motd
     *            - Motd split in lines
     */
    public void sendMotd(Player player);

    public String getMotd(Player player, int month, int day);

    public String addVariables(String motd, Player player, RealMotd plugin);

    public void setMOTD(String[] motd, String world, String group, int month, int day) throws Exception;

    public File getMotdFile(Plugin plugin, EMotdMode mode, String group, String world, int month, int day);
}
