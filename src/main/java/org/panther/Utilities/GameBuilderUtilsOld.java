package org.panther.Utilities;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.panther.Models.GameInfo;

import java.io.IOException;

public class GameBuilderUtilsOld {

    public static GameInfo findGameInfo(Document webScrap, String date)   {

        GameInfo currentGame = new GameInfo();

        Elements rows = webScrap.select("table tr");


        for (Element row : rows) {

            Elements teams = row.select("img");
            String team1 = teams.get(0).attr("alt");
            String team2 = teams.get(1).attr("alt");

            if (!panthersRow(team1, team2)) {
                continue;
            }

            // Set teams and logos. Home team is listed second which is the home team is team 2
            currentGame.setHomeTeam(team2);
            currentGame.setAwayTeam(team1);
            currentGame.setHomeTeamLogo(teams.get(1).attr("src"));
            currentGame.setAwayTeamLogo(teams.get(0).attr("src"));


            //the score will be found in the second h2. win percentage chance is found in h2 as well at 0 and 2
            String scoreOrDate = row.select("h2").get(1).text();
            String score = null;
            String time = row.select("h3").get(1).text();


            if (scoreOrDate != null && scoreOrDate.contains("-")) {
                score = scoreOrDate;
            } else  {

                time = DateTimeUtils.findTime(time);
            }

            currentGame.setTime(time);
            currentGame.setDate(date);
            currentGame.setScore(score);



            return currentGame;

        }
        return null;
    }

    //create the URL to be used for data scraping
    public static String findUrl(String date)   {
        return "https://moneypuck.com/moneypuck/dates/" + date + ".htm";
    }

    public static Document getWebsiteForScrape(String url)   {

        try {
            return Jsoup.connect(url).get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static boolean checkForGame(Document webScrape) {

        return webScrape.toString().contains("FLORIDA PANTHERS");

    }

    private static boolean panthersRow(String team1, String team2)   {

        return team1.equals("FLORIDA PANTHERS") || team2.equals("FLORIDA PANTHERS");

    }

    public static String determineOpponent(GameInfo currentGame)   {

        if (currentGame.getHomeTeam().contains("Florida Panthers")) {
            return currentGame.getAwayTeam();
        }
        else   {
            return currentGame.getHomeTeam();
        }

    }



}