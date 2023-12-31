package org.panther;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.panther.Commands.CommandHandler;

import java.time.Duration;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class BotMain {




    public static void main(String[] args) {
        try {
            // Replace "your-bot-token" with your actual bot token
            JDA jda = JDABuilder.createDefault("MTE5MDE4MjczODQyNzQwMDMzNA.G9ytuB.Ow6W6fUr70EdheEgXq4v5Zo7__HdCYtqB7ee8g")
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.customStatus("Vamos Gatos!"))
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new CommandHandler())
                    .build();

            jda.awaitReady(); // Wait for the bot to log in
            registerSlashCommands(jda); // Register slash commands




            System.out.println("Bot is ready!");



        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void registerSlashCommands(JDA jda) {

        OptionData players = new OptionData(OptionType.STRING, "player", "Choose a player", true)
                .addChoice("Aaron Ekblad", "Aaron Ekblad")
                .addChoice("Steven Lorentz", "Steven Lorentz")
                .addChoice("Jonah Gadjovich", "Jonah Gadjovich")
                .addChoice("Oliver Ekman-Larsson", "Oliver Ekman-Larsson")
                .addChoice("Sam Bennett", "Sam Bennett")
                .addChoice("Eetu Luostarinen", "Eetu Luostarinen")
                .addChoice("Sam Reinhart", "Sam Reinhart")
                .addChoice("William Lockwood", "William Lockwood")
                .addChoice("Niko Mikkola", "Niko Mikkola")
                .addChoice("Evan Rodrigues", "Evan Rodrigues")
                .addChoice("Ryan Lomberg", "Ryan Lomberg")
                .addChoice("Carter Verhaeghe", "Carter Verhaeghe")
                .addChoice("Gustav Forsling", "Gustav Forsling")
                .addChoice("Brandon Montour", "Brandon Montour")
                .addChoice("Josh Mahura", "Josh Mahura")
                .addChoice("Mackie Samoskevich", "Mackie Samoskevich")
                .addChoice("Dmitry Kulikov", "Dmitry Kulikov")
                .addChoice("Justin Sourdif", "Justin Sourdif")
                .addChoice("Nick Cousins", "Nick Cousins")
                .addChoice("Kevin Stenlund", "Kevin Stenlund")
                .addChoice("Uvis Balinskis", "Uvis Balinskis")
                .addChoice("Anton Lundell", "Anton Lundell")
                .addChoice("Aleksander Barkov", "Aleksander Barkov")
                .addChoice("Matthew Tkachuk", "Matthew Tkachuk");



        // Register slash commands here
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Test the bot's response time!"),
                Commands.slash("score", "Get the score of the most recent Panthers Game!")
                        .addOptions(
                                new OptionData(OptionType.STRING, "date", "Enter the date for the score (format: yyyy/MM/dd)", false)
                                // false at the end signifies that this argument is optional
                        ),
                Commands.slash("stats", "Get the most relevant stats for a specified player.")
                        .addOptions(players)



        ).queue();
    }



}

