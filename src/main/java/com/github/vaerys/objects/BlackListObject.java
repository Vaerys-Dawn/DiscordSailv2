package com.github.vaerys.objects;

import com.github.vaerys.main.Constants;

/**
 * Created by Vaerys on 27/08/2016.
 */
public class BlackListObject {
    private String name;
    private String phrase;
    private String reason;
    private String type;

    public BlackListObject(String name, String phrase) {
        this.phrase = phrase;
        this.name = name;
        type = Constants.BL_PENDING;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getPhrase() {
        return phrase;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
