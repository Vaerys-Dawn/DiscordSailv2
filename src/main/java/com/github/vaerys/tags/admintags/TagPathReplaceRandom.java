package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.objects.ReplaceObject;
import com.github.vaerys.templates.TagAdminReplaceObject;

import java.util.List;

public class TagPathReplaceRandom extends TagAdminReplaceObject {

    public TagPathReplaceRandom(int priority, TagType... types) {
        super(priority, types);
    }

    public String execute(String from, CommandObject command, String args, AdminCCObject cc, List<ReplaceObject> toReplace) {
        String subTag = getSubTag(from);

        List<String> split = getSplit(from);

        if (cc.getKeysUser(command.user.longID).size() == 0 && subTag.equalsIgnoreCase("Start")) {
            toReplace.add(new ReplaceObject(split.get(0), TagPathRandom.handleRandom(split.subList(1, split.size()))));
            from = removeFirstTag(from);
        } else if (cc.searchKeys(subTag, command) && args.equalsIgnoreCase(subTag)) {
            toReplace.add(new ReplaceObject(split.get(0), TagPathRandom.handleRandom(split.subList(1, split.size()))));
            from = removeFirstTag(from);
            cc.removeKey(subTag, command);
            from = get(TagNoPath.class).removeAllTag(from);
        } else {
            from = removeFirstTag(from);
        }
        return from;
    }

    @Override
    protected String subTagUsage() {
        return "PathID";
    }

    @Override
    protected String tagName() {
        return "pathReplaceRandom";
    }

    @Override
    protected int argsRequired() {
        return -2;
    }

    @Override
    protected String usage() {
        return "ReplaceThis" + splitter + "WithThis" + splitter + "OrThis...";
    }

    @Override
    protected String desc() {
        return "If the user has the Path Key specifies in the SubTag it will replace the first variable with one of the randomly " +
                "chosen variables of the tag and then remove itself, else it will just remove itself.\n" +
                "However if the SubTag is set to \"Start\" it will act as if the user has that key unless they have any other keys.\n" +
                "When activated the tag will remove any keys the user may have to the path.";
    }
}
