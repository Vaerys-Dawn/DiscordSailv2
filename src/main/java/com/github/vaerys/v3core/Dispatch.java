package com.github.vaerys.v3core;

import discord4j.core.DiscordClient;
import discord4j.core.event.domain.Event;

public class Dispatch {

    static EventSubscriberAdapter adapter = new EventSubscriberAdapter() {

    };

    protected static void runDispatch(DiscordClient client){

        client.getEventDispatcher().on(Event.class).as(adapter::listener).subscribe();
        client.get

    }

}
