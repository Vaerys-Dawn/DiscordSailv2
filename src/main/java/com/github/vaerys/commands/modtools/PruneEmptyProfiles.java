package com.github.vaerys.commands.modtools;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.userlevel.ProfileObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class PruneEmptyProfiles extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        List<Long> idsToRemove = new LinkedList<>();
        ProfileObject defaultProfile = new ProfileObject(-1);
        command.guild.users.profiles.forEach((id, profile) -> {
            boolean noXP = profile.getXP() == 0;
            boolean noGender = defaultProfile.getGender().equals(profile.getGender());
            boolean noQuote = defaultProfile.getQuote().equals(profile.getQuote());
            boolean noSettings = profile.getSettings().isEmpty();
            boolean noLinks = profile.getLinks().isEmpty();
            if (noXP && noGender && noQuote && noSettings && noLinks) {
                idsToRemove.add(id);
            }
        });
        long profileCount = idsToRemove.size();
        idsToRemove.forEach(l -> command.guild.users.profiles.remove(l));
        return "\\> " + NumberFormat.getInstance().format(profileCount) + " empty profiles pruned.";
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
