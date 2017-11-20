package com.github.vaerys.objects;

/**
 * Created by Vaerys on 17/06/2017.
 */
public class UserLinkObject {
    String name;
    String link;

    public UserLinkObject(String name, String link) {
        this.name = name;
        this.link = link;
    }

    public String getName() {
        return name;
    }

    public String getLink() {
        return link;
    }

    @Override
    public String toString() {
        return "[" + name + "](" + link + ")";
    }
}
