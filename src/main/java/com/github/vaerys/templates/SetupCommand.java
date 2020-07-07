package com.github.vaerys.templates;

import com.github.vaerys.enums.ChannelSetting;
import com.github.vaerys.enums.SAILType;
import com.github.vaerys.templates.Command;
import sx.blah.discord.handle.obj.Permissions;

public abstract class SetupCommand extends Command {
    @Override
    protected SAILType type() {
        return SAILType.SETUP;
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
    protected String usage() {
        return null;
    }

    @Override
    protected void init() {
        //nop
    }
}
