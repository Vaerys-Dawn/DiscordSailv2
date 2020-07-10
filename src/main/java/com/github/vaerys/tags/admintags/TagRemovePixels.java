package com.github.vaerys.tags.admintags;

import com.github.vaerys.enums.TagType;
import com.github.vaerys.enums.UserSetting;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.adminlevel.AdminCCObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.TagAdminSubTagObject;

public class TagRemovePixels extends TagAdminSubTagObject {

    public TagRemovePixels(int priority, TagType... types) {
        super(priority, types);
    }

    @Override
    public String execute(String from, CommandObject command, String args, AdminCCObject cc) {
        String subTag = getSubTag(from);
        if (!command.guild.config.modulePixels) {
            return replaceAllTag(from, ERROR_PIXELS_DISABLED);
        }

        try {
            long pixels = Long.parseUnsignedLong(subTag);
            if ((pixels < 1 || pixels > 200)) {
                from = replaceFirstTag(from, error);
            } else {
                ProfileObject profile = command.user.getProfile();
                if (profile == null) return removeAllTag(from);
                if (profile.getSettings().contains(UserSetting.DENIED_XP) ||
                        profile.getSettings().contains(UserSetting.NO_XP_GAIN)) {
                    pixels = 0;
                } else {
                    profile.removePixels(pixels, command.guild.config);
                }
                from = replaceFirstTag(from, "You have lost " + pixels + " pixels");
                from = removeAllTag(from);
            }
        } catch (NumberFormatException e) {
            from = replaceFirstTag(from, error);
        }
        return from;
    }

    @Override
    protected String subTagUsage() {
        return "PixelCount";
    }

    @Override
    public String tagName() {
        return "removePixels";
    }

    @Override
    protected int argsRequired() {
        return 0;
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected String desc() {
        return "Removes the amount of pixels specified in the SubTag from the user's profile upon activation. Replaces itself with \"You have lost x pixels\".\n" +
                "Limit of 200 Pixels. (Affected by Pixel multiplier)";
    }
}

