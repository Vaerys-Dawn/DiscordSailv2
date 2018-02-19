package com.github.vaerys.commands.creator.directmessages;

import java.util.ArrayList;
import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.SAILType;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.IGuild;

/**
 * Created by Vaerys on 17/02/2017.
 */
public class GetGuildList extends DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> guilds = new ArrayList<>();
        for (IGuild g: command.client.get().getGuilds()){
            guilds.add(g.getName() +": " + g.getLongID());
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        Utility.listFormatterEmbed("List Of Guilds", builder, guilds, false);
        command.user.sendEmbededDm("",builder);
        return null;
    }

    protected static final String[] NAMES = new String[]{"GetGuildList"};
    @Override
    protected String[] names() {
        return NAMES;
    }

    @Override
    public String description(CommandObject command) {
        return "Gives a list of all the Guilds the bot is connected to.";
    }

    protected static final String USAGE = null;
    @Override
    protected String usage() {
        return USAGE;
    }

    protected static final SAILType COMMAND_TYPE = SAILType.CREATOR;
    @Override
    protected SAILType type() {
        return COMMAND_TYPE;
    }

    protected static final boolean REQUIRES_ARGS = false;
    @Override
    protected boolean requiresArgs() {
        return REQUIRES_ARGS;
    }

    @Override
    public void init() {

    }
}