package com.github.vaerys.handlers;

import com.github.vaerys.masterobjects.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.TagObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.TextChannel;
import sx.blah.discord.handle.obj.Guild;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This Class Is the Handler for the updateInfo Method it pulls the info.txt doc and then updates the messageChannel based on the getContents of the file.
 */
public class InfoHandler {
    List<String> infoContents;
    CommandObject object;
    private Guild guild;
    private TextChannel channel;

    public InfoHandler(CommandObject object) {
        this.object = object;
        this.channel = object.guildChannel.get();
        this.guild = object.guild.get();
        RequestHandler.deleteMessage(object.message.get());
        infoContents = FileHandler.readFromFile(Utility.getFilePath(guild.getIdLong(), Constants.FILE_INFO));
        updateChannel();
    }

    private void updateChannel() {
        RequestBuffer.request(() -> channel.getMessageHistory().bulkDelete());
        StringBuilder builder = new StringBuilder();
        ArrayList<String> stringChunks = new ArrayList<>();
        String lastChunk;
        String nextChunk;
        String imageTag;
        String[] split;
        String imagePrefix = "<image>{";
        String imageSuffix = "}";
        String tagBreak = "<split>";
        String image;

        //prep for the everything...
        for (String s : infoContents) {
            //this ignores commented out code.
            if (!s.startsWith("//")) {
                //code for image handling
                if (builder.toString().contains(imagePrefix)) {
                    imageTag = imagePrefix + StringUtils.substringBetween(builder.toString(), imagePrefix, imageSuffix) + imageSuffix;
                    split = builder.toString().split(Pattern.quote(imageTag));
                    lastChunk = split[0];
                    nextChunk = split[1];
                    stringChunks.add(lastChunk);
                    stringChunks.add(imageTag);
                    builder.delete(0, builder.length());
                    builder.append(nextChunk);
                }
                // tag tells the system to save chunk and move to the next one
                if (builder.toString().contains(tagBreak)) {
                    split = builder.toString().split(Pattern.quote(tagBreak));
                    lastChunk = split[0];
                    nextChunk = split[1];
                    stringChunks.add(lastChunk);
                    builder.delete(0, builder.length());
                    builder.append(nextChunk);
                }
                //if the char count for the current chunk is too much move to the next one
                if ((builder + doTextTags(s) + "\n").length() > 2000) {
                    stringChunks.add(builder.toString());
                    builder.delete(0, builder.length());
                }
                builder.append(doTextTags(s) + "\n");
            }
        }
        stringChunks.add(builder.toString());
        builder.delete(0, builder.length());


        //actual code.
        for (String contents : stringChunks) {
            if (contents.contains(imagePrefix)) {
                image = StringUtils.substringBetween(contents, imagePrefix, imageSuffix);
                File file = new File(Utility.getGuildImageDir(guild.getIdLong()) + image);
                RequestHandler.sendFile("", file, channel).get();
            } else {
                RequestHandler.sendMessage(contents, channel).get();
            }
        }
    }


    private String doTextTags(String s) {
        List<TagObject> tagObjects = TagList.getType(TagType.INFO);
        for (TagObject t : tagObjects) {
            s = t.handleTag(s, object, "");
        }
        return s;
    }


}
