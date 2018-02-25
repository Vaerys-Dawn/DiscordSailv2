package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.objects.ProfileObject;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.ListIterator;

public class PruneEmptyProfiles extends Command {

    // using static as it will cause less memory to be used overall by orphaned data
    protected static final String[] NAMES = new String[]{"PruneEmptyProfiles"};
    protected static final String USAGE = null;
    protected static final SAILType COMMAND_TYPE = SAILType.ADMIN;
    protected static final ChannelSetting CHANNEL_SETTING = null;
    protected static final Permissions[] PERMISSIONS = new Permissions[]{Permissions.MANAGE_SERVER};
    protected static final boolean REQUIRES_ARGS = false;
    protected static final boolean DO_ADMIN_LOGGING = true;

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
        return NAMES;
    }

    @Override
    protected String usage() {
        return USAGE;
    }

    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    @Override
    protected ChannelSetting channel() {
        return CHANNEL_SETTING;
    }

    @Override
    protected Permissions[] perms() {
        return PERMISSIONS;
    }

    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    protected boolean doAdminLogging() {
        return DO_ADMIN_LOGGING;
    }

}
