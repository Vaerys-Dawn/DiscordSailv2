package com.github.vaerys.v3core;

import com.github.vaerys.handlers.FileHandler;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.event.domain.Event;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Sailv3 {

    final static Logger logger = LoggerFactory.getLogger(Sailv3.class);

    private static DiscordClient client;

    private static String token;

    public static DiscordClient login(String token){
        client = new DiscordClientBuilder(token).build();
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(ready -> System.out.println("Logged in as " + ready.getSelf().getUsername()));
        client.login().block();
        return client;
    }

    public static DiscordClient getClient() {
        return client;
    }

    public static void main(String[] args) {

        logger.info("Starting bot...");

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info(">>> Running Shutdown Process <<<");
            if (Globals.savingFiles) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Utility.sendStack(e);
                }
            }
            Globals.saveFiles(true);
        }));

        try {
            token = FileHandler.readFromFile(Constants.FILE_TOKEN).get(0);
        } catch (IndexOutOfBoundsException e) {
            logger.error("!!!BOT TOKEN NOT VALID PLEASE CHECK \"Storage/Token.txt\" AND UPDATE THE TOKEN!!!");
            System.exit(Constants.EXITCODE_STOP);
        }

        login(token);
        Dispatch.runDispatch(client);
    }




}
