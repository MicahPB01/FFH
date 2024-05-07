package org.panther.Commands.Score;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.panther.Commands.Command;
import org.panther.Models.GameInfo;
import org.panther.Utilities.DateTimeUtils;
import org.panther.Utilities.GameBuilderUtils;

import java.awt.*;
import java.io.IOException;
import java.time.Instant;

public class Score implements Command {


    @Override
    public void execute(SlashCommandInteractionEvent event) {
        System.out.println("looking for a game");
        String baseUrl = "https://api-web.nhle.com/v1/score/";
        String date = DateTimeUtils.findDate(event);

        if(date.equals("invalid"))   {
            event.reply("Invalid Date. Please ensure its formatted as yyyy-MM-dd").queue();
            return;
        }

        String fullUrl = baseUrl + date;
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(fullUrl).build();
        GameInfo newGame = new GameInfo();

        try   {
            System.out.println("found games, looking for panthers");
            Response response = client.newCall(request).execute();

            if(response.isSuccessful() && response.body() != null)   {
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray games = jsonObject.getAsJsonArray("games");

                for(int i = 0; i < games.size(); i++)   {
                    JsonObject game = games.get(i).getAsJsonObject();
                    JsonObject homeTeam = game.getAsJsonObject("homeTeam");
                    JsonObject awayTeam = game.getAsJsonObject("awayTeam");

                    if(homeTeam.get("abbrev").getAsString().equals("FLA") || awayTeam.get("abbrev").getAsString().equals("FLA"))   {
                        System.out.println("Found Florida, creating game info");
                        newGame = GameBuilderUtils.findGameInfo(game);
                        sendResult(newGame, event);
                        return;
                    }

                }
                event.reply("No game Today!").queue();

            }
        }
        catch (IOException ignored)   {

        }


    }
    private void sendResult(GameInfo currentGame, SlashCommandInteractionEvent event)   {
        System.out.println("Sending game");

        if (currentGame != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Game Details");
            embedBuilder.addField("Home: ", currentGame.getHomeTeam(), true);
            embedBuilder.addField("Away: " , currentGame.getAwayTeam(), true);


            if(currentGame.getHomeScore() != null) {
                embedBuilder.addField("Score: ", currentGame.getAwayTeam() + " - " + currentGame.getAwayScore() + " : " + currentGame.getHomeScore() + " - " + currentGame.getHomeTeam(), false);
            }
            else   {
                embedBuilder.addField("Time: " , currentGame.getTime(), false);
            }


            embedBuilder.addField("Date: ", DateTimeUtils.reverseDate(currentGame.getDate()), true);
            embedBuilder.setColor(Color.red);
            embedBuilder.setFooter("Game Information", null);
            embedBuilder.setTimestamp(Instant.now());
            event.replyEmbeds(embedBuilder.build()).queue();
        }

    }
}
