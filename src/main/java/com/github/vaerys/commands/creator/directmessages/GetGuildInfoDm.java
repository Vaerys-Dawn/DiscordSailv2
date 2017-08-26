package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.interfaces.DMCommand;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by Vaerys on 17/02/2017.
 */
public class GetGuildInfoDm implements DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        IGuild guild = command.client.get().getGuildByID(args);
        if (guild != null) {
            new GetGuildInfoDm().execute(args, command);
            return null;
        } else {
            return "> Guild ID Invalid";
        }
    }

    @Override
    public String[] names() {
        return new String[]{"GetGuildInfo"};
    }

    @Override
    public String description() {
        return "Returns with all of the information about a specific Guild.";
    }

    @Override
    public String usage() {
        return "[GuildID]";
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return true;
    }
}
