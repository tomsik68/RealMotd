package sk.tomsik68.realmotd.vars;

import java.util.Map;

import org.bukkit.Server;

import sk.tomsik68.realmotd.api.VariablesRegisterEvent;

final class EventVariablesProvider extends VariableProvider {

    public EventVariablesProvider(Server server) {
        super(server);
    }

    @Override
    public Map<String, Variable> provide() {
        // fire the event
        VariablesRegisterEvent event = new VariablesRegisterEvent();
        server.getPluginManager().callEvent(event);
        return event.getToRegister();
    }

}
