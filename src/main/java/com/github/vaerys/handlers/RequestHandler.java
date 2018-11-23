package com.github.vaerys.handlers;

import com.github.vaerys.main.Client;
import com.github.vaerys.main.Constants;
import com.github.vaerys.main.Globals;
import com.github.vaerys.main.Utility;
import com.github.vaerys.masterobjects.*;
import com.github.vaerys.objects.utils.WebHookObject;
import com.github.vaerys.utilobjects.XEmbedBuilder;
import com.google.gson.Gson;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sx.blah.discord.api.internal.json.objects.EmbedObject;
import sx.blah.discord.handle.impl.events.guild.member.UserRoleUpdateEvent;
import sx.blah.discord.handle.impl.obj.ReactionEmoji;
import sx.blah.discord.handle.obj.*;
import sx.blah.discord.util.*;

import javax.net.ssl.SSLHandshakeException;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@SuppressWarnings("CaughtExceptionImmediatelyRethrown")
public class RequestHandler {


    final static Logger logger = LoggerFactory.getLogger(RequestHandler.class);

    private static void sendError(String prefix, String message, IChannel channel) {
        long guildID;
        if (channel.isPrivate()) guildID = -1;
        else guildID = channel.getGuild().getLongID();
        String format = "%s {\"MESSAGE\": \"%s\", \"GUILD\": %d, \"CHANNEL\": %d}";
        logger.debug(String.format(format, prefix, message, guildID, channel.getLongID()));
    }

    private static void missingPermissions(String message, IChannel channel) {
        sendError("Could not send message, Missing Permissions.", message, channel);
    }

    public static RequestBuffer.RequestFuture<IMessage> sendMessage(String message, IChannel channel) {
        return RequestBuffer.request(() -> {
            //message and channel checking
            if (!Globals.client.isReady()) return null;
            if (message == null || message.isEmpty()) return null;
            if (channel == null) return null;
            if (message.length() > 2000) {
                StringHandler error = new StringHandler("> Could not send message, Too large. ")
                        .append("Please contact my developer by sending me a **Direct Message** with the **Command Name** that caused this message.");
                sendMessage(error.toString(), channel);
                sendError("Could not send message, Too Large.", message, channel);
                return null;
            }
            if (StringUtils.containsOnly(message, "\n")) return null;
            if (StringUtils.isBlank(message)) return null;
            try {
                return channel.sendMessage(message);
            } catch (RateLimitException e) {
                throw e;
            } catch (MissingPermissionsException e) {
                missingPermissions(message, channel);
                return null;
            } catch (DiscordException e) {
                String stackContents = Arrays.toString(Arrays.stream(e.getStackTrace()).map(StackTraceElement::toString).toArray());
                if (stackContents.contains("sx.blah.discord.handle.impl.obj.PrivateChannel.sendMessage(PrivateChannel.java") &&
                        e.getMessage().contains("Message was unable to be sent")) {
                    sendError("Message was unable to be sent, User Dms might be off.", message, channel);
                } else {
                    throw e;
                }
                return null;
            }
        });
    }

