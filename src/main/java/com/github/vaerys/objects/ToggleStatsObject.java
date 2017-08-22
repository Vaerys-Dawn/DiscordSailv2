package com.github.vaerys.objects;

public class ToggleStatsObject {
    String toggle;
    long count = 0;
    boolean isModif;

    public ToggleStatsObject(String toggle, boolean isMofif) {
        this.toggle = toggle;
        this.isModif = isMofif;
    }

    public void addOne() {
        count++;
    }

    public String getToggle() {
        return toggle;
    }

    public long getCount() {
        return count;
    }

    public boolean isModule() {
        return isModif;
    }
}
