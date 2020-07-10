package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.Guild;

import java.util.ArrayList;

/**
 * Created by Vaerys on 17/02/2017.
 */
public class GetGuildList extends DMCommand {

    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> guilds = new ArrayList<>();
        for (Guild g : command.client.get().getGuilds()) {
            guilds.add(g.getName() + ": " + g.getIdLong());
        }
        XEmbedBuilder builder = new XEmbedBuilder(command);
        Utility.listFormatterEmbed("List Of Guilds", builder, guilds, false);
        command.user.queueDm(builder.build());
        return null;
    }

    @Override
    protected String[] names() {
        return new String[]{"GetGuildList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives a list of all the Guilds the bot is connected to.";
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
    protected boolean requiresArgs() {
        return false;
    }

    @Override
    public void init() {

    }
}
