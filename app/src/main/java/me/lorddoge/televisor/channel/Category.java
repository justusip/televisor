package me.lorddoge.televisor.channel;

import me.lorddoge.televisor.channel.Channel;

public class Category {

    private String id;
    private String name;
    private Channel[] channels;

    public Category(String id, String name, Channel[] channels) {
        this.id = id;
        this.name = name;
        this.channels = channels;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Channel[] getChannels() {
        return channels;
    }
}
