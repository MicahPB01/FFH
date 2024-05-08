package org.panther.CommandInteraction;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jetbrains.annotations.NotNull;
import org.panther.CommandInteraction.Commands.*;
import org.panther.Database;
import org.panther.Models.PlayerVoteCount;
import org.panther.Utilities.AppLogger;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CommandHandler extends ListenerAdapter {
    private static final Logger LOGGER = AppLogger.getLogger();

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        LOGGER.fine("Received command: " + commandName);

        switch (commandName) {
            case "ping" -> new Ping().execute(event);
            case "score" -> new Score().execute(event);
            case "vote" -> new Vote().execute(event);
            case "overall" -> new Overall().execute(event);
            case "stats" -> new Stats().execute(event);

            default -> event.reply("Unknown command").setEphemeral(true).queue();
        }
        LOGGER.info("Command successfully executed: " + commandName);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        LOGGER.fine("Setting up autocomplete options");
        if (event.getCommandString().startsWith("/stats player:") || event.getCommandString().startsWith("/vote")) { // Ensure the command path is correctly specified
            //System.out.println("Checking autocomplete options for stats:player");
            String input = event.getFocusedOption().getValue();

            List<Command.Choice> choices = getPlayerChoices(input);
            event.replyChoices(choices).queue(); // Remove unnecessary casting
        }
    }

    private List<Command.Choice> getPlayerChoices(String input) {
        List<String> playerNames = fetchPlayerNames(input);
        return playerNames.stream()
                .map(name -> new Command.Choice(name, name)) // Correctly map to new Choice objects
                .collect(Collectors.toList());
    }

    private List<String> fetchPlayerNames(String input) {
        LOGGER.fine("Fetching player names");
        List<String> names = new ArrayList<>();
        // Adjust the SQL query to concatenate first_name and last_name
        String sql = "SELECT CONCAT(first_name, ' ', last_name) AS full_name FROM players WHERE first_name LIKE ? OR last_name LIKE ? LIMIT 25";

        try (Connection conn = Database.getConnection()) {
            assert conn != null;
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Setting the same input for both first_name and last_name to be included in the search
                stmt.setString(1, input + "%");
                stmt.setString(2, input + "%");

                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    // Fetching the concatenated full name
                    names.add(rs.getString("full_name"));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        LOGGER.fine("Got player names");
        return names;
    }






}
