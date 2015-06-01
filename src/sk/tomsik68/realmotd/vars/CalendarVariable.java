package sk.tomsik68.realmotd.vars;

import java.util.Calendar;

import org.bukkit.entity.Player;

import sk.tomsik68.realmotd.RealMotd;

final class CalendarVariable extends Variable {

    private final int field;
    private final String format;

    public CalendarVariable(RealMotd plugin, String formatStr, int field) {
        super(plugin);
        this.field = field;
        format = formatStr;
    }
    @Override
    public boolean requiresPlayer() {
        return false;
    }

    @Override
    public String getValue(Player player) {
        return String.format(format, Calendar.getInstance().get(field));
    }

}
