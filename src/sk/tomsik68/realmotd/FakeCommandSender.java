package sk.tomsik68.realmotd;

import java.util.Set;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.permissions.PermissionAttachmentInfo;
import org.bukkit.plugin.Plugin;

final class FakeCommandSender implements CommandSender {
	private final CommandSender sender;
	private StringBuilder sb = new StringBuilder();
	public FakeCommandSender(CommandSender perm) {
		sender = perm;
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0) {
		return sender.addAttachment(arg0);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, int arg1) {
		return sender.addAttachment(arg0, arg1);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2) {
		return sender.addAttachment(arg0, arg1, arg2);
	}

	@Override
	public PermissionAttachment addAttachment(Plugin arg0, String arg1, boolean arg2, int arg3) {
		return sender.addAttachment(arg0, arg1, arg2, arg3);
	}

	@Override
	public Set<PermissionAttachmentInfo> getEffectivePermissions() {
		return sender.getEffectivePermissions();
	}

	@Override
	public boolean hasPermission(String arg0) {
		return sender.hasPermission(arg0);
	}

	@Override
	public boolean hasPermission(Permission arg0) {
		return sender.hasPermission(arg0);
	}

	@Override
	public boolean isPermissionSet(String arg0) {
		return sender.isPermissionSet(arg0);
	}

	@Override
	public boolean isPermissionSet(Permission arg0) {
		return sender.isPermissionSet(arg0);
	}

	@Override
	public void recalculatePermissions() {
		sender.recalculatePermissions();
	}

	@Override
	public void removeAttachment(PermissionAttachment arg0) {
		sender.removeAttachment(arg0);
	}

	@Override
	public boolean isOp() {
		return sender.isOp();
	}

	@Override
	public void setOp(boolean arg0) {
		sender.setOp(arg0);
	}

	@Override
	public String getName() {
		return sender.getName();
	}

	@Override
	public Server getServer() {
		return sender.getServer();
	}

	@Override
	public void sendMessage(String msg) {
		sb = sb.append(msg).append(" ");
	}
	public String getText(){
		return sb.toString();
	}

	@Override
	public void sendMessage(String[] arg0) {
		for(String s : arg0)
			sb = sb.append(s).append(" ");
	}
}
