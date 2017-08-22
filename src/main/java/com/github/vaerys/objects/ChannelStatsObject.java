package com.github.vaerys.objects;

public class ChannelStatsObject {
    String name;
    long count = 0;
    boolean isSetting;

    public ChannelStatsObject(String name, boolean isSetting) {
        this.name = name;
        this.isSetting = isSetting;
    }

    public void addCounts(int size) {
        count += size;
    }

    public String getName() {
        return name;
    }

    public long getCount() {
        return count;
    }

    public boolean isSetting() {
        return isSetting;
    }
}
