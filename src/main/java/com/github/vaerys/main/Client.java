package com.github.vaerys.main;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.Discord4J;
import sx.blah.discord.api.ClientBuilder;
import sx.blah.discord.api.IDiscordClient;
import sx.blah.discord.api.events.IListener;
import sx.blah.discord.handle.impl.events.ReadyEvent;
import sx.blah.discord.util.DiscordException;

/**
 * Created by Vaerys on 19/05/2016.
 */
public class Client {
    final static Logger logger = LoggerFactory.getLogger(Client.class);

    public static IDiscordClient getClient(String token, boolean login) throws DiscordException {
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.withToken(token);
        clientBuilder.setMaxReconnectAttempts(4000);
        if (login) {
            logger.info("Logging in to Discord");
            return clientBuilder.login();
        } else {
            return clientBuilder.build();
        }
    }


    public static void main(String[] args) {
        IDiscordClient client = new ClientBuilder().withToken(args[0]).build();
        client.getDispatcher().registerListener((IListener<ReadyEvent>) readyEvent -> System.out.println("login successful"));
        client.login();
    }
}