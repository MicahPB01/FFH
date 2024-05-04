package org.panther.Commands;

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
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.panther.Commands.Score.Score;
import org.panther.Commands.Votes.Votes;
import org.panther.Database;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collector;
import java.util.stream.Collectors;

public class CommandHandler extends ListenerAdapter {



    public CommandHandler() {


    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        System.out.println("Command: " + event.getName());

        switch (commandName) {
            case "ping" -> handlePing(event);
            case "profile" -> handleHelp(event);
            case "score" -> new Score().execute(event);
            //case "stars" -> handVoteStars(event);
            case "vote" -> Votes.handleVoteStars(event);

            // Add cases for other commands
            case "stats" -> {
                try {
                    handleStats(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
            default -> event.reply("Unknown command").setEphemeral(true).queue();
        }
    }

    private void handlePing(SlashCommandInteractionEvent event) {
        // Implementation for the ping command
        event.reply("Pong!").queue();
    }

    private void handleHelp(SlashCommandInteractionEvent event) {
        event.reply("Help hasn't been constructed yet!").queue();
    }



    public void handleStats(SlashCommandInteractionEvent event) throws IOException {
        OkHttpClient client = new OkHttpClient();
        String playerName = Objects.requireNonNull(event.getOption("player")).getAsString();
        int playerID = findPlayerIDByName(playerName);

        if (playerID == -1) {
            event.reply("Player not found.").setEphemeral(true).queue();
            return;
        }

        String url = "https://api-web.nhle.com/v1/player/" + playerID + "/landing";
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String jsonData = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract stats for regular season and playoffs
            JsonObject featuredStats = jsonObject.getAsJsonObject("featuredStats");
            JsonObject regularSeasonStats = featuredStats.getAsJsonObject("regularSeason").getAsJsonObject("subSeason");
            JsonObject playoffStats = featuredStats.getAsJsonObject("playoffs").getAsJsonObject("subSeason");

            // Calculate combined stats
            int combinedGoals = regularSeasonStats.get("goals").getAsInt() + playoffStats.get("goals").getAsInt();
            int combinedAssists = regularSeasonStats.get("assists").getAsInt() + playoffStats.get("assists").getAsInt();
            int combinedGamesPlayed = regularSeasonStats.get("gamesPlayed").getAsInt() + playoffStats.get("gamesPlayed").getAsInt();

            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle(playerName + " - Season Overview");
            embedBuilder.setColor(Color.decode("#C8102E"));  // RED
            embedBuilder.setThumbnail(jsonObject.get("headshot").getAsString());
            embedBuilder.addField("Team", jsonObject.getAsJsonObject("fullTeamName").get("default").getAsString(), false);  // Not inline to ensure it appears on its own line

// Stats line
            embedBuilder.addField("Goals", String.valueOf(combinedGoals), true);
            embedBuilder.addField("Assists", String.valueOf(combinedAssists), true);
            embedBuilder.addField("Shoots/Catches", jsonObject.get("shootsCatches").getAsString(), true);

// Physical attributes line
            embedBuilder.addField("Height", jsonObject.get("heightInInches").getAsInt() + " in", true);
            embedBuilder.addField("Weight", jsonObject.get("weightInPounds").getAsInt() + " lbs", true);
            embedBuilder.addField("Position", jsonObject.get("position").getAsString(), true);

            embedBuilder.addField("Games Played", String.valueOf(combinedGamesPlayed), false);  // Not inline to ensure it appears on its own line
            embedBuilder.setFooter("Data includes Regular Season and Playoffs", null);

            event.replyEmbeds(embedBuilder.build()).queue();
        } catch (IOException e) {
            e.printStackTrace();
            event.reply("Failed to fetch player stats.").setEphemeral(true).queue();
        }


    }


    private static int findPlayerIDByName(String fullName) {
        String[] names = fullName.split(" ", 2);  // Assumes name is in "First Last" format
        String sql = "SELECT id FROM players WHERE first_name = ? AND last_name = ? LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, names[0]); // first_name
            pstmt.setString(2, names.length > 1 ? names[1] : ""); // last_name
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Indicates not found
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        //System.out.println("looking for autocomplete options");
        //System.out.println("Command string is: " + event.getCommandString());
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
            e.printStackTrace();
        }

        return names;
    }












    // Add other command handling methods here
}
