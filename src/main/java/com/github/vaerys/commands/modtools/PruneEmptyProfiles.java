package com.github.vaerys.commands.modtools;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ListIterator;

public class PruneEmptyProfiles extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<ProfileObject> profiles = command.guild.users.getProfiles();
        ProfileObject defaultProfile = new ProfileObject(-1);
        long profileCount = 0;
        ListIterator listIterator = profiles.listIterator();
        while (listIterator.hasNext()) {
            ProfileObject profile = (ProfileObject) listIterator.next();
            boolean noXP = profile.getXP() == 0;
            boolean noGender = defaultProfile.getGender().equals(profile.getGender());
            boolean noQuote = defaultProfile.getQuote().equals(profile.getQuote());
            boolean noSettings = profile.getSettings().size() == 0;
            boolean noLinks = profile.getLinks().size() == 0;
            if (noXP && noGender && noQuote && noSettings && noLinks) {
                listIterator.remove();
                profileCount++;
            }
        }
        return "> " + NumberFormat.getInstance().format(profileCount) + " empty profiles pruned.";
    }

    @Override
    public String description(CommandObject command) {
        return "Prunes all of the empty profiles on the server.";
    }

    @Override
    public void init() {

    }

    @Override
    protected String[] names() {
        return new String[]{"PruneEmptyProfiles"};
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.MOD_TOOLS;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return true;
    }

}
