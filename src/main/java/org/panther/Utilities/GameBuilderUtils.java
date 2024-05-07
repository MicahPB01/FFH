package org.panther.Utilities;

import com.google.gson.JsonObject;
import org.panther.Models.GameInfo;

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

        if(game.has("clock"))   {
            currentGame.setHomeScore(game.getAsJsonObject("homeTeam").get("score").getAsString());
            currentGame.setAwayScore(game.getAsJsonObject("awayTeam").get("score").getAsString());
        }

        return currentGame;

    }










}