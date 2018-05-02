package com.github.vaerys.objects;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.tags.admintags.TagLimitTry;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Pattern;

public class AdminCCObject {

    private transient List<TriVar<Long, String, Long>> keysToCull = new LinkedList<>();

    String name;
    long creatorID;
    long timesRun;
    String contents;

    private List<TriVar<Long, String, Long>> pathKeys = new LinkedList<>();

    public AdminCCObject(String name, long userID, String contents) {
        this.name = name;
        this.creatorID = userID;
        this.contents = contents;
        this.timesRun = 0;
    }

    public String getName() {
        return name;
    }

    public String getName(CommandObject command) {
        return command.guild.config.getPrefixAdminCC() + name;
    }

    public long getCreatorID() {
        return creatorID;
    }

    public long getTimesRun() {
        return timesRun;
    }

    public String getContents(boolean addCounter) {
        if (addCounter) timesRun++;
        if (keysToCull == null) keysToCull = new LinkedList<>();
        return contents;
    }

    public boolean hasLimitTry() {
        TagLimitTry tag = TagList.getTag(TagLimitTry.class);
        return Pattern.compile(tag.prefix + tag.group + tag.suffix).matcher(contents).find();
    }

    public List<TriVar<Long, String, Long>> getPathKeys() {
        return pathKeys;
    }

    public boolean searchKeys(String subTag, CommandObject command) {
        for (TriVar<Long, String, Long> key : pathKeys) {
            if (key.getVar1() == command.user.longID && subTag.equalsIgnoreCase(key.var2)) return true;
        }
        return false;
    }

    public List<TriVar<Long, String, Long>> getKeysUser(long longID) {
        List<TriVar<Long, String, Long>> keys = new LinkedList<>();
        for (TriVar<Long, String, Long> key : pathKeys) {
            if (key.getVar1() == longID) {
                keys.add(key);
            }
        }
        return keys;
    }

    public void grantKey(String subTag, CommandObject command) {
        TriVar<Long, String, Long> key = getKey(subTag, command);
        if (key == null) {
            pathKeys.add(new TriVar<>(command.user.longID, subTag, command.message.get().getTimestamp().toEpochMilli()));
        } else {
            key.setVar3(command.message.get().getTimestamp().toEpochMilli());
        }
    }

    public TriVar<Long, String, Long> getKey(String subTag, CommandObject command) {
        for (TriVar<Long, String, Long> key : pathKeys) {
            if (key.getVar1() == command.user.longID && subTag.equalsIgnoreCase(key.var2)) {
                return key;
            }
        }
        return null;
    }

    public void removeKey(String subTag, CommandObject command) {
        for (TriVar<Long, String, Long> key : pathKeys) {
            if (key.getVar1() == command.user.longID && key.getVar2().equalsIgnoreCase(subTag)) {
                addKeytoCull(key);

            }
        }
    }

    private void addKeytoCull(TriVar<Long, String, Long> key) {
        if (keysToCull.contains(key)) return;
        keysToCull.add(key);
    }

    public void cullKeys() {
        pathKeys.removeAll(keysToCull);
        keysToCull.clear();
    }

    public void removeAllKeys() {
        pathKeys.clear();
    }

    public void removeAllKeys(CommandObject command) {
        ListIterator iterator = pathKeys.listIterator();
        while (iterator.hasNext()) {
            TriVar<Long, String, Long> key = (TriVar<Long, String, Long>) iterator.next();
            if (key.getVar1() == command.user.longID) {
                iterator.remove();
            }
        }
    }

    public void setContents(String contents) {
        this.contents = contents;
    }
}
