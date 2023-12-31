package org.panther;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.panther.Commands.CommandHandler;

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
            System.out.println("Bot is ready!");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void registerSlashCommands(JDA jda) {
        // Register slash commands here
        jda.updateCommands().addCommands(
                Commands.slash("ping", "Test the bot's response time!"),
                Commands.slash("score", "Get the score of the most recent Panthers Game!")
                // Add other slash commands here
        ).queue();
    }
}
