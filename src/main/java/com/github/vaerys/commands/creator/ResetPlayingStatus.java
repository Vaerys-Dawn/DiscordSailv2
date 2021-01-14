package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Activity;

/**
 * Created by Vaerys on 21/03/2017.
 */
public class ResetPlayingStatus extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.client.get().getPresence().setActivity(Activity.playing(Globals.playing));
        return "\\> Status reset.";
    }

    @Override
    protected String[] names() {
        return new String[]{"ResetPlayingStatus"};
    }

    @Override
    public String description(CommandObject command) {
        return "Resets the playing status.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.CREATOR;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permission[] perms() {
        return new Permission[0];
    }

    @Override
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    protected boolean doAdminLogging() {
        return false;
    }

    @Override
    public void init() {

    }
}
