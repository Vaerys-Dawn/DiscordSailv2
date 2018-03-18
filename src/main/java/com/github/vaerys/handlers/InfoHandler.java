package com.github.vaerys.handlers;

import com.github.vaerys.commands.CommandObject;
import com.github.vaerys.enums.TagType;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Utility;
import com.github.vaerys.tags.TagList;
import com.github.vaerys.templates.TagObject;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;
import sx.blah.discord.util.RequestBuffer;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This Class Is the Handler for the updateInfo Method it pulls the info.txt doc and then updates the channel based on the contents of the file.
 */
public class InfoHandler {
    List<String> infoContents;
    CommandObject object;
    private IGuild guild;
    private IChannel channel;

    public InfoHandler(CommandObject object) {
        this.object = object;
        this.channel = object.channel.get();
        this.guild = object.guild.get();
        RequestHandler.deleteMessage(object.message.get());
        infoContents = FileHandler.readFromFile(Utility.getFilePath(guild.getLongID(), Constants.FILE_INFO));
        updateChannel();
    }

    private void updateChannel() {
        RequestBuffer.request(() -> channel.getMessageHistory().bulkDelete());
        StringBuilder builder = new StringBuilder();
        ArrayList<String> stringChunks = new ArrayList<>();
        String lastChunk;
        String nextChunk;
        String imageTag;
        String[] splited;
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
                    splited = builder.toString().split(Pattern.quote(imageTag));
                    lastChunk = splited[0];
                    nextChunk = splited[1];
                    stringChunks.add(lastChunk);
                    stringChunks.add(imageTag);
                    builder.delete(0, builder.length());
                    builder.append(nextChunk);
                }
                // tag tells the system to save chunk and move to the next one
                if (builder.toString().contains(tagBreak)) {
                    splited = builder.toString().split(Pattern.quote(tagBreak));
                    lastChunk = splited[0];
                    nextChunk = splited[1];
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
                File file = new File(Utility.getGuildImageDir(guild.getLongID()) + image);
                RequestHandler.sendFile("", file, channel).get();
            } else {
                RequestHandler.sendMessage(contents, channel).get();
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Utility.sendStack(e);
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
