package me.lorddoge.televisor.channel;

import java.util.Date;

public class Programme {

    private String name;
    private String desc;
    private Date start;
    private Date end;

    public Programme(String name, String desc, Date start, Date end) {
        this.name = name;
        this.desc = desc;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return name;
    }

    public String getDesc() {
        return desc;
    }

    public Date getStart() {
        return start;
    }

    public Date getEnd() {
        return end;
    }
}