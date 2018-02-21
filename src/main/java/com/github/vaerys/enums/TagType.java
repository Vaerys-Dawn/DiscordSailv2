package com.github.vaerys.enums;

public enum TagType {
    
    CC("CustomCommand"),
    INFO("Info"),
    DAILY("Daily"),
    LEVEL("LevelUp"),
    PIXEL("Pixels");
    
    private String name;
    
    TagType(String name) {
        this.name = name;
    }
    
    @Override
    public String toString() {
        return name;
    }


    public static TagType get(String type) {
        for (TagType c : values()) {
            if (c.toString().equalsIgnoreCase(type)) {
                return c;
            }
        }
        return null;
    }
}
