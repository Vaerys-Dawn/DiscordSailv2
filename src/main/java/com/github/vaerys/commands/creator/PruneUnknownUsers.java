package com.github.vaerys.commands.creator;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.main.Globals;
import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.masterobjects.EmptyUserObject;
import com.github.vaerys.masterobjects.GuildObject;
import com.github.vaerys.masterobjects.UserObject;
import com.github.vaerys.templates.Command;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Message;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

public class PruneUnknownUsers extends Command {

    @Override
    public String execute(String args, CommandObject command) {
        command.guildChannel.sendMessage("\\> Purging unknown users from profile list...");
        AtomicReference<AtomicLong> total = new AtomicReference<>(new AtomicLong());
        String progress = "\\> Progress: ";
        for (GuildObject g : Globals.getGuilds()) {

            int percentIncrement = (int) (2000.0F / g.users.profiles.size());

            command.guildChannel.sendMessage("\\> Purging " + g.get().getName() + "...");
            Message message = command.guildChannel.sendMessage(progress + 0 + "%");
            List<Long> toRemove = new LinkedList<>();
            AtomicLong counter = new AtomicLong();
            AtomicInteger last = new AtomicInteger();
            g.users.profiles.forEach((l, p) -> {
                UserObject object = p.getUser(command.guild);
                if (object instanceof EmptyUserObject && p.getXP() < 200) {
                    toRemove.add(l);
                }
                counter.getAndIncrement();
                int newPercent = (int) ((counter.get() / (float) g.users.profiles.size()) * 100);
                if (last.get() + percentIncrement < newPercent) {
                    message.editMessage(progress + newPercent + "%").queue();
                    last.set(newPercent);
                }
            });
            message.editMessage("\\> Complete.").queue();
            total.set(new AtomicLong(total.get().get() + toRemove.size()));
            toRemove.forEach(l -> g.users.profiles.remove(l));
        }
        return "\\> Removed " + total + " unknown profiles.";
    }

    @Override
    protected String[] names() {
        return new String[]{"PruneUnknownUsers"};
    }

    @Override
    public String description(CommandObject command) {
        return "Removes unknown users from this guild's profiles";
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
        return true;
    }

    @Override
    protected void init() {

    }
}
