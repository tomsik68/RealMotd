package sk.tomsik68.realmotd.api;

import java.io.File;

import org.bukkit.entity.Player;

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

    String addVariables(String motd, Player player, RealMotd plugin);
}
