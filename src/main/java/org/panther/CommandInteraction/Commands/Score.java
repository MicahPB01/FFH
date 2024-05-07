package org.panther.CommandInteraction.Commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.panther.CommandInteraction.Command;
import org.panther.Models.GameInfo;
import org.panther.Utilities.AppLogger;
import org.panther.Utilities.DateTimeUtils;
import org.panther.Utilities.GameBuilderUtils;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;
import java.util.logging.Logger;

public class Score implements Command {
    private static final Logger LOGGER = AppLogger.getLogger();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.info("Attempting to find a game");

        String baseUrl = "https://api-web.nhle.com/v1/score/";
        String date = DateTimeUtils.findDate(event);

        if (date.equals("invalid")) {
            LOGGER.warning("Invalid date format received: " + date);
            event.reply("Invalid Date. Please ensure its formatted as yyyy-MM-dd").queue();
            return;
        }

        String fullUrl = baseUrl + date;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fullUrl).build();
        GameInfo newGame = new GameInfo();

        try {
            LOGGER.fine("Sending request to URL: " + fullUrl);
            Response response = client.newCall(request).execute();

            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray games = jsonObject.getAsJsonArray("games");

                boolean gameFound = false;
                for (int i = 0; i < games.size(); i++) {
                    JsonObject game = games.get(i).getAsJsonObject();
                    JsonObject homeTeam = game.getAsJsonObject("homeTeam");
                    JsonObject awayTeam = game.getAsJsonObject("awayTeam");

                    if (homeTeam.get("abbrev").getAsString().equals("FLA") || awayTeam.get("abbrev").getAsString().equals("FLA")) {
                        LOGGER.fine("Florida Panthers game found, processing game info");
                        newGame = GameBuilderUtils.findGameInfo(game);
                        sendResult(newGame, event);
                        gameFound = true;
                        break;
                    }
                }

                if (!gameFound) {
                    LOGGER.fine("No Florida Panthers game found on the specified date: " + date);
                    event.reply("No game Today!").queue();
                }
            } else {
                LOGGER.severe("Failed to retrieve game information. HTTP response not successful.");
            }
        } catch (IOException e) {
            LOGGER.severe("IOException occurred while fetching game data: " + e.getMessage());
        }
    }

    private void sendResult(GameInfo currentGame, SlashCommandInteractionEvent event) {
        LOGGER.info("Preparing to send game details");

        if (currentGame != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Game Details");
            embedBuilder.addField("Home: ", currentGame.getHomeTeam(), true);
            embedBuilder.addField("Away: ", currentGame.getAwayTeam(), true);

            if (currentGame.getHomeScore() != null) {
                embedBuilder.addField("Score: ", currentGame.getAwayTeam() + " - " + currentGame.getAwayScore() + " : " + currentGame.getHomeScore() + " - " + currentGame.getHomeTeam(), false);
            } else {
                embedBuilder.addField("Time: ", currentGame.getTime(), false);
            }

            embedBuilder.addField("Date: ", DateTimeUtils.reverseDate(currentGame.getDate()), true);
            embedBuilder.setColor(Color.RED);
            embedBuilder.setFooter("Game Information", null);
            embedBuilder.setTimestamp(Instant.now());
            event.replyEmbeds(embedBuilder.build()).queue();
        } else {
            LOGGER.warning("No game information available to send");
        }
    }
}
