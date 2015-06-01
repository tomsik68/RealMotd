package sk.tomsik68.realmotd.decor;

import java.util.Random;

import org.bukkit.ChatColor;

import sk.tomsik68.realmotd.api.IMotdDecorator;

public final class RainbowDecorator implements IMotdDecorator {
    private final ChatColor[] rainbowColors;
    private static final Random rand = new Random();

    public RainbowDecorator(ChatColor[] colors) {
        rainbowColors = colors;
    }

    @Override
    public String decorate(String motd) {
        int index = 0;
        while (motd.contains("&rbow")) {
            index = motd.indexOf("&rbow") + 5;
            int endIndex = motd.indexOf(ChatColor.RESET.toString(), index);
            if (endIndex < 0) {
                endIndex = motd.length();
            }
            String substr = motd.substring(index, endIndex);
            StringBuilder replacement = new StringBuilder();
            for (int i = 0; i < substr.length(); ++i) {
                char c = substr.charAt(i);
                // don't break my newlines!!!
                if ((c == '/' && i <= substr.length() - 2 && substr.charAt(i + 1) == 'n')) {
                    i += 2;
                    replacement = replacement.append("/n");
                    continue;
                }
                replacement = replacement.append(rainbowColors[rand.nextInt(rainbowColors.length)].toString()).append(c);
            }
            replacement = replacement.append(ChatColor.RESET.toString());
            motd = motd.replaceFirst("&rbow".concat(substr), replacement.toString());
        }
        return motd;
    }
}
