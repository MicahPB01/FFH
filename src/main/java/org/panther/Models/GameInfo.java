package org.panther.Models;

public class GameInfo {
    private String homeTeam;
    private String homeTeamLogo;
    private String awayTeam;
    private String awayTeamLogo;
    private String score;
    private String time;
    private String date;
    private String homeScore;
    private String awayScore;



    public String getHomeTeam()   {
        return homeTeam;
    }

    public void setHomeTeam(String homeTeam)   {
        this.homeTeam = homeTeam;
    }

    public String getHomeTeamLogo()   {
        return homeTeamLogo;
    }
    public void setHomeTeamLogo(String logo)   {
        this.homeTeamLogo = homeTeamLogo;
    }

    public String getAwayTeam() {
        return awayTeam;
    }
    public void setAwayTeam(String awayTeam) {
        this.awayTeam = awayTeam;
    }


    public String getAwayTeamLogo()  {
        return awayTeamLogo;
    }

    public void setAwayTeamLogo(String logo)  {
        this.awayTeamLogo = awayTeamLogo;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getTime()   {
        return time;
    }

    public void setTime(String time)   {
        this.time = time;
    }

    public String getDate()   {
        return date;
    }

    public void setDate(String date)   {
        this.date = date;
    }

    public String getHomeScore()   {
        return homeScore;
    }

    public void setHomeScore(String homeScore) {
        this.homeScore = homeScore;
    }

    public void setAwayScore(String awayScore) {
        this.awayScore = awayScore;
    }

    public String getAwayScore() {
        return awayScore;
    }
}
