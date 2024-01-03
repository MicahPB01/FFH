package org.panther.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.panther.Commands.Score.Score;

import java.awt.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                new Score().execute(event);
                break;
            // Add cases for other commands
            case "stats":
                try {
                    handleStats(event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;


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








    private String[] findScore(SlashCommandInteractionEvent event, String date)   {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String[] gameInfo = new String[6];

        LocalDateTime now = LocalDateTime.now();
        String currentDate = (dateTimeFormatter.format(now));

        if(date != null)   {
            currentDate = date.replaceAll("/", "");
        }


        String url = "https://moneypuck.com/moneypuck/dates/" + currentDate + ".htm";

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
                gameInfo[4] = teamLogos.get(0).attr("src");
                gameInfo[5] = teamLogos.get(1).attr("src");

                if (team1.contains("FLORIDA PANTHERS") || team2.contains("FLORIDA PANTHERS")) {
                    String opponent = team1.contains("FLORIDA PANTHERS") ? team2 : team1;

                    String score;



                    Elements oddsAndScores = row.select("h2");
                    Element secondH2 = oddsAndScores.get(1);
                    score = secondH2.text();

                    if(score.contains("%"))   {
                        score = row.select("h3").text(); //grabbing date instead
                        score = score.replace(" Preview", "");
                    }

                    gameInfo[2] = score;


                    System.out.println(score);

                    date = row.select("h4").text(); // Assuming date is in an h4 tag

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

    public void handleStats(SlashCommandInteractionEvent event) throws IOException {
        String playerName = Objects.requireNonNull(event.getOption("player")).getAsString();
        CSVRecord foundRecord = null;


        URL url = new URL("https://moneypuck.com/moneypuck/playerData/seasonSummary/2023/regular/teams/skaters/FLA.csv");
        URLConnection connection = url.openConnection();

        CSVFormat csvFormat = CSVFormat.DEFAULT
                .builder()
                .setSkipHeaderRecord(true)
                .setHeader()
                .setIgnoreHeaderCase(true)
                .setTrim(true)
                .build();

        try(BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)))   {
            CSVParser parser = new CSVParser(reader, csvFormat);

            for(CSVRecord record : parser)   {
                String playerColumnValue = record.get("name");
                String allColumnValue = record.get("situation");

                if(playerColumnValue.equalsIgnoreCase(playerName) && "all".equalsIgnoreCase(allColumnValue))   {
                    foundRecord = record;
                }
            }

        }
        catch(IOException e )   {
            e.printStackTrace();
        }

        if(foundRecord != null)   {
            System.out.println("Found stats for " + playerName);


            String columnA = foundRecord.get("playerId"); // Replace "A" with the actual header name for column A
            int columnG = Integer.parseInt(foundRecord.get("games_played")); // Replace "G" with the actual header name for column G
            int columnI = (int) Double.parseDouble(foundRecord.get("shifts")); // Replace "I" with the actual header name for column I
            int columnAB = (int) Double.parseDouble(foundRecord.get("I_F_primaryAssists")); // Replace "AB" with the actual header name for column AB
            int columnAC = (int) Double.parseDouble(foundRecord.get("I_F_secondaryAssists")); // Replace "AC" with the actual header name for column AC
            int columnAI = (int) Double.parseDouble(foundRecord.get("I_F_goals")); // Replace "AI" with the actual header name for column AI


            EmbedBuilder embedBuilder = new EmbedBuilder();

            embedBuilder.setTitle(playerName);
            embedBuilder.setColor(Color.RED);
            embedBuilder.setThumbnail("https://assets.nhle.com/mugs/nhl/20232024/FLA/" + columnA + ".png"); // Set the image URL
            embedBuilder.addField("Goals: " , String.valueOf(columnAI), true);
            embedBuilder.addField("Assists: ", String.valueOf((columnAC + columnAB)), true);

            embedBuilder.addField("Games Played: ", String.valueOf(columnG), true);
            embedBuilder.addField("Number of shifts: " , String.valueOf(columnI), true);
            embedBuilder.setFooter("Player Information", null);
            embedBuilder.setTimestamp(LocalDateTime.now());
            event.replyEmbeds(embedBuilder.build()).queue();

        }





    }









    // Add other command handling methods here
}
