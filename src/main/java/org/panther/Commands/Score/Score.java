package org.panther.Commands.Score;


import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.jsoup.nodes.Document;
import org.panther.Commands.Command;
import org.panther.Utilities.DateTimeUtils;
import org.panther.Utilities.GameBuilderUtilsOld;

import java.awt.*;
import java.time.Instant;

public class Score implements Command {
    @Override
    public void execute(SlashCommandInteractionEvent event) {

        String date = DateTimeUtils.removeDateDash(DateTimeUtils.findDate(event));

        String url = GameBuilderUtilsOld.findUrl(date);

        Document webScrape = GameBuilderUtilsOld.getWebsiteForScrape(url);

        boolean gameToday = GameBuilderUtilsOld.checkForGame(webScrape);

        if(!gameToday)   {
            event.reply("No game today").queue();
            return;
        }

        GameInfo currentGame = GameBuilderUtilsOld.findGameInfo(webScrape, date);

        sendResult(currentGame, event);

    }

    public void sendResult(GameInfo currentGame, SlashCommandInteractionEvent event)   {

        if (currentGame != null) {
            EmbedBuilder embedBuilder = new EmbedBuilder();
            embedBuilder.setTitle("Game Details");
            embedBuilder.addField("Home: ", currentGame.getHomeTeam(), true);
            embedBuilder.addField("Away: " , currentGame.getAwayTeam(), true);


            if(currentGame.getScore() != null) {
                embedBuilder.addField("Score: ", currentGame.getAwayTeam() + "      " + currentGame.getScore() + "      " + currentGame.getHomeTeam(), false);
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
