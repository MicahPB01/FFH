package org.panther.Utilities;

import com.google.gson.JsonObject;
import org.jsoup.nodes.Document;
import org.panther.Commands.Score.GameInfo;

import java.io.IOException;

public class GameBuilderUtils {

    public static GameInfo findGameInfo(JsonObject game) throws IOException {

        GameInfo currentGame = new GameInfo();

        currentGame.setDate(game.get("gameDate").getAsString());
        System.out.println(game.get("gameDate").getAsString());
        currentGame.setTime(DateTimeUtils.convertUTCtoET(game.get("startTimeUTC").getAsString()));
        currentGame.setHomeTeam(game.getAsJsonObject("homeTeam").getAsJsonObject("name").get("default").getAsString());
        currentGame.setAwayTeam(game.getAsJsonObject("awayTeam").getAsJsonObject("name").get("default").getAsString());

        currentGame.setHomeTeamLogo(game.getAsJsonObject("homeTeam").get("logo").getAsString());
        currentGame.setAwayTeamLogo(game.getAsJsonObject("awayTeam").get("logo").getAsString());


        return currentGame;



    }



    //create the URL to be used for data scraping
    public static String findUrl(String date)   {
        return "https://moneypuck.com/moneypuck/dates/" + date + ".htm";
    }



    public static boolean checkForGame(Document webScrape) {

        return webScrape.toString().contains("FLORIDA PANTHERS");

    }









}