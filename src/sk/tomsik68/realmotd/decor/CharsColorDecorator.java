package sk.tomsik68.realmotd.decor;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import sk.tomsik68.realmotd.api.StringReplaceDecorator;

public class CharsColorDecorator extends StringReplaceDecorator {
    private static final Pattern pattern = Pattern.compile("&[\\w]");

    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String getReplacement(String inPattern) {
        System.out.println(inPattern);
        for(ChatColor cc : ChatColor.values()){
            inPattern = inPattern.replace("&"+cc.getChar(), cc.toString());
        }
        return inPattern;
    }

}
