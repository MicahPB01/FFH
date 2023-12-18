package org.DiscordBot.Commands.Add;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.utils.FileUpload;
import org.DiscordBot.Command;
import org.DiscordBot.Database;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class ObtainProfile implements Command {



    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String userID;
        String serverID = event.getGuild().getId();

        List<User> mentionedUsers = event.getMessage().getMentions().getUsers();

        if (args.length > 1 && !args[1].isEmpty() && !mentionedUsers.isEmpty()) {
            // Use the ID of the first mentioned user
            userID = mentionedUsers.get(0).getId();
        } else {
            // Otherwise, use the ID of the user who issued the command
            userID = event.getAuthor().getId();
        }


        displayUserProfile(event, serverID, userID);
    }

    @Override
    public String getDescription() {
        return "Used to display a profile. You can use it with just \"!profile\" to see your profile or tag someone with it \"!profile @example\" to pull up someone else's profile.";
    }


    private void displayUserProfile(MessageReceivedEvent event, String serverID, String userID) {
        String sql = "SELECT * FROM `user_profiles` WHERE server_id = ? AND user_id = ?";

        try (Connection connection = Database.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

            preparedStatement.setString(1, serverID);
            preparedStatement.setString(2, userID);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle(event.getJDA().getUserById(userID).getName() + "'s Profile");
                embed.setColor(Color.CYAN); // You can choose any color

                // Nickname (if available)
                String nickname = resultSet.getString("nickname");
                if (nickname != null) {
                    embed.addField("Nickname", nickname, false);
                }

                String almaMater = resultSet.getString("alma_mater");
                if (almaMater != null) {
                    embed.addField("Alma Mater", almaMater, false);
                }

                String contact = resultSet.getString("contact");
                if (contact != null) {
                    embed.addField("Contact Info", contact, false);
                }

                // Other fields
                String birthday = resultSet.getString("birthday");
                if (birthday != null) {
                    embed.addField("Birthday", birthday, false);
                }

                String socialMedia = resultSet.getString("social_media");
                if (socialMedia != null) {
                    embed.addField("Social Media", socialMedia, false);
                }

                String generalInfo = resultSet.getString("general_info");
                if (generalInfo != null) {
                    embed.addField("General Info", generalInfo, false);
                }

                byte[] photoData = resultSet.getBytes("photo");
                File tempFile;
                if (photoData != null) {
                    tempFile = File.createTempFile("profile_photo", ".jpg");
                    Files.write(tempFile.toPath(), photoData);
                    embed.setImage("attachment://" + tempFile.getName());
                } else {
                    tempFile = null;
                }

                if (tempFile != null) {
                    event.getChannel().sendMessageEmbeds(embed.build()).addFiles(FileUpload.fromData(tempFile)).queue(
                            message -> tempFile.delete() // Delete the temporary file after sending
                    );
                } else {
                    event.getChannel().sendMessageEmbeds(embed.build()).queue();
                }
            } else {
                event.getChannel().sendMessage("No profile information found.").queue();
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            // Handle exceptions
        }
    }

}
