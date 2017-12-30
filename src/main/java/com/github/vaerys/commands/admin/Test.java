package com.github.vaerys.commands.admin;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.main.Utility;
import com.github.vaerys.objects.XEmbedBuilder;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

/**
 * Created by Vaerys on 30/01/2017.
 */
public class Test implements Command {

    String nothing = "> You didn't see anything.";

    @Override
    public String execute(String args, CommandObject command) {

        XEmbedBuilder builder = new XEmbedBuilder(command);
        builder.withAuthorName("Note 1 - " +command.user.displayName);
        builder.withAuthorIcon(command.user.getAvatarURL());
        builder.withDescription("blah blah this is a note.\n\n" +
                "`Last edited: " + Utility.formatTime(10, true) + " ago.`");
        builder.withTimestamp(command.message.getTimestamp());
        builder.withFooterText("Created by " + command.client.bot.displayName);
        builder.withFooterIcon(command.client.bot.getAvatarURL());
        builder.send(command.channel);


//        MessageTokenizer tokenizer = new MessageTokenizer(command.message.get());
//        tokenizer.nextEmoji();
//        tokenizer.hasNextEmoji();
//                XEmbedBuilder builder = new XEmbedBuilder(command);
//        builder.withAuthorName("User Info: Andirel Chaoti");
//        builder.withThumbnail("https://cdn.discordapp.com/avatars/175442602508812288/b8789f393487be6468c9ab3f634684d8.webp");
//        builder.withDesc("**Total Pixels:** 474,540\n" +
//                "**Total CCs:** 38\n" +
//                "**Total Chars:** 2\n" +
//                "**Total Server Listings:** 0\n" +
//                "**Total DailyMessages:** 6\n" +
//                "**Total Reminders:** 0");
//        builder.appendField("Mod Notes:",
//                "**Note #1:** \nmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmmm...\n" +
//                        "**Note #2:** \nDawn's The best.\n" +
//                        "**Note #3:** \nHello world.\n" +
//                        "**Note #4:** \nDo the thinger.\n\n>> **ModNote [@User] Info [Index]** <<", true);
//        builder.send(command.channel);
//        command.channel.get().setTypingStatus(true);
//        RequestBuffer.request(() -> command.channel.get().sendMessage("hello world"));
//
//        RequestBuffer.request(() -> command.client.get().getOrCreatePMChannel(command.user.get()).sendMessage("hello world"));
//        RequestBuffer.request(() -> command.client.get().getOrCreatePMChannel(command.user.get()).sendMessage("hello world"));
//        RequestBuffer.request(() -> command.client.get().getOrCreatePMChannel(command.user.get()).sendMessage("hello world"));
//        RequestBuffer.request(() -> command.client.get().getOrCreatePMChannel(command.user.get()).sendMessage("hello world"));
//        RequestBuffer.request(() -> command.client.get().getOrCreatePMChannel(command.user.get()).sendMessage("hello world"));
//        RequestBuffer.request(() -> command.client.get().getOrCreatePMChannel(command.user.get()).sendMessage("hello world"));

//        IChannel logging = command.client.get().getChannelByID(254790205851041794L);
//        IMessage message1 = RequestBuffer.request(() -> {
//            return command.channel.get().sendMessage("Hello 1");
//        }).get();
//        IMessage message2 = RequestBuffer.request(() -> {
//            return command.channel.get().sendMessage("Hello 2");
//        }).get();
//        IMessage message3 = RequestBuffer.request(() -> {
//            return command.channel.get().sendMessage("Hello 3");
//        }).get();
//        IMessage message4 = RequestBuffer.request(() -> {
//            return command.channel.get().sendMessage("Hello 4");
//        }).get();
//        IMessage message5 = RequestBuffer.request(() -> {
//            return command.channel.get().sendMessage("Hello 5");
//        }).get();
//        IMessage message6 = RequestBuffer.request(() -> {
//            return command.channel.get().sendMessage("Hello 6");
//        }).get();
//        RequestBuffer.request(() -> message1.delete());
//        RequestHandler.sendDM("Hello 1",command.user.longID);
////        RequestBuffer.request(() -> command.user.get().getOrCreatePMChannel().sendMessage("Hello 1"));
//        RequestBuffer.request(() -> logging.sendMessage("Hello 7"));
//
//        RequestBuffer.request(() -> message2.delete());
//        RequestHandler.sendDM("Hello 2",command.user.longID);
//        RequestBuffer.request(() -> logging.sendMessage("Hello 8"));
//
//        RequestBuffer.request(() -> message3.delete());
//        RequestHandler.sendDM("Hello 3",command.user.longID);
//        RequestBuffer.request(() -> logging.sendMessage("Hello 9"));
//
//        RequestBuffer.request(() -> message4.delete());
//        RequestHandler.sendDM("Hello 4",command.user.longID);
//        RequestBuffer.request(() -> logging.sendMessage("Hello 10"));
//
//        RequestBuffer.request(() -> message5.delete());
//        RequestHandler.sendDM("Hello 5",command.user.longID);
//        RequestBuffer.request(() -> logging.sendMessage("Hello 11"));
//
//        RequestBuffer.request(() -> message6.delete());
//        RequestHandler.sendDM("Hello 6",command.user.longID);
//        RequestBuffer.request(() -> logging.sendMessage("Hello 12"));


//        try {
//            Thread.sleep(15000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        RequestBuffer.request(() -> command.channel.get().setTypingStatus(true));
//        return null;
        return nothing;
//        throw new DiscordException("TestException");
//        return nothing;
    }


    @Override
    public String[] names() {
        return new String[]{"Test", "Testing"};
    }

    @Override
    public String description(CommandObject command) {
        return "Tests Things.";
    }

    @Override
    public String usage() {
        return "[Lol this command has no usages XD]";
    }

    @Override
    public String type() {
        return TYPE_ADMIN;
    }

    @Override
    public String channel() {
        return null;
    }

    @Override
    public Permissions[] perms() {
        return new Permissions[]{Permissions.MANAGE_SERVER};
    }

    @Override
    public boolean requiresArgs() {
        return false;
    }

    @Override
    public boolean doAdminLogging() {
        return false;
    }

    @Override
    public String dualDescription() {
        return null;
    }

    @Override
    public String dualUsage() {
        return null;
    }

    @Override
    public String dualType() {
        return null;
    }

    @Override
    public Permissions[] dualPerms() {
        return new Permissions[0];
    }
}
