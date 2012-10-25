package sk.tomsik68.realmotd.api;

import java.io.File;

import org.bukkit.entity.Player;

public interface MotdManager {
	public File getMotdFile(Player player, int month,int day,boolean wspec,boolean gspec,boolean random);
	/**
	 * 
	 * @param player
	 * @param world
	 * @param month
	 * @param day
	 * @param motd - Motd split in lines
	 */
	public void sendMotd(Player player);
	public String getMotd(Player player, int month, int day, boolean wspec, boolean gspec,boolean random);
}
