package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;

import java.util.List;
import java.util.Random;

public class TagPathRandom extends TagAdminSubTagObject {

    public TagPathRandom(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        String subTag = getSubTag(from);

        List<String> split = getSplit(from);

        if (cc.getKeysUser(command.user.longID).size() == 0 && subTag.equalsIgnoreCase("Start")) {
            from = replaceFirstTag(from, handleRandom(split));
        }
        if (cc.searchKeys(subTag, command) && args.equalsIgnoreCase(subTag)) {
            from = replaceFirstTag(from, handleRandom(split));
            cc.removeKey(subTag, command);
            from = get(TagNoPath.class).removeAllTag(from);
        } else {
            from = removeFirstTag(from);
        }
        FileHandler.writeToFile(Constants.DIRECTORY_STORAGE + "Error.txt", from, true);
        return from;

    }

    protected static String handleRandom(List<String> contents) {
        Random random = Globals.getGlobalRandom();
        return contents.get(random.nextInt(contents.size()));
    }


    @Override
    protected String subTagUsage() {
        return "PathID";
    }

    @Override
    public String tagName() {
        return "pathRandom";
    }

    @Override
    protected int argsRequired() {
        return -1;
    }

    @Override
    protected String usage() {
        return "Rand1" + splitter + "Rand2" + splitter + "Rand3...";
    }

    @Override
    protected String desc() {
        return "If the user has the Path Key specifies in the SubTag it will replace itself with one of the randomly chosen " +
                "variables of the tag, else it will remove itself.\n" +
                "However if the SubTag is set to \"Start\" it will act as if the user has that key unless they have any other keys.\n" +
                "When activated the tag will remove any keys the user may have to the path.";
    }
}
