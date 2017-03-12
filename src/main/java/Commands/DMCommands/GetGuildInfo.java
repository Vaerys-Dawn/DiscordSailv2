package Commands.DMCommands;

import Commands.CommandObject;
import Interfaces.DMCommand;
import Commands.DMCommandObject;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by Vaerys on 17/02/2017.
 */
public class GetGuildInfo implements DMCommand {
    @Override
    public String execute(String args, DMCommandObject command) {
        IGuild guild = command.client.getGuildByID(args);
        if (guild != null) {
            Commands.Help.GetGuildInfo getGuildInfo = new Commands.Help.GetGuildInfo();
            CommandObject commandObject = new CommandObject(command.message, guild, command.channel, command.author);
            getGuildInfo.execute(args, commandObject);
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
