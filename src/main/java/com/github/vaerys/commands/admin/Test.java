package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SubCommandObject;
import com.github.vaerys.templates.Command;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import sx.blah.discord.handle.obj.Permissions;
import sx.blah.discord.util.DiscordException;

import java.awt.*;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test extends Command {

    protected static final SubCommandObject object = new SubCommandObject(
            new String[]{"Boop", "Snugg"},
            "[Test Data]",
            "nothing",
            SAILType.MOD_TOOLS
    );
    String nothing = "> You didn't see anything.";

    @Override
    public String execute(String args, CommandObject command) {
//
//
//        String webHookUrl = "https://discordapp.com/api/webhooks/443687114417373184/CGcLzpTeQAWuzdXHdRSJYh_1Vc9zhOAxbRVsBBRreQS_esBgIlOAAdDX5U5z2gaZ-KPW";
//        XEmbedBuilder embed = new XEmbedBuilder(command.user);
//        embed.withDesc(args);
//        WebHookObject object = new WebHookObject(embed.build()).setUsername("Blep").setAvatarURL(command.user.getAvatarURL());
//        RequestHandler.sendWebHook(webHookUrl, object);

//        return null;

        XEmbedBuilder builder = new XEmbedBuilder(Color.RED);
        builder.withAuthorName(String.format("@%s", command.user.username));
        builder.withTitle("Message Edited.");
        builder.withDescription(args);
        builder.withFooterText(String.format("Created in #%s, Time Created", command.channel.name));
        builder.withTimestamp(command.message.getTimestamp());
        builder.send(command);


//        command.guild.characters.addDungeonChar("test");

        throw new DiscordException("TestException");
    }

    @Override
    protected String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description(CommandObject command) {
        return "Tests Things.";
    }

    @Override
    protected String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    protected SAILType type() {
        return SAILType.ADMIN;
    }

    @Override
    protected ChannelSetting channel() {
        return null;
    }

    @Override
    protected Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
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
        subCommands.add(object.appendRegex(" (\\+|-)"));
    }
}
