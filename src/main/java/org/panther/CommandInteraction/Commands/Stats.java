package org.panther.CommandInteraction.Commands;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.panther.CommandInteraction.Command;
import org.panther.Database;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

public class Stats implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) {
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

            assert response.body() != null;
            String jsonData = response.body().string();
            JsonObject jsonObject = JsonParser.parseString(jsonData).getAsJsonObject();

            // Extract stats for regular season and playoffs
            JsonObject featuredStats = jsonObject.getAsJsonObject("featuredStats");
            JsonObject regularSeasonStats = featuredStats.getAsJsonObject("regularSeason").getAsJsonObject("subSeason");


            JsonObject playoffStats = new JsonObject();

            if(featuredStats.has("playoff"))   {
                playoffStats = featuredStats.getAsJsonObject("playoffs").getAsJsonObject("subSeason");
            }
            else   {
                playoffStats.addProperty("goals", 0);
                playoffStats.addProperty("assists", 0);
                playoffStats.addProperty("gamesPlayed", 0);
                LOGGER.fine("No playoff stats found, setting stats for playoffs at 0");
            }

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
            LOGGER.severe(e.getMessage());
            event.reply("Failed to fetch player stats.").setEphemeral(true).queue();
        }


    }

    private static int findPlayerIDByName(String fullName) {
        String[] names = fullName.split(" ", 2);  // Assumes name is in "First Last" format
        String sql = "SELECT id FROM players WHERE first_name = ? AND last_name = ? LIMIT 1";
        try (Connection conn = Database.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, names[0]); // first_name
                pstmt.setString(2, names.length > 1 ? names[1] : ""); // last_name
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    return rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }
        return -1; // Indicates not found
    }
}
