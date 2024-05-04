package org.panther.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.Command;
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
        String playerName = Objects.requireNonNull(event.getOption("player")).getAsString();
        CSVRecord foundRecord = null;


        URL url = new URL("https://moneypuck.com/moneypuck/playerData/seasonSummary/2023/regular/teams/skaters/FLA.csv");
        URLConnection connection = url.openConnection();

        CSVFormat csvFormat = CSVFormat.DEFAULT
                .builder()
                .setSkipHeaderRecord(true)
                .setHeader()
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))   {
            CSVParser parser = new CSVParser(reader, csvFormat);

            for(CSVRecord record : parser)   {
                String playerColumnValue = record.get("name");
                String allColumnValue = record.get("situation");

                if(playerColumnValue.equalsIgnoreCase(playerName) && "all".equalsIgnoreCase(allColumnValue))   {
                    foundRecord = record;
                }
            }

        }
        catch(IOException e )   {
            e.printStackTrace();
        }

        if(foundRecord != null)   {
            System.out.println("Found stats for " + playerName);


            String columnA = foundRecord.get("playerId"); // Replace "A" with the actual header name for column A
            int columnG = Integer.parseInt(foundRecord.get("games_played")); // Replace "G" with the actual header name for column G
            int columnI = (int) Double.parseDouble(foundRecord.get("shifts")); // Replace "I" with the actual header name for column I
            int columnAB = (int) Double.parseDouble(foundRecord.get("I_F_primaryAssists")); // Replace "AB" with the actual header name for column AB
            int columnAC = (int) Double.parseDouble(foundRecord.get("I_F_secondaryAssists")); // Replace "AC" with the actual header name for column AC
            int columnAI = (int) Double.parseDouble(foundRecord.get("I_F_goals")); // Replace "AI" with the actual header name for column AI


            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setTitle(playerName);
            embedBuilder.setColor(Color.RED);
            embedBuilder.setThumbnail("https://assets.nhle.com/mugs/nhl/20232024/FLA/" + columnA + ".png"); // Set the image URL
            embedBuilder.addField("Goals: " , String.valueOf(columnAI), true);
            embedBuilder.addField("Assists: ", String.valueOf((columnAC + columnAB)), true);

            embedBuilder.addField("Games Played: ", String.valueOf(columnG), true);
            embedBuilder.addField("Number of shifts: " , String.valueOf(columnI), true);
            embedBuilder.setFooter("Player Information", null);
            embedBuilder.setTimestamp(LocalDateTime.now());
            event.replyEmbeds(embedBuilder.build()).queue();

        }





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
