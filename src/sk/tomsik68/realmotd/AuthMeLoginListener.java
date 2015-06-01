package sk.tomsik68.realmotd;

import fr.xephi.authme.events.LoginEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

final class AuthMeLoginListener implements Listener {
    private final RealMotd realMotd;

    AuthMeLoginListener(RealMotd realMotd) {
        this.realMotd = realMotd;
    }

    @EventHandler(priority = EventPriority.MONITOR)
    final void onPlayerAuthMeLogin(final LoginEvent event) {
        realMotd.sendMessage(event.getPlayer());
    }
}
