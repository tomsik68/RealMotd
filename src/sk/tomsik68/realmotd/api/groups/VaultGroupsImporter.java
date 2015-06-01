package sk.tomsik68.realmotd.api.groups;

import net.milkbowl.vault.permission.Permission;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.plugin.RegisteredServiceProvider;

final class VaultGroupsImporter extends GroupsImporter {

    public VaultGroupsImporter() {

    }

    @Override
    public boolean isAvailable(Server server) {
        try{
            Class.forName("net.milkbowl.vault.permission.Permission;");
        }catch(Exception e){
            return false;
        }
        RegisteredServiceProvider<Permission> provider = server.getServicesManager().getRegistration(Permission.class);
        return (provider != null) && (provider.getProvider().hasGroupSupport());
    }

    @Override
    public void importGroups(Server server, GroupsRegistry groupsRegistry) {
        RegisteredServiceProvider<Permission> provider = server.getServicesManager().getRegistration(Permission.class);
        Validate.notNull(provider, "You need to check importer availability before importing!");

        Permission perm = provider.getProvider();
        Validate.isTrue(perm.hasGroupSupport(), "You need to check importer availability before importing!");

        String checkWorld = Bukkit.getWorlds().get(0).getName();
        // there are more players, so if I only go through them once, that'd be
        // great
        for (OfflinePlayer player : Bukkit.getServer().getOfflinePlayers()) {
            String[] playerGroups = perm.getPlayerGroups(checkWorld, player.getName());
            if (playerGroups.length >= 1)
                groupsRegistry.addPlayer(player.getName(), playerGroups[0]);
        }

    }

}
