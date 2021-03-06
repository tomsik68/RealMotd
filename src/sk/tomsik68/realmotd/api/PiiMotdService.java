package sk.tomsik68.realmotd.api;

import org.bukkit.entity.Player;

import sk.tomsik68.pii.MOTDPlugin;
import sk.tomsik68.realmotd.RMMotdManager;
import sk.tomsik68.realmotd.Util;

public final class PiiMotdService implements MOTDPlugin {
    private MotdManager man;
    public PiiMotdService(MotdManager handle){
        man = handle;
    }
    @Override
    public String[] getMotd(Player player) {
        return man.getMotd(player, -1, -1).split("/n");
    }

    @Override
    public void setMotd(Player player, String... motd) {
        Util.writeFile(((RMMotdManager) man).getMotdFile(player, -1, -1), motd);
    }

}
