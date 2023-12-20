package org.DiscordBot;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.*;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.DiscordBot.Commands.Add.UserProfileSetupState;
import org.DiscordBot.Commands.CommandHandler;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;


import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class MessageListener extends ListenerAdapter {
    private final CommandHandler commandHandler;
    StringBuilder stringBuilder = new StringBuilder();



    private final Map<Long, UserProfileSetupState> userStates = new HashMap<>();
    private final Map<Long, Long> userServerMap = new HashMap<>();

    public MessageListener()   {
        this.commandHandler = new CommandHandler();
        this.commandHandler.setMessageListener(this);
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()) return; // Ignoring bot messages

        if (event.isFromType(ChannelType.TEXT)) {
            // Handle commands in public channels
            String msg = event.getMessage().getContentRaw();
            if (msg.startsWith("!")) {
                commandHandler.handle(event);
                System.out.println("Received Command");
            }
        } else if (event.isFromType(ChannelType.PRIVATE)) {
            // Handle DMs for profile setup or other interactions
            handlePrivateMessage(event);
        }
    }


    public void setUserServer(long userId, long serverId) {
        userServerMap.put(userId, serverId);
    }

    private void handlePrivateMessage(MessageReceivedEvent event) {
        String userID = event.getAuthor().getId();
        String serverID = String.valueOf(userServerMap.get(Long.parseLong(userID)));
        String message = event.getMessage().getContentDisplay();


        if (userStates.containsKey(Long.parseLong(userID))) {
            UserProfileSetupState currentState = userStates.get(Long.parseLong(userID));

            switch (currentState) {

                case NICKNAME:
                    // Save the nickname and prompt for the next piece of information
                    saveNickname(userID, serverID, message);
                    userStates.put(Long.valueOf(userID), UserProfileSetupState.ALMA_MATER);
                    event.getChannel().sendMessage("Care to share where you go/went to school?").queue();
                    break;

                case ALMA_MATER:
                    saveAlmaMater(userID, serverID, message);
                    userStates.put(Long.valueOf(userID), UserProfileSetupState.CONTACT_INFO);
                    event.getChannel().sendMessage("Awesome, add some ways to contact you? One way at a time please. Type \"Done\" when finished").queue();
                    break;

                case CONTACT_INFO:
                    if(message.equalsIgnoreCase("done"))   {
                        userStates.put(Long.valueOf(userID), UserProfileSetupState.BIRTHDAY);
                        saveContact(userID, serverID, stringBuilder.toString());
                        stringBuilder = new StringBuilder();
                        event.getChannel().sendMessage("Thanks! Now, please enter your birthday so we all know when to celebrate with you! MM-DD-YYYY").queue();
                        break;
                    }
                    else {
                        stringBuilder.append("\n").append(message);
                        System.out.println(stringBuilder.toString());
                        event.getChannel().sendMessage("Contact added! Type another or 'Done' if finished.").queue();
                        break;
                    }


                case BIRTHDAY:
                    saveBirthday(userID, serverID, message);
                    userStates.put(Long.valueOf(userID), UserProfileSetupState.SOCIAL_MEDIA);
                    event.getChannel().sendMessage("Got it, now, please consider entering some social media handles! Ex. Instagram: Handle. One way at a time please. Type \"Done\" when finished").queue();
                    break;

                case SOCIAL_MEDIA:
                   if(message.equalsIgnoreCase("done"))   {
                       userStates.put(Long.valueOf(userID), UserProfileSetupState.GENERAL_INFO);
                       saveSocialMedia(userID, serverID, stringBuilder.toString());
                       stringBuilder = new StringBuilder();
                       event.getChannel().sendMessage("Fantastic, now, please type a few sentences about yourself and what you like to do.").queue();
                   }
                   else {
                       stringBuilder.append("\n").append(message);
                       System.out.println(stringBuilder.toString());
                       event.getChannel().sendMessage("Social media handle added! Type another or 'Done' if finished.").queue();
                   }
                    break;

                case GENERAL_INFO, EDIT_GENERAL_INFO:
                    saveGeneral(userID, serverID, message);
                    event.getChannel().sendMessage("One last thing. If you would like to upload a picture of yourself please do so now!").queue();
                    userStates.put(Long.valueOf(userID), UserProfileSetupState.PHOTO);
                    break;


                case PHOTO:
                    if (!event.getMessage().getAttachments().isEmpty()) {
                        Message.Attachment attachment = event.getMessage().getAttachments().get(0);
                        if (attachment.isImage()) {
                            // Process the image attachment
                            saveUserPhoto(userID, serverID,  attachment);
                        } else {
                            event.getChannel().sendMessage("Please attach an image file.").queue();
                        }
                    } else {
                        event.getChannel().sendMessage("No image detected. That's okay!").queue();
                    }
                    event.getChannel().sendMessage("Profile setup completed. Thanks!").queue();
                    userStates.remove(Long.parseLong(userID));
                    break;

                case COMPLETED:
                    event.getChannel().sendMessage("Your profile setup is complete!").queue();
                    userStates.remove(Long.parseLong(userID));
                    break;

                case EDIT_BIRTHDAY:
                    saveBirthday(userID, serverID, message);
                    userStates.put(Long.valueOf(userID), UserProfileSetupState.COMPLETED);
                    event.getChannel().sendMessage("Change Successful!").queue();
                    userStates.remove(Long.parseLong(userID));
                    break;
                case EDIT_NICKNAME:
                    saveNickname(userID, serverID, message);
                    userStates.put(Long.valueOf(userID), UserProfileSetupState.COMPLETED);
                    event.getChannel().sendMessage("Change Successful!").queue();
                    userStates.remove(Long.parseLong(userID));
                    break;
                case EDIT_SOCIAL_MEDIA:
                    saveSocialMedia(userID, serverID, message);
                    userStates.put(Long.valueOf(userID), UserProfileSetupState.COMPLETED);
                    event.getChannel().sendMessage("Change Successful!").queue();
                    userStates.remove(Long.parseLong(userID));
                    break;

                default:
                    event.getChannel().sendMessage("Hmmm, I'm having trouble understanding you. Please message Micah if you are having issues").queue();
            }








        }
    }

    public void setUserState(long userId, UserProfileSetupState state) {
        userStates.put(userId, state);
    }


    private void saveNickname(String userID, String serverID, String nickname)   {
        Connection connection = Database.getConnection();
        String sql = "INSERT INTO user_profiles (server_id, user_id, nickname) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE nickname = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverID);
            preparedStatement.setString(2, userID);
            preparedStatement.setString(3, nickname);
            preparedStatement.setString(4, nickname); // For the update case

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private void saveAlmaMater(String userID, String serverID, String almaMater)   {
        Connection connection = Database.getConnection();
        String sql = "INSERT INTO user_profiles (server_id, user_id, alma_mater) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE alma_mater = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(sql))   {
            preparedStatement.setString(1, serverID);
            preparedStatement.setString(2, userID);
            preparedStatement.setString(3, almaMater);
            preparedStatement.setString(4, almaMater);

            preparedStatement.executeUpdate();
        }
        catch (SQLException e)   {
            e.printStackTrace();
        }


    }

    private void saveContact(String userID, String serverID, String contact)   {
        Connection connection = Database.getConnection();
        String sql = "INSERT INTO user_profiles (server_id, user_id, contact) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE contact = ?";


        try(PreparedStatement preparedStatement = connection.prepareStatement(sql))   {
            preparedStatement.setString(1, serverID);
            preparedStatement.setString(2, userID);
            preparedStatement.setString(3, contact);
            preparedStatement.setString(4, contact);

            preparedStatement.executeUpdate();
        }
        catch (SQLException e)   {
            e.printStackTrace();
        }
    }

    private void saveUserPhoto(String userID, String serverID, Message.Attachment attachment) {
        File tempFile = new File("tempfile_" + attachment.getFileName()); // Temporary file

        attachment.downloadToFile(tempFile).thenAccept(file -> {
            try {
                byte[] imageData = Files.readAllBytes(file.toPath());

                // Save imageData to the database
                try (Connection connection = Database.getConnection()) {
                    String sql = "INSERT INTO user_profiles (server_id, user_id, photo) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE photo = ?";
                    try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                        pstmt.setString(1, serverID);
                        pstmt.setString(2, userID);
                        pstmt.setBytes(3, imageData);
                        pstmt.setBytes(4, imageData);
                        pstmt.executeUpdate();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                    // Handle exceptions
                }
            } catch (IOException e) {
                e.printStackTrace();
                // Handle exceptions
            } finally {
                file.delete(); // Delete the temporary file
            }
        }).exceptionally(e -> {
            e.printStackTrace();
            return null; // Handle exceptions
        });
    }




    private void saveBirthday(String userID, String serverID, String birthday)   {
        Connection connection = Database.getConnection();
        String sql = "INSERT INTO user_profiles (server_id, user_id, birthday) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE birthday = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, serverID);
            pstmt.setString(2, userID);
            pstmt.setString(3, birthday);
            pstmt.setString(4, birthday); // For the update case

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private void saveSocialMedia(String userID, String serverID, String socialMedia)   {
        Connection connection = Database.getConnection();
        String sql = "INSERT INTO user_profiles (server_id, user_id, social_media) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE social_media = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, serverID);
            pstmt.setString(2, userID);
            pstmt.setString(3, socialMedia);
            pstmt.setString(4, socialMedia); // For the update case

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

    private void saveGeneral(String userID, String serverID, String generalInfo)   {
        Connection connection = Database.getConnection();
        String sql = "INSERT INTO user_profiles (server_id, user_id, general_info) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE general_info = ?";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, serverID);
            pstmt.setString(2, userID);
            pstmt.setString(3, generalInfo);
            pstmt.setString(4, generalInfo); // For the update case

            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }


    @Override
    public void onMessageReactionAdd(MessageReactionAddEvent event) {
        if (event.getUser().isBot())return; // Ignore non-checkmark or bot reactions



        long userId = event.getUserIdLong();
        long messageId = event.getMessageIdLong();


        // Determine which field is being edited based on messageId
        UserProfileSetupState newState = getEditingFieldByMessageId(userId, messageId);
        if (newState != null) {
            setUserState(userId, newState);
            event.getChannel().sendMessage("You selected to " + newState.name() + ". Please enter the new value:").queue();
        }
    }




    private Map<Long, Map<Long, UserProfileSetupState>> userEditingMessages = new HashMap<>();

    public void storeEditingMessage(long userId, long messageId, UserProfileSetupState editingField) {
        userEditingMessages.computeIfAbsent(userId, k -> new HashMap<>()).put(messageId, editingField);
    }

    public UserProfileSetupState getEditingFieldByMessageId(long userId, long messageId) {
        Map<Long, UserProfileSetupState> userMessages = userEditingMessages.get(userId);
        if (userMessages != null) {
            return userMessages.get(messageId);
        }
        return null; // Or handle this case appropriately
    }







}

