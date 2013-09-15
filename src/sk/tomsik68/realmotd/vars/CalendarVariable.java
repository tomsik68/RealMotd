package sk.tomsik68.realmotd.vars;

import java.util.Calendar;

import org.bukkit.entity.Player;

import sk.tomsik68.realmotd.RealMotd;
import sk.tomsik68.realmotd.Variable;

public class CalendarVariable extends Variable {
    
    private final int field;

    public CalendarVariable(RealMotd plugin, int field) {
        super(plugin);
        this.field = field;
    }

    @Override
    public String getValue(Player player) {
        return Integer.toString(Calendar.getInstance().get(field));
    }

}
