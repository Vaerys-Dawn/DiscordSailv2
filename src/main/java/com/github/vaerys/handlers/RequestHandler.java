package com.github.vaerys.handlers;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.*;
import com.github.vaerys.objects.adminlevel.MutedUserObject;
import com.github.vaerys.objects.utils.WebHookObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.google.gson.Gson;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRoleAddEvent;
import net.dv8tion.jda.api.exceptions.HierarchyException;
import net.dv8tion.jda.api.exceptions.PermissionException;
import net.dv8tion.jda.api.requests.restaction.MessageAction;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


public class RequestHandler {

    final static Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static void sendError(String prefix, String message, MessageChannel channel, @Nullable Guild guild) {
        long guildID;
        if (guild == null) guildID = -1;
        else guildID = guild.getIdLong();
        String format = "%s {\"MESSAGE\": \"{0}\", \"GUILD\": {1}, \"CHANNEL\": {2}}";
        logger.debug(format, prefix, message, guildID, channel.getIdLong());
    }
//
//    private static void missingPermissions(String message, TextChannel channel) {
//        sendError("Could not send message, Missing Permissions.", message, channel);
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendMessage(String message, TextChannel channel) {
//        return RequestBuffer.request(() -> {
//            //message and messageChannel checking
//            if (!Globals.client.isReady()) return null;
//            if (message == null || message.isEmpty()) return null;
//            if (channel == null) return null;
//            if (message.length() > 2000) {
//                StringHandler error = new StringHandler("\\> Could not send message, Too large. ")
//                        .append("Please contact my developer by sending me a **Direct Message** with the **Command Name** that caused this message.");
//                sendMessage(error.toString(), channel);
//                sendError("Could not send message, Too Large.", message, channel);
//                return null;
//            }
//            if (StringUtils.containsOnly(message, "\n")) return null;
//            if (StringUtils.isBlank(message)) return null;
//            try {
//                return channel.sendMessage(message);
//            } catch (RateLimitException e) {
//                throw e;
//            } catch (MissingPermissionsException e) {
//                missingPermissions(message, channel);
//                return null;
//            } catch (DiscordException e) {
//                String stackContents = Arrays.toString(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toArray());
//                if (stackContents.contains("sx.blah.discord.handle.impl.obj.PrivateChannel.queueMessage(PrivateChannel.java") &&
//                        e.getMessage().contains("Message was unable to be sent")) {
//                    sendError("Message was unable to be sent, User Dms might be off.", message, channel);
//                } else {
//                    throw e;
//                }
//                return null;
//            }
//        });
//    }
//
//
//    public static RequestBuffer.RequestFuture<Message> sendEmbedMessage(String message, XEmbedBuilder builder, ChannelObject channel) {
//        return sendEmbedMessage(message, builder, channel.get());
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendEmbedMessage(String message, XEmbedBuilder builder, CommandObject command) {
//        return sendEmbedMessage(message, builder, command.guildChannel.get());
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendEmbed(String content, EmbedObject embedObject, TextChannel channel) {
//        return RequestBuffer.request(() -> {
//            try {
//                if (embedObject == null) return null;
//                if (channel == null) return null;
//                return channel.sendMessage(content, embedObject);
//            } catch (MissingPermissionsException e) {
//                String newContent = Utility.embedToString(embedObject);
//                if (content != null && !content.isEmpty()) {
//                    return channel.sendMessage(new StringHandler(content).append("**__EMBED__**\n").append(newContent).toString());
//                } else {
//                    return channel.sendMessage(newContent);
//                }
//            }
//        });
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendEmbed(String content, EmbedObject embedObject, ChannelObject channel) {
//        return sendEmbed(content, embedObject, channel.get());
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendEmbed(String content, EmbedObject embedObject, CommandObject command) {
//        return sendEmbed(content, embedObject, command.guildChannel.get());
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendFile(String message, File file, TextChannel channel) {
//        return RequestBuffer.request(() -> {
//            String checkedMessage = message;
//            if (file == null) throw new IllegalArgumentException("File must never be null.");
//            if (checkedMessage == null) checkedMessage = "";
//            try {
//                return channel.sendFile(checkedMessage, file);
//            } catch (MissingPermissionsException e) {
//                String debugMessage;
//                if (checkedMessage.isEmpty()) debugMessage = "FILE";
//                else debugMessage = checkedMessage + "\nFILE";
//                sendMessage("\\> Could not send File, missing permissions.", channel);
//                missingPermissions(debugMessage, channel);
//                return null;
//            } catch (FileNotFoundException e) {
//                Utility.sendStack(e);
//                return null;
//            }
//        });
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendFile(String message, String fileContents, String fileName, TextChannel channel) {
//        return RequestBuffer.request(() -> {
//            String checkedMessage = message;
//            if (checkedMessage == null) checkedMessage = "";
//            InputStream stream = new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
//            try {
//                Message sent = channel.sendFile(checkedMessage, stream, fileName);
//                stream.close();
//                return sent;
//            } catch (MissingPermissionsException e) {
//                String debugMessage;
//                if (checkedMessage.isEmpty()) debugMessage = "FILE";
//                else debugMessage = checkedMessage + "\nFILE";
//                sendMessage("\\> Could not send File, missing permissions.", channel);
//                missingPermissions(debugMessage, channel);
//                return null;
//            } catch (IOException e) {
//                return null;
//            }
//        });
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendFile(String message, File file, ChannelObject channel) {
//        return sendFile(message, file, channel.get());
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendFile(String message, File file, CommandObject command) {
//        return sendFile(message, file, command.guildChannel.get());
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendFile(String message, String fileContents, String fileName, ChannelObject channel) {
//        return sendFile(message, fileContents, fileName, channel.get());
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendFile(String message, String fileContents, String fileName, CommandObject command) {
//        return sendFile(message, fileContents, fileName, command.guildChannel.get());
//    }
//
//
//    public static RequestBuffer.RequestFuture<Message> sendFileURL(String preMessage, String imageURL, TextChannel channel, boolean loadMessage) {
//        // if url is empty send as regular Image
//        if (imageURL == null || imageURL.isEmpty()) {
//            return sendMessage(preMessage, channel);
//        }
//
//        //make sure that the message getContents is valid
//        if (StringUtils.containsOnly(preMessage, "\n") || preMessage == null || preMessage.isEmpty()) {
//            preMessage = "";
//        }
//
//        // if url isn't considered a image URL send as message.
//        if (!Utility.isImageLink(imageURL, true) || imageURL.contains("giphy.gif")) {
//            return sendMessage(preMessage + "\n" + imageURL, channel);
//        }
//        //convert into final value
//        final String message = preMessage;
//
//        //send loading message
//        Message loading = null;
//        if (loadMessage) {
//            loading = sendMessage("`Loading...`", channel).get();
//        }
//
//        //request for image to be sent.
//        RequestBuffer.RequestFuture<Message> sent = RequestBuffer.request(() -> {
//            Message sentMessage = null;
//            InputStream stream = null;
//            int responseCode = -1;
//            try {
//                //connect to the Image URL
//                HttpURLConnection connection = (HttpURLConnection) new URL(imageURL).openConnection();
//                connection.setRequestProperty("User-Agent", Constants.MOZILLA_USER_AGENT);
//
//                //getAllToggles responseCode in case of IOException;
//                responseCode = connection.getResponseCode();
//
//                //turn the image connection into an inputStream
//                stream = connection.getInputStream();
//
//                //image's file name
//                String filename = FilenameUtils.getName(new URL(imageURL).getPath());
//
//                //send file
//                sentMessage = channel.sendFile(Utility.removeMentions(message), false, stream, filename);
//
//            } catch (MissingPermissionsException e) {
//                //send message and url with url closed
//                missingPermissions("URL_FILE", channel);
//                sentMessage = sendMessage(message + " <" + imageURL + ">", channel).get();
//            } catch (RateLimitException e) {
//                //send exception back out, needed for request handling.
//                throw e;
//            } catch (MalformedURLException e) {
//                //this should never show up. seriously
//                Utility.sendStack(e);
//            } catch (SSLHandshakeException e) {
//                //something to do with the ssl handshake failed. unsure what causes this.
//                sendMessage(message + " " + imageURL + " `FAILED TO EMBED - FAILED SSL HANDSHAKE.`", channel).get();
//            } catch (IOException e) {
//                //the file failed to be grabbed.
//                String response = " `ERROR:" + responseCode + ", IMAGE FAILED TO EMBED";
//                if (responseCode == 403) {
//                    sentMessage = sendMessage(message + "\n" + imageURL + response + ", IMAGE LINK NEEDS UPDATING.`", channel).get();
//                } else if (responseCode != -1) {
//                    sentMessage = sendMessage(message + "\n" + imageURL + response + "`", channel).get();
//                } else {
//                    sentMessage = sendMessage(message + "\n" + imageURL, channel).get();
//                }
//            } catch (IllegalArgumentException e) {
//                //the host failed, unsure as to the cause. inspect.
//                if (e.getMessage().contains("http host = null")) {
//                    sendMessage(message + "\n" + imageURL + " `HTTP HOST ERROR, CHECK URL FOR ERRORS.`", channel);
//                }
//            }
//            try {
//                //close off the stream
//                if (stream != null) stream.close();
//            } catch (IOException e) {
//                //how the hell did this even happen
//                Utility.sendStack(e);
//            }
//            //return the completed message
//            return sentMessage;
//        });
//        if (loading != null) {
//            sent.get();
//            deleteMessage(loading);
//        }
//        return sent;
//    }
//
//
//    public static RequestBuffer.RequestFuture<Message> sendFileURL(String message, String imageURL, ChannelObject channel, boolean loadMessage) {
//        return sendFileURL(message, imageURL, channel.get(), loadMessage);
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendFileURL(String message, String imageURL, CommandObject command, boolean loadMessage) {
//        return sendFileURL(message, imageURL, command.guildChannel.get(), loadMessage);
//    }
//
//

    public static boolean roleManagement(Member author, Guild guild, List<Role> userRoles) {
        return roleManagement(author, guild, userRoles, false);
    }

    /***
     *
     * @param author the user to be modified
     * @param guild the guild the action is relevant to
     * @param userRoles the roles that the user should have
     * @param suppressWarnings suppress permission warnings
     * @return whether the action was successful
     */
    public static boolean roleManagement(Member author, Guild guild, List<Role> userRoles, boolean suppressWarnings) {
        try {
            List<Role> temp = userRoles.stream().filter(Objects::nonNull).collect(Collectors.toList());
            Role[] roles = temp.toArray(new Role[0]);
            List<Role> tempUserRoles = author.getRoles();
            if (tempUserRoles.containsAll(temp) && temp.containsAll(tempUserRoles)) return false;
            guild.modifyMemberRoles(author, roles).complete();
            return true;
        } catch (HierarchyException e) {
          return false;
        } catch (PermissionException e) {
            if (!suppressWarnings) {
                logger.warn("Error Editing roles of globalUser with id: " + author.getIdLong() + " on guild with id: " + guild.getIdLong() +
                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Edited roles hierarchy is too high.");
            }
            return false;
        }
    }
//
//    public static void updateAvatar(Image avatar) {
//        RequestBuffer.request(() -> {
//            try {
//                Globals.getClient().changeAvatar(avatar);
//            } catch (RateLimitException e) {
//                throw e;
//            } catch (DiscordException e) {
//                Utility.sendStack(e);
//            }
//        });
//    }
//
    public static void updateUsername(String botName) {
        Client.getClient().getSelfUser().getManager().setName(botName).complete();
    }

    public static boolean muteUser(long guildID, long userID, boolean isMuting) {
        GuildObject content = Globals.getGuildContent(guildID);
        Guild guild = Client.getClient().getGuildById(guildID);
        if (guild == null) return false;
        Member user = guild.getMemberById(userID);
        Role mutedRole = Globals.client.getRoleById(content.config.getMutedRoleID());

        if (user == null) return false;
        List<Role> newRoles = user.getRoles();
        //roles for logging
        List<Role> oldRoles = new ArrayList<>(newRoles);

        if (mutedRole == null) return false;

        if (isMuting) {
            if (content.config.muteRemovesRoles) newRoles.clear();
            newRoles.add(mutedRole);
        } else {
            MutedUserObject mutedUser = content.users.getMutedUser(userID);
            if (mutedUser != null) newRoles.addAll(mutedUser.getRoles(guild));
            newRoles.remove(mutedRole);
            content.users.mutedUsers.removeIf(m -> m.getID() == userID);
        }

        return roleManagement(user, guild, newRoles);
    }

    public static boolean muteUser(CommandObject command, boolean b) {
        return muteUser(command.guild.longID, command.user.longID, b);
    }

    public static void changePresence(String s) {
        Client.getClient().getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(s));
    }

    public static MessageAction requestEmbedImage(@Nullable String s, String imageUrl, MessageChannel channel) {
        XEmbedBuilder builder = new XEmbedBuilder(Constants.pixelColour);
        builder.setImage(imageUrl);

        Guild guild = channel instanceof TextChannel ? ((TextChannel) channel).getGuild() : null;
        if (!Utility.isImageLink(imageUrl)) {
            sendError("Could not send Embed, link is not a valid image URL", imageUrl, channel, guild);
            return null;
        }
        if (s == null || s.isEmpty()) {
            return channel.sendMessage(builder.build());
        }
        if (s.length() > 2000) {
            sendError("Could not send message, Too Large.", s, channel, guild);
            return null;
        }
        return channel.sendMessage(s).embed(builder.build());
    }
//
//    public static RequestBuffer.RequestFuture<Boolean> roleManagement(UserObject user, GuildObject content, long mutedRoleID, boolean isAdding) {
//        return roleManagement(user.get(), content.get(), mutedRoleID, isAdding);
//    }
//
//    public static void addReaction(Message message, ReactionEmoji emoji) {
//        RequestBuffer.request(() -> {
//            try {
//                message.addReaction(emoji);
//            } catch (MissingPermissionsException e) {
//                logger.debug("Could not add reaction to message. Reason: Missing Permission ADD_REACTIONS\n" +
//                        "MessageID: " + message.getIdLong() + ", ReactionID: " +
//                        (emoji.isUnicode() ? emoji.getName() : emoji.getIdLong()) + ", GuildID: " +
//                        (message.getGuild() == null ? -1 : message.getGuild().getIdLong()));
//            }
//        });
//    }
//
//    public static void addReaction(MessageObject message, ReactionEmoji emoji) {
//        addReaction(message.get(), emoji);
//    }
//
//    public static void roleManagement(CommandObject command, Role role, boolean isAdding) {
//        roleManagement(command.user.get(), command.guild.get(), role.getIdLong(), isAdding);
//    }
//
//    public static void sendWebHook(String webHookUrl, WebHookObject object) {
//        try {
//            URL url = new URL(webHookUrl);
//            URLConnection con = url.openConnection();
//            con.setRequestProperty("User-Agent", Constants.MOZILLA_USER_AGENT);
//            HttpURLConnection http = (HttpURLConnection) con;
//            http.setRequestMethod("POST"); // PUT is another valid option
//            http.setDoOutput(true);
//            Gson gson = new Gson();
//            String json = gson.toJson(object);
//            byte[] contents = json.getBytes(StandardCharsets.UTF_8);
//            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
//            http.setFixedLengthStreamingMode(contents.length);
//            try (OutputStream os = http.getOutputStream()) {
//                os.write(contents);
//                os.close();
//            }
//        } catch (ProtocolException e) {
//            e.printStackTrace();
//        } catch (MalformedURLException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public static RequestBuffer.RequestFuture<Boolean> roleManagement(UserObject user, GuildObject guild, List<Role> roles) {
//        return roleManagement(user.get(), guild.get(), roles);
//    }
//
//    public static RequestBuffer.RequestFuture<Message> sendCreatorDm(String s) {
//        TextChannel creatorDm = RequestBuffer.request(() -> Globals.getCreator().getOrCreatePMChannel()).get();
//        return sendMessage(s, creatorDm);
//    }
//
//    public static RequestBuffer.RequestFuture<Boolean> roleManagement(UserObject u, GuildObject content, Role topTenRole, boolean b) {
//        long roleID = topTenRole.getIdLong();
//        return roleManagement(u, content, roleID, b);
//    }

}
