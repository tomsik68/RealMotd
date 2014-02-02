package sk.tomsik68.realmotd.api;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class StringReplaceDecorator implements IMotdDecorator {
    public abstract Pattern getPattern();

    public abstract String getReplacement(String inPattern);

    @Override
    public String decorate(String motd) {
        Matcher matcher = getPattern().matcher(motd);
        while (matcher.find()) {
            String group = matcher.group();
            String repl = getReplacement(group);
            motd = motd.replace(group, repl);
        }
        return motd;
    }

}
