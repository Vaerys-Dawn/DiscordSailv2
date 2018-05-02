package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.AdminCCObject;
import com.github.vaerys.templates.TagAdminSubTagObject;

public class TagPath extends TagAdminSubTagObject {

    public TagPath(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    protected String subTagUsage() {
        return "PathID";
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        String subTag = getSubTag(from);

        String contents = contents(from);

        if (cc.getKeysUser(command.user.longID).size() == 0 && subTag.equalsIgnoreCase("Start")) {
            from = replaceFirstTag(from, contents(from));
        }
        if (cc.searchKeys(subTag, command) && args.equalsIgnoreCase(subTag)) {
            from = replaceFirstTag(from, contents);
            cc.removeKey(subTag, command);
            from = get(TagNoPath.class).removeAllTag(from);
        } else {
            from = removeFirstTag(from);
        }
        return from;
    }

    @Override
    protected String tagName() {
        return "path";
    }

    @Override
    protected int argsRequired() {
        return 1;
    }

    @Override
    protected String usage() {
        return "Contents";
    }

    @Override
    protected String desc() {
        return "If the user has the Path Key specifies in the SubTag it will replace itself with the contents of the tag, else it will remove itself.\n" +
                "However if the SubTag is set to \"Start\" it will act as if the user has that key unless they have any other keys." +
                "\nWhen activated the tag will remove any keys the user may have to the path.";
    }
}
