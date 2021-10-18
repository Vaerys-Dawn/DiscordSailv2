package com.github.vaerys.listeners;

import com.github.vaerys.handlers.RequestHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Main;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class ReadyListener extends ListenerAdapter {

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        if (!Globals.isCreatorValid()) {
            System.exit(Constants.EXITCODE_STOP);
        }
        Globals.loadContributors();
        Main.consoleInput();
        Globals.isReady = true;
        RequestHandler.changePresence(Globals.playing);
        RequestHandler.updateUsername(Globals.botName);
    }
}
