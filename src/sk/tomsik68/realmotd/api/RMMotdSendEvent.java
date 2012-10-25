package sk.tomsik68.realmotd.api;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RMMotdSendEvent extends Event implements Cancellable {
    private String motd;
    private final Player recv;
    private boolean cancel;
    private HandlerList handlers;

    public RMMotdSendEvent(String motd, Player receiver) {
        this.handlers = new HandlerList();
        this.motd = motd;
        this.recv = receiver;
    }

    public boolean isCancelled() {
        return this.cancel;
    }

    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }

    public String getMotd() {
        return this.motd;
    }

    public Player getReceiver() {
        return this.recv;
    }

    public void addVariable(String variableName, Object value) {
        this.motd = this.motd.replaceFirst("%".concat(variableName).concat("%"), value.toString());
    }

    public void setMotd(String newOne) {
        this.motd = newOne;
    }

    public HandlerList getHandlers() {
        return this.handlers;
    }
}