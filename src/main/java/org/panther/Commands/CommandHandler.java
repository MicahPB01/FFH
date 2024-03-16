package org.panther.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.panther.Commands.Score.Score;
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
import java.util.Map;
import java.util.Objects;

public class CommandHandler extends ListenerAdapter {



    public CommandHandler() {


    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        System.out.println("Command");

        switch (commandName) {
            case "ping" -> handlePing(event);
            case "profile" -> handleHelp(event);
            case "score" -> new Score().execute(event);
            //case "stars" -> handVoteStars(event);
            case "vote" ->handVoteStars(event);

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

    private void handVoteStars(SlashCommandInteractionEvent event)   {

        String firstStar = Objects.requireNonNull(event.getOption("firststar")).getAsString();
        String secondStar = Objects.requireNonNull(event.getOption("secondstar")).getAsString();
        String thirdStar = Objects.requireNonNull(event.getOption("thirdstar")).getAsString();

        int gameID = findMostRecentGameID();

        processVote(gameID, firstStar, secondStar, thirdStar);

        String description = "Test description";



        event.reply("Your votes have been tallied!" + description).queue();


    }

    private void processVote(int gameID, String firstStar, String secondStar, String thirdStar)   {
        Map<String, Integer> stars = Map.of(
                firstStar, 3,
                secondStar, 2,
                thirdStar, 1
        );

        stars.forEach((starName, points) -> {
            int playerID = findPlayerIDByName(starName);
            if(playerID != -1)   {
                updateVoteSummary(gameID, playerID, points);
            }
            else   {
                System.out.println("Player not found");
            }
        });


    }

    private int findMostRecentGameID() {
        String sql = "SELECT id FROM games ORDER BY id DESC LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Indicates not found
    }

    private void updateVoteSummary(int gameId, int playerId, int points) {
        String sql = "INSERT INTO vote_summary (game_id, player_id, vote_count) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE vote_count = vote_count + VALUES(vote_count)";
        try (Connection conn = Database.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, gameId);
                pstmt.setInt(2, playerId);
                pstmt.setInt(3, points);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int findPlayerIDByName(String name) {
        String sql = "SELECT id FROM players WHERE name = ?";
        try (Connection conn = Database.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, name);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt("id");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1; // Indicates not found
    }









    // Add other command handling methods here
}
