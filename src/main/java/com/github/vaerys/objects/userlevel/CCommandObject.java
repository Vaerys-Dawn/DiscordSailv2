package com.github.vaerys.objects.userlevel;

import com.github.vaerys.masterobjects.CommandObject;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Vaerys on 17/08/2016.
 */
public class CCommandObject {

    String name;
    long userID;
    boolean isLocked;
    boolean shitPost;
    long timesRun;
    List<String> contents;

    public CCommandObject(boolean isLocked, long userID, String name, String contents, boolean shitPost) {
        this.isLocked = isLocked;
        this.userID = userID;
        this.name = name;
        this.contents = Arrays.asList(contents.split("\n"));
        this.shitPost = shitPost;
        timesRun = 0;
    }

    public void setContents(String contents) {
        this.contents = Arrays.asList(contents.split("\n"));
    }

    public String getName() {
        return name;
    }

    public long getUserID() {
        return userID;
    }

    public boolean isLocked() {
        return isLocked;
    }

    public String getContents(boolean addCounter) {
        if (addCounter) {
            timesRun++;
        }
        StringBuilder builder = new StringBuilder();
        for (String s : contents) {
            builder.append(s + "\n");
        }
        builder.delete(builder.length() - 1, builder.length());
        return builder.toString();
    }

    public long getTimesRun() {
        return timesRun;
    }

    public boolean toggleShitPost() {
        return shitPost = !shitPost;
    }

    public boolean toggleLocked() {
        return isLocked = !isLocked();
    }

    public boolean isShitPost() {
        return shitPost;
    }

    public String getName(CommandObject command) {
        return command.guild.config.getPrefixCC() + name;
    }
}
