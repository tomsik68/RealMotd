package sk.tomsik68.realmotd.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import sk.tomsik68.realmotd.vars.Variable;

public final class VariablesRegisterEvent extends Event {
    private final HashMap<String, Variable> toRegister = new HashMap<String, Variable>();

    public VariablesRegisterEvent() {

    }

    public void registerVariable(String name, Variable var) {
        toRegister.put(name, var);
    }

    public Map<String, Variable> getToRegister() {
        return toRegister;
    }

    private static final HandlerList handlers = new HandlerList();

    public static HandlerList getHandlerList() {
        return handlers;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

}
