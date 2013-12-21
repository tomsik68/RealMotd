package sk.tomsik68.realmotd.api;

import org.bukkit.entity.Player;
import sk.tomsik68.realmotd.EMotdMode;
import sk.tomsik68.realmotd.RealMotd;

public interface MotdManager {
    public EMotdMode getMode();

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

    
}
