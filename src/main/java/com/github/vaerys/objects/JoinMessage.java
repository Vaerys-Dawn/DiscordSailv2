package com.github.vaerys.objects;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.TagObject;

import java.util.List;

public class JoinMessage {
    long creator;
    String content;

    public JoinMessage(long creator, String content) {
        this.creator = creator;
        this.content = content;
    }

    public String getContent(CommandObject command) {
        List<TagObject> tags = TagList.getType(TagType.JOIN_MESSAGES);
        String message = content;
        for (TagObject t : tags) {
            message = t.handleTag(message, command, "");
        }
        return message;
    }

    public String getContent() {
        return content;
    }

    public long getCreator() {
        return creator;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
