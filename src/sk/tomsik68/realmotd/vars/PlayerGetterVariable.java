package sk.tomsik68.realmotd.vars;

import org.bukkit.entity.Player;

import sk.tomsik68.realmotd.RealMotd;

public class PlayerGetterVariable extends Variable {

    private final String getterName;

    public PlayerGetterVariable(RealMotd plugin, String getterName) {
        super(plugin);
        this.getterName = getterName;
    }
    @Override
    public String getValue(Player player) {
        try {
            return player.getClass().getMethod(getterName).invoke(player).toString();
        } catch (Exception e) {
            return "<unknown>";
        }
    }

}
