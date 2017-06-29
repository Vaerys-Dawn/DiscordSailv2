package Handlers;

import Main.Constants;
import Main.TagSystem;
import Main.Utility;
import org.apache.commons.lang3.StringUtils;
import sx.blah.discord.handle.obj.IChannel;
import sx.blah.discord.handle.obj.IGuild;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * This Class Is the Handler for the updateInfo Method it pulls the info.txt doc and then updates the channel based on the contents of the file.
 */
public class InfoHandler {
    private IGuild guild;
    private IChannel channel;
    List<String> infoContents;

    public InfoHandler(IChannel channel, IGuild guild) {
        this.channel = channel;
        this.guild = guild;
        infoContents = FileHandler.readFromFile(Utility.getFilePath(guild.getStringID(), Constants.FILE_INFO));
        updateChannel();
    }

    private void updateChannel() {
        if (channel.getMessageHistory().size() > 1) {
            Utility.deleteMessage(channel.getMessageHistory());
        }
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
        for (String s: infoContents){
            //this ignores commented out code.
            if (!s.startsWith("//")){
                //code for image handling
                if (builder.toString().contains(imagePrefix)){
                    imageTag = imagePrefix + StringUtils.substringBetween(builder.toString(),imagePrefix,imageSuffix) + imageSuffix;
                    splited = builder.toString().split(Pattern.quote(imageTag));
                    lastChunk = splited[0];
                    nextChunk = splited[1];
                    stringChunks.add(lastChunk);
                    stringChunks.add(imageTag);
                    builder.delete(0,builder.length());
                    builder.append(nextChunk);
                }
                // tag tells the system to save chunk and move to the next one
                if (builder.toString().contains(tagBreak)){
                    splited = builder.toString().split(Pattern.quote(tagBreak));
                    lastChunk = splited[0];
                    nextChunk = splited[1];
                    stringChunks.add(lastChunk);
                    builder.delete(0,builder.length());
                    builder.append(nextChunk);
                }
                //if the char count for the current chunk is too much move to the next one
                if ((builder + doTextTags(s) + "\n").length() > 2000){
                    stringChunks.add(builder.toString());
                    builder.delete(0, builder.length());
                }
                builder.append(doTextTags(s) + "\n");
            }
        }
        stringChunks.add(builder.toString());
        builder.delete(0, builder.length());


        //actual code.
        for (String contents : stringChunks){
            contents = TagSystem.tagNoNL(contents); //noNL tag
            contents = TagSystem.tagEmoji(contents,guild); //Emoji tag
            if (contents.contains(imagePrefix)){
                image = StringUtils.substringBetween(contents, imagePrefix, imageSuffix);
                File file = new File(Utility.getGuildImageDir(guild.getStringID()) + image);
                Utility.sendFile("",file,channel);
            }else {
                Utility.sendMessage(contents,channel);
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


    private String doTextTags(String s) {
        s = TagSystem.tagChannel(s); //links the channel
        s = TagSystem.tagDisplayName(s, guild); //links a non ping displayname
        s = TagSystem.tagSpacer(s); //spacer tag
        return s;
    }


}
