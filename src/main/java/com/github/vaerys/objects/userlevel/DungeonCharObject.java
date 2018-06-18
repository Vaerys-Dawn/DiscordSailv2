package com.github.vaerys.objects.userlevel;

import com.github.vaerys.enums.DungeonStat;

import java.util.HashMap;
import java.util.Map;

public class DungeonCharObject {
    private final String charID;
    private String classSpec = "N/a";
    private String race = "Unknown";
    private Map<DungeonStat, Integer> stats = new HashMap<>();

    public DungeonCharObject(String charID) {
        this.charID = charID;
        for (DungeonStat s : DungeonStat.values()) stats.put(s, 0);
    }

    public void setClassSpec(String classSpec) {
        this.classSpec = classSpec;
    }

    public void setRace(String race) {
        this.race = race;
    }

    public String getCharID() {
        return charID;
    }

    public String getClassSpec() {
        return classSpec;
    }

    public String getRace() {
        return race;
    }

    public int getStat(DungeonStat stat) {
        return stats.get(stat);
    }

    public Map<DungeonStat, Integer> getStats() {
        return stats;
    }
}
