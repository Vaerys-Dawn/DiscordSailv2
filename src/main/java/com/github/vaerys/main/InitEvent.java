package com.github.vaerys.main;

import com.github.vaerys.handlers.RequestHandler;
import net.dv8tion.jda.api.events.ReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InitEvent {

    final static Logger logger = LoggerFactory.getLogger(Main.class);

    public static void initBot() {
        //makes sure that nothing in the config file will cause an error
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
