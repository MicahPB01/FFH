package org.panther.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.awt.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class CommandHandler extends ListenerAdapter {



    public CommandHandler() {


    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String commandName = event.getName();
        System.out.println("Command");

        switch (commandName) {
            case "ping":
                handlePing(event);
                break;
            case "profile":
                handleHelp(event);
                break;
            case "score":
                handleScore(event);
                break;
            // Add cases for other commands
            default:
                event.reply("Unknown command").setEphemeral(true).queue();
                break;
        }
    }

    private void handlePing(SlashCommandInteractionEvent event) {
        // Implementation for the ping command
        event.reply("Pong!").queue();
    }

    private void handleHelp(SlashCommandInteractionEvent event) {
        event.reply("Help hasn't been constructed yet!").queue();
    }





    private void handleScore(SlashCommandInteractionEvent event)   {
        String[] gameInfo = findScore(event);

        if(gameInfo == null)   {
            event.reply("No Game Today").queue();
            return;
        }

        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Game Details");
        embedBuilder.addField("Home: ", gameInfo[0], true);
        embedBuilder.addField("Away: " , gameInfo[1], true);
        embedBuilder.addField("Score: ", gameInfo[2], false);
        embedBuilder.addField("Date: ", gameInfo[3], true);
        embedBuilder.setColor(Color.red);
        embedBuilder.setFooter("Game Information", null);
        embedBuilder.setTimestamp(Instant.now());
        event.replyEmbeds(embedBuilder.build()).queue();




        
    }


    private String[] findScore(SlashCommandInteractionEvent event)   {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String[] gameInfo = new String[4];

        LocalDateTime now = LocalDateTime.now();
        System.out.println(dateTimeFormatter.format(now));


        String url = "https://moneypuck.com/moneypuck/dates/" + dateTimeFormatter.format(now) + ".htm";

        System.out.println(url);



        try {
            Document doc = Jsoup.connect(url).get();
            Elements rows = doc.select("table tr");

            for (Element row : rows) {
                Elements teamLogos = row.select("img");
                if (teamLogos.size() < 2) continue; // Skip rows with less than 2 teams

                String team1 = teamLogos.get(0).attr("alt");
                String team2 = teamLogos.get(1).attr("alt");
                
                gameInfo[0] = team1;
                gameInfo[1] = team2;

                if (team1.contains("FLORIDA PANTHERS") || team2.contains("FLORIDA PANTHERS")) {
                    String opponent = team1.contains("FLORIDA PANTHERS") ? team2 : team1;

                    String score;

                    Elements oddsAndScores = row.select("h2");
                    Element secondH2 = oddsAndScores.get(1);
                    score = secondH2.text();
                    gameInfo[2] = score;


                    System.out.println(score);

                    String date = row.select("h4").text(); // Assuming date is in an h4 tag

                    gameInfo[3] = date;
                    

                    return gameInfo;
                }
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }






    // Add other command handling methods here
}
