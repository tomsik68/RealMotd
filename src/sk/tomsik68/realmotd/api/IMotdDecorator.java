package sk.tomsik68.realmotd.api;

public interface IMotdDecorator {
    /**
     * 
     * @param motd - MOTD before decoration
     * @return The whole MOTD, but decorated.
     */
    public String decorate(String motd);


}
