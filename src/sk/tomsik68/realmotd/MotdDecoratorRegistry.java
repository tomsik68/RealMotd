package sk.tomsik68.realmotd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import sk.tomsik68.realmotd.api.IMotdDecorator;

public class MotdDecoratorRegistry {

    public static MotdDecoratorRegistry instance = new MotdDecoratorRegistry();
    private final ArrayList<IMotdDecorator> decorators = new ArrayList<IMotdDecorator>();

    private MotdDecoratorRegistry() {
    }
    
    public void register(IMotdDecorator decorator) {
        decorators.add(decorator);
    }

    public List<IMotdDecorator> getDecorators() {
        return Collections.unmodifiableList(decorators);
    }
}
