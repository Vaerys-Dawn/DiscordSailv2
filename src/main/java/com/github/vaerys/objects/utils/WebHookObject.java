package com.github.vaerys.objects.utils;

import sx.blah.discord.api.internal.json.objects.EmbedObject;

import java.util.LinkedList;
import java.util.List;

public class WebHookObject {
    String content;
    String username;
    String avatar_url;
    List<EmbedObject> embeds;

    public WebHookObject(String content) {
        if (content == null) content = "";
        this.content = content;
    }

    public WebHookObject(EmbedObject embed) {
        embeds = new LinkedList<>();
        embeds.add(embed);
    }

    public WebHookObject setContent(String content) {
        this.content = content;
        return this;
    }

    public WebHookObject setUsername(String username) {
        this.username = username;
        return this;
    }

    public WebHookObject setAvatarURL(String avatar_url) {
        this.avatar_url = avatar_url;
        return this;
    }

    public WebHookObject addEmbed(EmbedObject embed) {
        embeds.add(embed);
        return this;
    }
}
