package sk.tomsik68.realmotd.decor;

import java.util.regex.Pattern;

import org.bukkit.ChatColor;

import sk.tomsik68.realmotd.api.StringReplaceDecorator;

public class CustomFormattingDecorator extends StringReplaceDecorator {
    private Pattern pattern = Pattern.compile("&[\\w*]");

    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public String getReplacement(String inPattern) {
        // some formatting
        if (inPattern.contains("&bo")) {
            inPattern = inPattern.replaceAll("&bo", ChatColor.BOLD.toString());
        }
        if (inPattern.contains("&it")) {
            inPattern = inPattern.replaceAll("&it", ChatColor.ITALIC.toString());
        }
        if (inPattern.contains("&str")) {
            inPattern = inPattern.replaceAll("&str", ChatColor.STRIKETHROUGH.toString());
        }
        if (inPattern.contains("&ran")) {
            inPattern = inPattern.replaceAll("&ran", ChatColor.MAGIC.toString());
        }
        if (inPattern.contains("&un")) {
            inPattern = inPattern.replaceAll("&un", ChatColor.UNDERLINE.toString());
        }
        if (inPattern.contains("&no")) {
            inPattern = inPattern.replaceAll("&no", ChatColor.RESET.toString());
        }
        return inPattern;
    }

}
