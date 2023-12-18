package org.DiscordBot;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.requests.GatewayIntent;
import javax.security.auth.login.LoginException;

public class BotMain {
    public static void main(String[] args) {
        try {
            // Replace "your-bot-token" with your actual bot token
            JDA jda = JDABuilder.createDefault("MTE4NTY2NTM0MDM3MDMyOTc4MA.GSBrJJ.mtDF4fQk7spKTyuwFnzZFINHrJOqKofZJmWNqs")
                    .enableIntents(GatewayIntent.GUILD_MESSAGES, GatewayIntent.MESSAGE_CONTENT)
                    .setActivity(Activity.customStatus("As always, Go Huskies!"))
                    .addEventListeners(new MessageListener())
                    .build();

            jda.awaitReady(); // Wait for the bot to log in
            System.out.println("Bot is ready!");

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


}
