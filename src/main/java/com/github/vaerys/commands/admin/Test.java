package com.github.vaerys.commands.admin;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.handlers.StringHandler;
import com.github.vaerys.handlers.TimerHandler;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.objects.utils.SubCommandObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

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

    @Override
    public String execute(String args, CommandObject command) {
//        Message message = command.guildChannel.sendMessage("\\> Running Daily Reset...");
////        TimerHandler.dailyReset();
//        message.delete().complete();
        return "\\> Nothing to test right now.";
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
    protected Permission[] perms() {
        return new Permission[]{Permission.MANAGE_SERVER};
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
