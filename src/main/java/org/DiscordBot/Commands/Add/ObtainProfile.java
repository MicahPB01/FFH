package org.DiscordBot.Commands.Add;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;
import org.DiscordBot.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ObatinProfile implements Command {



    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        String userID = event.getAuthor().getId();
        String serverID = event.getGuild().getId();

        displayUserProfile(event, serverID, userID);
    }


    private void displayUserProfile(MessageReceivedEvent event, String serverID, String userID) {
        Connection connection = Database.getConnection();

        String sql = "SELECT * FROM `user_profiles` WHERE server_id = ? AND user_id = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(sql)) {
            preparedStatement.setString(1, serverID);
            preparedStatement.setString(2, userID);

            System.out.println(preparedStatement);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("Profile\n");

                String nickname = resultSet.getString("nickname");
                if (nickname != null) stringBuilder.append("Nickname: ").append(nickname).append("\n");

                String birthday = resultSet.getString("birthday");
                if (birthday != null) stringBuilder.append("Birthday: ").append(birthday).append("\n");

                String instagramTag = resultSet.getString("instagram_tag");
                if (instagramTag != null) stringBuilder.append("Instagram: ").append(instagramTag).append("\n");

                String generalInfo = resultSet.getString("general_info");
                if (generalInfo != null) stringBuilder.append("General Info: ").append(generalInfo).append("\n");


                event.getChannel().sendMessage(stringBuilder.toString()).queue();
            } else {
                event.getChannel().sendMessage("No User Information Found").queue();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle exceptions


        }
    }
}
