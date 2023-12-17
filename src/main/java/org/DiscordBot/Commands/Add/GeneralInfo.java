package org.DiscordBot.Commands.Add;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;
import org.DiscordBot.Commands.CommandHandler;
import org.DiscordBot.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Arrays;

public class GeneralInfo implements Command {


    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        System.out.println("ADDING INFO");

        if(args.length > 2)   {
            event.getChannel().sendMessage("Usage: !addinfo [info]").queue();
        }

        String info = String.join(" ", Arrays.copyOfRange(args, 1 , args.length));
        String userID = event.getAuthor().getId();
        String serverID = event.getGuild().getId();

        updateGeneralInfo(serverID, userID, info);

    }


    private void updateGeneralInfo(String serverID, String userID, String info)   {



        Connection connection = Database.getConnection();

        String sql = "INSERT INTO user_profiles (server_id, user_id, general_info) VALUES (?, ?, ?) ON DUPLICATE KEY UPDATE general_info = ?";


        try(PreparedStatement preparedStatement = connection.prepareStatement(sql))   {
            preparedStatement.setString(1, serverID);
            preparedStatement.setString(2, userID);
            preparedStatement.setString(3, info);
            preparedStatement.setString(4, info); //used if updating info

            preparedStatement.executeUpdate();
        }
        catch (SQLException e)   {
            e.printStackTrace();
        }


    }





}
