package com.github.vaerys.commands.help;


import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.ChannelObject;
import com.github.vaerys.masterobjects.ClientObject;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.DmCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.User;

import java.util.stream.Collectors;

public class BotInfo extends Command {

    @Override
    public String executeDm(String args, DmCommandObject command) {
        sendInfo(command.client, command.messageChannel, new XEmbedBuilder());
        return null;
    }

    @Override
    public String execute(String args, CommandObject command) {
        sendInfo(command.client, command.guildChannel, new XEmbedBuilder(command));
        return null;
    }

    private void sendInfo(ClientObject client, ChannelObject channel, XEmbedBuilder builder) {
        StringHandler response = new StringHandler();
        User creator = client.creator.get();
        builder.setTitle("Information about " + Globals.botName);
        response.append("Created by: **@" + creator.getName() + "#" + creator.getDiscriminator() + "**.");
        response.append("\nCreated entirely using Java 8 and the **[Discord4J Libraries](https://discord4j.com/)**.");
        response.append("\nSupport " + Globals.botName + " on **[Patreon](https://www.patreon.com/DawnFelstar)**.");
        response.append("\nFind " + Globals.botName + " on **[GitHub](https://github.com/Vaerys-Dawn/DiscordSailv2)**.");
        response.append("\nBot's Support Discord: **https://discord.gg/XSyQQrR**.");
        response.append("\n\nContributors: ");
        response.append(String.join(", ", Globals.getContributors().stream().map(u -> u.getAsTag()).collect(Collectors.toList())));
        response.append(".");
        builder.setDescription(response.toString());
        builder.setThumbnail(client.bot.avatarURL);
        builder.setFooter("Bot Version: " + Globals.version + " | D4J Version: " + Globals.d4jVersion);
        channel.queueMessage(builder.build());
    }

    @Override
    protected boolean hasDmVersion() {
        return true;
    }

    @Override
    protected String[] names() {
        return new String[]{"Info", "BotInfo"};
    }

    @Override
    public String description(CommandObject command) {
        return "Gives Information about the bot.";
    }

    @Override
    protected String usage() {
        return null;
    }

    @Override
    protected SAILType type() {
        return SAILType.HELP;
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
