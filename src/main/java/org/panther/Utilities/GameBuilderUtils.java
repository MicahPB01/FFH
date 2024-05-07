package org.panther.Utilities;

import com.google.gson.JsonObject;
import org.panther.Models.GameInfo;

import java.io.IOException;
import java.util.logging.Logger;

public class GameBuilderUtils {
    private static final Logger LOGGER = AppLogger.getLogger();


    public static GameInfo findGameInfo(JsonObject game) throws IOException {
        LOGGER.fine("Creating the game");

        GameInfo currentGame = new GameInfo();

        currentGame.setDate(game.get("gameDate").getAsString());
        System.out.println(game.get("gameDate").getAsString());
        currentGame.setTime(DateTimeUtils.convertUTCtoET(game.get("startTimeUTC").getAsString()));
        currentGame.setHomeTeam(game.getAsJsonObject("homeTeam").getAsJsonObject("name").get("default").getAsString());
        currentGame.setAwayTeam(game.getAsJsonObject("awayTeam").getAsJsonObject("name").get("default").getAsString());

        currentGame.setHomeTeamLogo(game.getAsJsonObject("homeTeam").get("logo").getAsString());
        currentGame.setAwayTeamLogo(game.getAsJsonObject("awayTeam").get("logo").getAsString());

        if(game.has("clock"))   {
            currentGame.setHomeScore(game.getAsJsonObject("homeTeam").get("score").getAsString());
            currentGame.setAwayScore(game.getAsJsonObject("awayTeam").get("score").getAsString());
        }
        LOGGER.fine("Game created, returning");
        return currentGame;
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