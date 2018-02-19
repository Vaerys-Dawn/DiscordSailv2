package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.GetGuildInfo;
import com.github.vaerys.main.Utility;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by Vaerys on 17/02/2017.
 */
public class GetGuildInfoDm extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        IGuild guild = command.client.get().getGuildByID(Utility.stringLong(args));
        if (guild != null) {
            new GetGuildInfo().execute(args, command.setGuild(guild));
            return null;
        } else {
            return "> Guild ID Invalid";
        }
    }

    protected static final String[] NAMES = new String[]{"GetGuildInfo"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Returns with all of the information about a specific Guild.";
    }

    protected static final String USAGE = "[GuildID]";
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final boolean REQUIRES_ARGS = true;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public void init() {

    }
}
