package org.DiscordBot;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

import javax.security.auth.login.LoginException;

public class BotMain {
    public static void main(String[] args) {
        try {
            // Replace "your-bot-token" with your actual bot token
            JDA jda = JDABuilder.createDefault("")
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.customStatus("As always, Go Huskies!"))
                    .enableCache(CacheFlag.VOICE_STATE)
                    .addEventListeners(new MessageListener())
                    .build();

            jda.awaitReady(); // Wait for the bot to log in
            System.out.println("Bot is ready!");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
