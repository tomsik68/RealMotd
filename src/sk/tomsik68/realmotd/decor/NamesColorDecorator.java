package sk.tomsik68.realmotd.decor;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import sk.tomsik68.realmotd.api.StringReplaceDecorator;

public final class NamesColorDecorator extends StringReplaceDecorator {
    private static final Pattern pattern = Pattern.compile("&\\w*");

    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String getReplacement(String inPattern) {
        for (ChatColor cc : ChatColor.values()) {
            if (inPattern.contains("&" + cc.name().toLowerCase()))
                inPattern = inPattern.replace("&" + cc.name().toLowerCase(), cc.toString());
            if (inPattern.contains("&" + cc.name()))
                inPattern = inPattern.replace("&" + cc.name(), cc.toString());
        }
        return inPattern;
    }

}