    public static RequestBuffer.RequestFuture<IMessage> sendMessage(String message, ChannelObject channel) {
        return sendMessage(message, channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendMessage(String message, CommandObject command) {
        return sendMessage(message, command.channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendEmbedMessage(String message, XEmbedBuilder builder, IChannel channel) {
        return RequestBuffer.request(() -> {
            String checkedMessage = message;
            if (builder == null) throw new IllegalArgumentException("Embed builder must never be null.");
            if (checkedMessage == null) checkedMessage = "";
            if (checkedMessage.length() > 2000) {
                StringHandler error = new StringHandler("> Could not send message, Too large. ")
                        .append("Please contact my developer by sending me a **Direct Message** with the **Command Name** that caused this message.");
                sendMessage(error.toString(), channel);
                sendError("Could not send message, Too Large.", message, channel);
                return null;
            }
            EmbedObject embed = builder.build();
            try {
                return channel.sendMessage(checkedMessage, embed);
            } catch (MissingPermissionsException e) {
                String debugMessage;
                if (checkedMessage.isEmpty()) debugMessage = Utility.embedToString(embed);
                else debugMessage = checkedMessage + "\n" + Utility.embedToString(embed);
                missingPermissions("EMBED_LINKS", channel);
                return sendMessage(debugMessage, channel).get();
            }
        });
    }

    public static RequestBuffer.RequestFuture<IMessage> sendEmbedMessage(String message, XEmbedBuilder builder, ChannelObject channel) {
        return sendEmbedMessage(message, builder, channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendEmbedMessage(String message, XEmbedBuilder builder, CommandObject command) {
        return sendEmbedMessage(message, builder, command.channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendEmbed(String content, EmbedObject embedObject, IChannel channel) {
        return RequestBuffer.request(() -> {
            try {
                if (embedObject == null) return null;
                if (channel == null) return null;
                return channel.sendMessage(content, embedObject);
            } catch (MissingPermissionsException e) {
                String newContent = Utility.embedToString(embedObject);
                if (content != null && !content.isEmpty()) {
                    return channel.sendMessage(new StringHandler(content).append("**__EMBED__**\n").append(newContent).toString());
                } else {
                    return channel.sendMessage(newContent);
                }
            }
        });
    }

    public static RequestBuffer.RequestFuture<IMessage> sendEmbed(String content, EmbedObject embedObject, ChannelObject channel) {
        return sendEmbed(content, embedObject, channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendEmbed(String content, EmbedObject embedObject, CommandObject command) {
        return sendEmbed(content, embedObject, command.channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendFile(String message, File file, IChannel channel) {
        return RequestBuffer.request(() -> {
            String checkedMessage = message;
            if (file == null) throw new IllegalArgumentException("File must never be null.");
            if (checkedMessage == null) checkedMessage = "";
            try {
                return channel.sendFile(checkedMessage, file);
            } catch (MissingPermissionsException e) {
                String debugMessage;
                if (checkedMessage.isEmpty()) debugMessage = "FILE";
                else debugMessage = checkedMessage + "\nFILE";
                sendMessage("> Could not send File, missing permissions.", channel);
                missingPermissions(debugMessage, channel);
                return null;
            } catch (FileNotFoundException e) {
                Utility.sendStack(e);
                return null;
            }
        });
    }

    public static RequestBuffer.RequestFuture<IMessage> sendFile(String message, String fileContents, String fileName, IChannel channel) {
        return RequestBuffer.request(() -> {
            String checkedMessage = message;
            if (checkedMessage == null) checkedMessage = "";
            InputStream stream = new ByteArrayInputStream(fileContents.getBytes(StandardCharsets.UTF_8));
            try {
                IMessage sent = channel.sendFile(checkedMessage, stream, fileName);
                stream.close();
                return sent;
            } catch (MissingPermissionsException e) {
                String debugMessage;
                if (checkedMessage.isEmpty()) debugMessage = "FILE";
                else debugMessage = checkedMessage + "\nFILE";
                sendMessage("> Could not send File, missing permissions.", channel);
                missingPermissions(debugMessage, channel);
                return null;
            } catch (IOException e) {
                return null;
            }
        });
    }

    public static RequestBuffer.RequestFuture<IMessage> sendFile(String message, File file, ChannelObject channel) {
        return sendFile(message, file, channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendFile(String message, File file, CommandObject command) {
        return sendFile(message, file, command.channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendFile(String message, String fileContents, String fileName, ChannelObject channel) {
        return sendFile(message, fileContents, fileName, channel.get());
    }

    public static RequestBuffer.RequestFuture<IMessage> sendFile(String message, String fileContents, String fileName, CommandObject command) {
        return sendFile(message, fileContents, fileName, command.channel.get());
    }


    public static RequestBuffer.RequestFuture<IMessage> sendFileURL(String preMessage, String imageURL, IChannel channel, boolean loadMessage) {
        // if url is empty send as regular Image
        if (imageURL == null || imageURL.isEmpty()) {
            return sendMessage(preMessage, channel);
        }

        //make sure that the message getContents is valid
        if (StringUtils.containsOnly(preMessage, "\n") || preMessage == null || preMessage.isEmpty()) {
            preMessage = "";
        }

        // if url isn't considered a image URL send as message.
        if (!Utility.isImageLink(imageURL, true) || imageURL.contains("giphy.gif")) {
            return sendMessage(preMessage + "\n" + imageURL, channel);
        }
        //convert into final value
        final String message = preMessage;

        //send loading message
        IMessage loading = null;
        if (loadMessage) {
            loading = sendMessage("`Loading...`", channel).get();
        }

        //request for image to be sent.
        RequestBuffer.RequestFuture<IMessage> sent = RequestBuffer.request(() -> {
            IMessage sentMessage = null;
            InputStream stream = null;
            int responseCode = -1;
            try {
                //connect to the Image URL
                HttpURLConnection connection = (HttpURLConnection) new URL(imageURL).openConnection();
                connection.setRequestProperty("User-Agent", Constants.MOZILLA_USER_AGENT);

                //getAllToggles responseCode in case of IOException;
                responseCode = connection.getResponseCode();

                //turn the image connection into an inputStream
                stream = connection.getInputStream();

                //image's file name
                String filename = FilenameUtils.getName(new URL(imageURL).getPath());

                //send file
                sentMessage = channel.sendFile(Utility.removeMentions(message), false, stream, filename);

            } catch (MissingPermissionsException e) {
                //send message and url with url closed
                missingPermissions("URL_FILE", channel);
                sentMessage = sendMessage(message + " <" + imageURL + ">", channel).get();
            } catch (RateLimitException e) {
                //send exception back out, needed for request handling.
                throw e;
            } catch (MalformedURLException e) {
                //this should never show up. seriously
                Utility.sendStack(e);
            } catch (SSLHandshakeException e) {
                //something to do with the ssl handshake failed. unsure what causes this.
                sendMessage(message + " " + imageURL + " `FAILED TO EMBED - FAILED SSL HANDSHAKE.`", channel).get();
            } catch (IOException e) {
                //the file failed to be grabbed.
                String response = " `ERROR:" + responseCode + ", IMAGE FAILED TO EMBED";
                if (responseCode == 403) {
                    sentMessage = sendMessage(message + "\n" + imageURL + response + ", IMAGE LINK NEEDS UPDATING.`", channel).get();
                } else if (responseCode != -1) {
                    sentMessage = sendMessage(message + "\n" + imageURL + response + "`", channel).get();
                } else {
                    sentMessage = sendMessage(message + "\n" + imageURL, channel).get();
                }
            } catch (IllegalArgumentException e) {
                //the host failed, unsure as to the cause. inspect.
                if (e.getMessage().contains("http host = null")) {
                    sendMessage(message + "\n" + imageURL + " `HTTP HOST ERROR, CHECK URL FOR ERRORS.`", channel);
                }
            }
            try {
                //close off the stream
                if (stream != null) stream.close();
            } catch (IOException e) {
                //how the hell did this even happen
                Utility.sendStack(e);
            }
            //return the completed message
            return sentMessage;
        });
        if (loading != null) {
            sent.get();
            deleteMessage(loading);
        }
        return sent;
    }


    public static RequestBuffer.RequestFuture<IMessage> sendFileURL(String message, String imageURL, ChannelObject channel, boolean loadMessage) {
        return sendFileURL(message, imageURL, channel.get(), loadMessage);
    }

    public static RequestBuffer.RequestFuture<IMessage> sendFileURL(String message, String imageURL, CommandObject command, boolean loadMessage) {
        return sendFileURL(message, imageURL, command.channel.get(), loadMessage);
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild guild, long newRoleID, boolean isAdding) {
        return RequestBuffer.request(() -> {
            try {
                if (guild.getRoleByID(newRoleID) == null) return true;
                if (isAdding) {
                    author.addRole(guild.getRoleByID(newRoleID));
                } else {
                    author.removeRole(guild.getRoleByID(newRoleID));
                }
            } catch (RateLimitException e) {
                throw e;
            } catch (MissingPermissionsException e) {
                if (e.getMessage().contains("Edited roles hierarchy is too high.")) {
                    logger.debug("Error Editing roles of user with id: " + author.getLongID() + " on guild with id: " + guild.getLongID() +
                            ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Edited roles hierarchy is too high.");
                    return true;
                } else {
                    Utility.sendStack(e);
                    return true;
                }
            } catch (DiscordException e) {
                Utility.sendStack(e);
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild guild, List<IRole> userRoles) {
        return roleManagement(author, guild, userRoles, false);
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(IUser author, IGuild guild, List<IRole> userRoles, boolean suppressWarnings) {
        return RequestBuffer.request(() -> {
            try {
                List<IRole> temp = userRoles.stream().filter(r -> r != null).collect(Collectors.toList());
                IRole[] roles = temp.toArray(new IRole[temp.size()]);
                List<IRole> tempUserRoles = author.getRolesForGuild(guild);
                if (tempUserRoles.containsAll(temp) && temp.containsAll(tempUserRoles)) return false;
                guild.editUserRoles(author, roles);
                return true;
            } catch (RateLimitException e) {
                throw e;
            } catch (MissingPermissionsException e) {
                if (e.getMessage().contains("hierarchy")) {
                    if (!suppressWarnings) {
                        logger.warn("Error Editing roles of user with id: " + author.getLongID() + " on guild with id: " + guild.getLongID() +
                                ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Edited roles hierarchy is too high.");
                    }
                    return false;
                } else {
                    Utility.sendStack(e);
                    return false;
                }
            } catch (DiscordException e) {
                Utility.sendStack(e);
                return false;
            }
        });
    }

    public static void updateAvatar(Image avatar) {
        RequestBuffer.request(() -> {
            try {
                Globals.getClient().changeAvatar(avatar);
            } catch (RateLimitException e) {
                throw e;
            } catch (DiscordException e) {
                Utility.sendStack(e);
            }
        });
    }

    public static void updateUsername(String botName) {
        RequestBuffer.request(() -> {
            try {
                if (Client.getClient().getOurUser().getName().equals(botName)) return;
                Globals.getClient().changeUsername(botName);
            } catch (RateLimitException e) {
                throw e;
            } catch (DiscordException e) {
                Utility.sendStack(e);
            }
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> deleteMessage(IMessage message) {
        return RequestBuffer.request(() -> {
            try {
                if (Globals.isReady) {
                    message.delete();
                } else return false;
            } catch (RateLimitException e) {
                throw e;
            } catch (MissingPermissionsException e) {
                Utility.sendStack(e);
                return true;
            } catch (DiscordException e) {
                Utility.sendStack(e);
                return true;
            }
            return false;
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> deleteMessage(MessageObject message) {
        return deleteMessage(message.get());
    }

    public static RequestBuffer.RequestFuture<Boolean> updateUserNickName(IUser author, IGuild guild, String nickname) {
        return RequestBuffer.request(() -> {
            try {
                guild.setUserNickname(author, nickname);
            } catch (MissingPermissionsException e) {
                if (e.getMessage().toLowerCase().contains("hierarchy")) {
                    logger.debug("Could not Update Nickname. User's position in hierarchy is higher than mine.");
                } else {
                    Utility.sendStack(e);
                }
                return true;
            } catch (RateLimitException e) {
                throw e;
            } catch (DiscordException e) {
                Utility.sendStack(e);
                return true;
            }
            return false;
        });
    }


    public static RequestBuffer.RequestFuture<Boolean> muteUser(long guildID, long userID, boolean isMuting) {
        GuildObject content = Globals.getGuildContent(guildID);
        IUser user = Globals.getClient().getUserByID(userID);
        IGuild guild = Globals.getClient().getGuildByID(guildID);
        IRole mutedRole = Globals.client.getRoleByID(content.config.getMutedRoleID());

        if (user == null) return RequestBuffer.request(() -> true);
        List<IRole> newRoles = user.getRolesForGuild(guild);
        //roles for logging
        List<IRole> oldRoles = new ArrayList<>(newRoles);

        if (mutedRole == null) return RequestBuffer.request(() -> true);

        if (isMuting) {
            if (content.config.muteRemovesRoles) {
                content.users.getMutedUser(userID).setRoles(newRoles);
                newRoles.clear();
            }
            newRoles.add(mutedRole);
        } else {
            newRoles.remove(mutedRole);
            newRoles.addAll(content.users.getMutedUser(userID).getRoles(guild));
        }
        RequestBuffer.RequestFuture<Boolean> passed = roleManagement(user, guild, newRoles);
        Globals.getClient().getDispatcher().dispatch(new UserRoleUpdateEvent(guild, user, oldRoles, newRoles));
        return passed;
    }

    public static RequestBuffer.RequestFuture<Boolean> muteUser(CommandObject command, boolean b) {
        return muteUser(command.guild.longID, command.user.longID, b);
    }

    public static void changePresence(String s) {
        Client.getClient().changePresence(StatusType.ONLINE, ActivityType.PLAYING, s);
    }

    public static RequestBuffer.RequestFuture<IMessage> sendEmbededImage(String s, String imageUrl, IChannel channel) {
        XEmbedBuilder builder = new XEmbedBuilder(Constants.pixelColour);
        builder.withImage(imageUrl);
        if (s.length() > 2000) {
            sendError("Could not send message, Too Large.", s, channel);
            return null;
        }
        if (!Utility.isImageLink(imageUrl)) {
            sendError("Could not send Embed, link is not a valid image URL", imageUrl, channel);
            return null;
        }
        return RequestBuffer.request(() -> {
            if (s == null || s.isEmpty()) {
                return channel.sendMessage(builder.build());
            }
            return channel.sendMessage(s, builder.build());
        });
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(UserObject user, GuildObject content, long mutedRoleID, boolean isAdding) {
        return roleManagement(user.get(), content.get(), mutedRoleID, isAdding);
    }

    public static void addReaction(IMessage message, ReactionEmoji emoji) {
        RequestBuffer.request(() -> {
            try {
                message.addReaction(emoji);
            } catch (MissingPermissionsException e) {
                logger.debug("Could not add reaction to message. Reason: Missing Permission ADD_REACTIONS\n" +
                        "MessageID: " + message.getLongID() + ", ReactionID: " +
                        (emoji.isUnicode() ? emoji.getName() : emoji.getLongID()) + ", GuildID: " +
                        (message.getGuild() == null ? -1 : message.getGuild().getLongID()));
            }
        });
    }

    public static void addReaction(MessageObject message, ReactionEmoji emoji) {
        addReaction(message.get(), emoji);
    }

    public static void roleManagement(CommandObject command, IRole role, boolean isAdding) {
        roleManagement(command.user.get(), command.guild.get(), role.getLongID(), isAdding);
    }

    public static void sendWebHook(String webHookUrl, WebHookObject object) {
        try {
            URL url = new URL(webHookUrl);
            URLConnection con = url.openConnection();
            con.setRequestProperty("User-Agent", Constants.MOZILLA_USER_AGENT);
            HttpURLConnection http = (HttpURLConnection) con;
            http.setRequestMethod("POST"); // PUT is another valid option
            http.setDoOutput(true);
            Gson gson = new Gson();
            String json = gson.toJson(object);
            byte[] contents = json.getBytes(StandardCharsets.UTF_8);
            http.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            http.setFixedLengthStreamingMode(contents.length);
            try (OutputStream os = http.getOutputStream()) {
                os.write(contents);
                os.close();
            }
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(UserObject user, GuildObject guild, List<IRole> roles) {
        return roleManagement(user.get(), guild.get(), roles);
    }

    public static RequestBuffer.RequestFuture<IMessage> sendCreatorDm(String s) {
        IChannel creatorDm = RequestBuffer.request(() -> Globals.getCreator().getOrCreatePMChannel()).get();
        return sendMessage(s, creatorDm);
    }

    public static RequestBuffer.RequestFuture<Boolean> roleManagement(UserObject u, GuildObject content, IRole topTenRole, boolean b) {
        long roleID = topTenRole.getLongID();
        return roleManagement(u, content, roleID, b);
    }


    // TODO: 25/02/2018 remove below

//    public static RequestBuffer.RequestFuture<Boolean> deleteMessage(MessageHistory messages) {
//        return RequestBuffer.request(() -> {
//            try {
//                messages.bulkDelete();
//            } catch (MissingPermissionsException e) {
//                Utility.sendStack(e);
//                return true;
//            } catch (DiscordException e) {
//                if (e.getMessage().contains("CloudFlare")) {
//                    deleteMessage(messages);
//                } else {
//                    Utility.sendStack(e);
//                    return true;
//                }
//            }
//            return false;
//        });
//    }

    //    public static RequestBuffer.RequestFuture<IMessage> sendMessage(String message, IChannel channel) {
//        if (!Globals.client.isReady()) {
//            return null;
//        }
//        return RequestBuffer.request(() -> {
//            IMessage error = null;
//            if (message == null) {
//                return error;
//            }
//            if (message.length() < 2000) {
//                try {
//                    if (channel == null) {
//                        return error;
//                    }
//                    if (StringUtils.containsOnly(message, "\n")) {
//                        return error;
//                    }
//                    if (StringUtils.isBlank(message)) {
//                        return error;
//                    }
//                    if (message != null && !message.isEmpty()) {
//                        return channel.sendMessage(Utility.removeMentions(message));
//                    }
//                } catch (MissingPermissionsException e) {
//                    String debug = "Error sending message to channel with id: " + channel.getLongID();
//                    if (channel.getGuild() != null) {
//                        debug += " on guild with id: " + channel.getGuild().getLongID() + ".";
//                    }
//                    debug += "\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.";
//                    logger.debug(debug);
//                    return error;
//                } catch (DiscordException e) {
//                    if (e.getMessage().contains("CloudFlare")) {
//                        return sendMessage(message, channel).getAllToggles();
//                    } else if (e.getMessage().contains("Message was unable to be sent (Discord didn't return a response).")) {
//                        logger.debug("Could not Send DM, Perhaps the user has Dms from server members turned off.\nMessage: " + message);
//                    } else {
//                        Utility.sendStack(e);
//                        logger.error(message);
//                        return error;
//                    }
//                }
//            } else {
//                logger.debug("Message to be sent to channel with id: " + channel.getLongID() + "on guild with id: " + channel.getGuild().getLongID() +
//                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Message to large.");
//                return error;
//            }
//            return error;
//        });
//    }

//    public static RequestBuffer.RequestFuture<IMessage> sendEmbedMessage(String message, XEmbedBuilder builder, IChannel channel) {
//
//        //removal of @everyone and @here Mentions.
//        EmbedObject embed = builder.build();
//        return RequestBuffer.request(() -> {
//            try {
//                String iMessage = message;
//                if (iMessage == null) {
//                    iMessage = "";
//                }
//                return channel.sendMessage(iMessage, builder.build(), false);
//            } catch (DiscordException e) {
//                if (e.getMessage().contains("CloudFlare")) {
//                    sendEmbedMessage(message, builder, channel);
//                } else {
//                    Utility.sendStack(e);
//                    return null;
//                }
//            } catch (MissingPermissionsException e) {
//                logger.debug("Error sending File to channel with id: " + channel.getLongID() + " on guild with id: " + channel.getGuild().getLongID() +
//                        ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: Missing permissions.");
//                return sendMessage(Utility.embedToString(embed), channel).getAllToggles();
//            }
//            return null;
//        });
//    }

//    public static IMessage sendDMEmbed(String message, XEmbedBuilder embed, long userID) {
//        IChannel channel = Globals.getClient().getOrCreatePMChannel(Globals.getClient().getUserByID(userID));
//        if (channel != null) {
//            return sendEmbedMessage(message, embed, channel).getAllToggles();
//        } else {
//            return null;
//        }
//    }
//
//    public static RequestBuffer.RequestFuture<IMessage> sendDM(String message, long userID) {
//        return RequestBuffer.request(() -> {
//            try {
//                IChannel channel = Globals.getClient().getOrCreatePMChannel(Globals.getClient().getUserByID(userID));
//                if (message == null || message.isEmpty()) {
//                    return null;
//                }
//                return sendMessage(message, channel).getAllToggles();
//            } catch (DiscordException e) {
//                if (e.getMessage().contains("CloudFlare")) {
//                    return sendDM(message, userID).getAllToggles();
//                } else {
//                    Utility.sendStack(e);
//                    return null;
//                }
//            } catch (NullPointerException e) {
//                logger.debug("[sendDM] " + e.getMessage());
//                return null;
//            }
//        });
//    }
//
//    public static IMessage sendDM(String message, String userID) {
//        try {
//            return sendDM(message, Long.parseLong(userID)).getAllToggles();
//        } catch (NumberFormatException e) {
//            return null;
//        }
//    }
//
//    public static RequestBuffer.RequestFuture<Boolean> sendFileDM(String message, File attatchment, long userID) {
//        return RequestBuffer.request(() -> {
//            try {
//                IChannel channel = Globals.getClient().getOrCreatePMChannel(Globals.getClient().getUserByID(userID));
//                if (message == null || message.isEmpty()) {
//                    return true;
//                }
//                sendFile(message, attatchment, channel);
//            } catch (DiscordException e) {
//                if (e.getMessage().contains("CloudFlare")) {
//                    sendFileDM(message, attatchment, userID);
//                } else {
//                    Utility.sendStack(e);
//                    return true;
//                }
//            } catch (NullPointerException e) {
//                logger.debug("[sendDM] " + e.getMessage());
//                return true;
//            }
//            return false;
//        });
//    }

//    public static RequestBuffer.RequestFuture<Boolean> sendFile(String message, File file, IChannel channel) {
//        return RequestBuffer.request(() -> {
//            try {
//                if (StringUtils.containsOnly(message, "\n") || (message == null) || message.equals("")) {
//                    if (file != null) {
//                        channel.sendFile(file);
//                    } else {
//                        logger.debug("Error sending File to channel with id: " + channel.getLongID() + " on guild with id: " + channel.getGuild().getLongID() +
//                                ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: No file to send");
//                        return true;
//                    }
//                } else {
//                    if (message != null) {
//                        channel.sendFile(Utility.removeMentions(message), file);
//                    } else {
//                        sendMessage(message, channel);
//                        return true;
//                    }
//                }
//            } catch (DiscordException e) {
//                if (e.getMessage().contains("CloudFlare")) {
//                    sendFile(message, file, channel);
//                } else {
//                    Utility.sendStack(e);
//                    return true;
//                }
//            } catch (IOException e) {
//                Utility.sendStack(e);
//            } catch (MissingPermissionsException e) {
//                sendMessage("> Could not send File, missing permissions.", channel);
//                missingPermissions("FILE", channel);
//                return true;
//            }
//            return false;
//        });
//    }

    //    public static IMessage sendFileURL(String message, String imageURL, IChannel channel, boolean loadMessage) {
//        IMessage toDelete = null;
//        if (loadMessage) {
//            toDelete = sendMessage("`Loading...`", channel).getAllToggles();
//        }
//        IMessage sentMessage = null;
//        HttpURLConnection connection = null;
//        try {
//            connection = (HttpURLConnection) new URL(imageURL).openConnection();
//            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_7_5) " + "AppleWebKit/537.31 (KHTML, like Gecko) Chrome/26.0.1410.65 Safari/537.31");
//            //setup for the stream
//
//            InputStream stream = connection.getInputStream();
//            sentMessage = RequestBuffer.request(() -> {
//                try {
//                    //set up the file name
//                    URL url = new URL(imageURL);
//                    String filename = FilenameUtils.getNames(url.getPath());
//                    if (filename.equalsIgnoreCase("giphy.gif")) {
//                        return sendMessage(message + " " + imageURL, channel).getAllToggles();
//                    }
//                    //checks if url is valid
//                    if (!Utility.isImageLink(filename)) {
//                        return sendMessage(message + " " + imageURL, channel).getAllToggles();
//                    }
//                    //sends message/files
//                    if (StringUtils.containsOnly(message, "\n") || (message == null) || message.equals("") && imageURL != null) {
//                        return channel.sendFile("", stream, filename);
//                    } else if (message != null && !message.isEmpty() && imageURL != null) {
//                        return channel.sendFile(Utility.removeMentions(message), false, stream, filename);
//                    } else {
//                        logger.debug("Error sending File to channel with id: " + channel.getLongID() + " on guild with id: " + channel.getGuild().getLongID() +
//                                ".\n" + Constants.PREFIX_EDT_LOGGER_INDENT + "Reason: No file to send");
//                        return null;
//                    }
//                } catch (MalformedURLException e) {
//                    return sendMessage(message + " " + imageURL, channel).getAllToggles();
//                } catch (MissingPermissionsException e) {
//                    missingPermissions("URL_FILE", channel);
//                    return sendMessage(message + " <" + imageURL + ">", channel).getAllToggles();
//                }
//            }).getAllToggles();
//            stream.close();
//        } catch (MalformedURLException e) {
//            Utility.sendStack(e);
//        } catch (SSLHandshakeException e) {
//            sendMessage(message + " " + imageURL + " `FAILED TO EMBED - Failed SSL Handshake`", channel).getAllToggles();
//        } catch (IOException e) {
//            try {
//                if (connection != null) {
//                    int responseCode = connection.getResponseCode();
//                    sendMessage(message + " " + imageURL + " `FAILED TO EMBED - ERROR:" + responseCode + "`", channel).getAllToggles();
//                } else {
//                    Utility.sendStack(e);
//                }
//            } catch (IOException e1) {
//                e1.printStackTrace();
//            }
//        } catch (IllegalArgumentException e) {
//            if (e.getMessage().contains("http host = null")) {
//                sendMessage("> `HTTP HOST ERROR, CHECK URL FOR ERRORS.`", channel);
//            }
//        }
//        if (loadMessage && toDelete != null) {
//            deleteMessage(toDelete);
//        }
//        return sentMessage;
//    }

}
