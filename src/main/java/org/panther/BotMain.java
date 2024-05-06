package org.panther;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.panther.Automation.GameChecker;
import org.panther.Commands.CommandHandler;

import javax.swing.text.html.Option;
import java.io.IOException;
import java.util.LinkedList;

public class BotMain {





    public static void main(String[] args) {
        try {
            // Replace "your-bot-token" with your actual bot token
            JDA jda = JDABuilder.createDefault("")
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.customStatus("Vamos Gatos!"))
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new CommandHandler())
                    .build();

            jda.awaitReady(); // Wait for the bot to log in
            registerSlashCommands(jda); // Register slash commands

            GameChecker gameChecker = new GameChecker(jda);



            gameChecker.startGameCheckingScheduler();







            System.out.println("Bot is ready!");



        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void registerSlashCommands(JDA jda) {

        OptionData players = updatePlayerOptions("player");
        OptionData playersOne = updatePlayerOptions("firststar");
        OptionData playersTwo = updatePlayerOptions("secondstar");
        OptionData playersThree = updatePlayerOptions("thirdstar");




        // Register slash commands here
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Test the bot's response time!"),
                Commands.slash("score", "Get the score of the most recent Panthers Game!")
                        .addOptions(
                                new OptionData(OptionType.STRING, "date", "Enter the date for the score (format: yyyy/MM/dd)", false)
                                // false at the end signifies that this argument is optional
                        ),
                Commands.slash("stats", "Get the most relevant stats for a specified player.")
                        .addOptions(players),
                Commands.slash("vote", "Vote for the current game's three stars.")
                        .addOptions(playersOne, playersTwo, playersThree),
                Commands.slash("stars", "View the stars for a game.")
                        .addOptions(
                                new OptionData(OptionType.STRING, "date", "Enter the date for the score (format: yyyy/MM/dd)", false)

                        ),
                Commands.slash("overall", "View the top 10 players based on star votes.")


        ).queue();
    }




    private static OptionData updatePlayerOptions(String argument) {
        String url = "https://api-web.nhle.com/v1/roster/FLA/current";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        OptionData players = new OptionData(OptionType.STRING, argument, "Choose a player", true);

        try {
            Response response = client.newCall(request).execute(); // Synchronous call
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray allPlayers = new JsonArray();

                // Combine players from different categories
                if (jsonObject.has("forwards")) {
                    allPlayers.addAll(jsonObject.getAsJsonArray("forwards"));
                }
                if (jsonObject.has("defensemen")) {
                    allPlayers.addAll(jsonObject.getAsJsonArray("defensemen"));
                }
                if (jsonObject.has("goalies")) {
                    allPlayers.addAll(jsonObject.getAsJsonArray("goalies"));
                }

                // Add players to options
                for (int i = 0; i < 25; i++) {
                    JsonObject player = allPlayers.get(i).getAsJsonObject();
                    String firstname = player.getAsJsonObject("firstName").get("default").getAsString();
                    String lastName = player.getAsJsonObject("lastName").get("default").getAsString();
                    String fullName = firstname + " " + lastName;

                    players.addChoice(fullName, fullName);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return players;
    }










}

