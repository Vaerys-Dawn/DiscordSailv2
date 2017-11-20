package com.github.vaerys.commands.creator.directmessages;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.DMCommand;
import sx.blah.discord.handle.obj.IGuild;

import java.util.ArrayList;

/**
 * Created by Vaerys on 17/02/2017.
 */
public class GetGuildList implements DMCommand {
    @Override
    public String execute(String args, CommandObject command) {
        ArrayList<String> guilds = new ArrayList<>();
        for (IGuild g: command.client.get().getGuilds()){
            guilds.add(g.getName() +": " + g.getLongID());
        }
        XEmbedBuilder builder = new XEmbedBuilder();
        Utility.listFormatterEmbed("List Of Guilds", builder, guilds, false);
        Utility.sendDMEmbed("",builder,command.user.longID);
        return null;
    }

    @Override
    public String[] names() {
        return new String[]{"GetGuildList"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives a list of all the Guilds the bot is connected to.";
    }

    @Override
    public String usage() {
        return null;
    }

    @Override
    public String type() {
        return TYPE_CREATOR;
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }
}