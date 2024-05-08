package org.panther;

import com.google.gson.JsonArray;
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
import org.panther.Automation.GameChecker;
import org.panther.CommandInteraction.CommandHandler;
import org.panther.Utilities.AppLogger;
import org.panther.Utilities.DataFetcher;
import org.panther.Utilities.PlayerUpdater;

import java.io.IOException;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BotMain {
    private static final Logger LOGGER = AppLogger.getLogger();






    public static void main(String[] args) {
        try {
            JDA jda = JDABuilder.createDefault("")
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.customStatus("Vamos Gatos!"))
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new CommandHandler())
                    .build();

            jda.awaitReady();
            registerSlashCommands(jda);

            GameChecker gameChecker = new GameChecker(jda);

            gameChecker.startGameCheckingScheduler();

            try   {
                String jsonData = DataFetcher.fetchPlayerData();
                PlayerUpdater.updateDatabase(jsonData);
            }
            catch (IOException | InterruptedException e)   {
                LOGGER.severe(e.getMessage());
            }

            LOGGER.info("Bot Loaded!");



        } catch (InterruptedException e) {
            LOGGER.severe(e.getMessage());
        }
    }

    private static void registerSlashCommands(JDA jda) {
        LOGGER.fine("Registering Commands");

        OptionData players = new OptionData(OptionType.STRING, "player", "Choose a player", true)
                .setAutoComplete(true);


        OptionData playersOne = new OptionData(OptionType.STRING, "firststar", "Choose a player", true)
                .setAutoComplete(true);


        OptionData playersTwo = new OptionData(OptionType.STRING, "secondstar", "Choose a player", true)
                .setAutoComplete(true);


        OptionData playersThree = new OptionData(OptionType.STRING, "thirdstar", "Choose a player", true)
                .setAutoComplete(true);

        LOGGER.fine("Got players for autofill");
        LOGGER.fine("Adding slash commands");


        jda.updateCommands().addCommands(
                Commands.slash("ping", "Test the bot's response time!"),

                Commands.slash("score", "Get the score of Panthers Game!")
                        .addOptions(
                                new OptionData(OptionType.STRING, "date", "Enter the date for the score (format: yyyy-MM-dd)", false)
                        ),

                Commands.slash("stats", "Get the most relevant stats for a specified player.")
                        .addOptions(players),

                Commands.slash("vote", "Vote for the current game's three stars.")
                        .addOptions(playersOne, playersTwo, playersThree),

                Commands.slash("stars", "View the stars for a game.")
                        .addOptions(
                                new OptionData(OptionType.STRING, "date", "Enter the date for the score (format: yyyy/MM/dd)", false)
                        ),
                Commands.slash("overall", "See the overall star tally across all games")

        ).queue();
    }

}