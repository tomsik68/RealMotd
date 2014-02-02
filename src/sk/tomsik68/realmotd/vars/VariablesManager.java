package sk.tomsik68.realmotd.vars;

import java.util.HashMap;
import java.util.HashSet;

import sk.tomsik68.realmotd.RealMotd;

public class VariablesManager {
    public static VariablesManager instance = new VariablesManager();
    private HashMap<String, Variable> vars = new HashMap<String, Variable>();

    private VariablesManager() {

    }

    public HashMap<String, Variable> getVariables() {
        return vars;
    }

    public void registerVariable(String name, Variable var) {
        if (vars.containsKey(name)) {
            RealMotd.log.warning(String.format("[RealMotd] Variable name conflict at '%s': '%s' vs '%s' ", name, var.getClass().getName(), vars.get(name).getClass().getName()));
        } else
            vars.put(name, var);
    }

    public void reloadVariables(RealMotd plugin) {
        vars.clear();
        HashSet<VariableProvider> providers = new HashSet<VariableProvider>();
        providers.add(new ScoreboardVariableProvider(plugin.getServer()));
        providers.add(new DefaultVariablesProvider(plugin.getServer()));
        providers.add(new EventVariablesProvider(plugin.getServer()));
        for (VariableProvider vp : providers) {
            vars.putAll(vp.provide());
        }
    }
}
