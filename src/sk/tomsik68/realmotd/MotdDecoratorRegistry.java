package sk.tomsik68.realmotd;

import java.util.HashMap;
import sk.tomsik68.realmotd.api.IMotdDecorator;
import sk.tomsik68.realmotd.decor.CharsColorDecorator;
import sk.tomsik68.realmotd.decor.CustomFormattingDecorator;
import sk.tomsik68.realmotd.decor.NamesColorDecorator;

public class MotdDecoratorRegistry {

    public static MotdDecoratorRegistry instance = new MotdDecoratorRegistry();
    private final HashMap<String, IMotdDecorator> decorators = new HashMap<String, IMotdDecorator>();

    private MotdDecoratorRegistry() {
    }

    public void register(String name, IMotdDecorator decorator) {
        decorators.put(name, decorator);
    }

    public IMotdDecorator getDecorator(String name) {
        if (!decorators.containsKey(name))
            try {
                Class<?> decoClass = Class.forName(name);
                IMotdDecorator result = (IMotdDecorator) decoClass.newInstance();
                decorators.put(name, result);
            } catch (Exception e) {
                throw new NullPointerException("Unknown decorator: '" + name + "'");
            }
        return decorators.get(name);
    }

    static {
        instance.register("custom_formatting", new CustomFormattingDecorator());
        instance.register("color_names", new NamesColorDecorator());
        instance.register("color_chars", new CharsColorDecorator());
    }
}
