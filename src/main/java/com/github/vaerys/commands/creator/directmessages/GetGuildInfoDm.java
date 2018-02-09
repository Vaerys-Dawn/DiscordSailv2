package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.commands.help.GetGuildInfo;
import com.github.vaerys.main.Utility;
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

    @Override
    public String[] names() {
        return new String[]{"GetGuildInfo"};
    }

    @Override
    public String description(CommandObject command) {
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

    @Override
    public void init() {

    }
}
