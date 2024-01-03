package org.panther.Automation;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jsoup.nodes.Document;
import org.panther.Commands.Score.GameInfo;
import org.panther.Utilities.DateTimeUtils;
import org.panther.Utilities.GameBuilderUtils;

import java.awt.*;
import java.time.*;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class GameChecker {
    private final JDA jda;
    private static final ZoneId EST_ZONE_ID = ZoneId.of("America/New_York");
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public GameChecker(JDA jda) {
        this.jda = jda;
    }

    public void startGameCheckingScheduler() {
        Runnable gameCheckTask = new GameCheckTask();
        long initialDelay = calculateInitialDelay();
        gameCheckTask.run();

        scheduledExecutorService.scheduleAtFixedRate(gameCheckTask, initialDelay, TimeUnit.DAYS.toMillis(1), TimeUnit.MILLISECONDS);
    }

    private long calculateInitialDelay() {
        ZonedDateTime now = ZonedDateTime.now(EST_ZONE_ID);
        ZonedDateTime nextRun = now.withHour(14).withMinute(18).withSecond(0);

        if (now.compareTo(nextRun) > 0) {
            nextRun = nextRun.plusDays(1);
        }

        return Duration.between(now, nextRun).toMillis();

    }

    private class GameCheckTask implements Runnable {
        @Override
        public void run() {
            checkForGame();

        }
    }

    private void checkForGame() {

        //String currentDate = DateTimeUtils.dateFromCurrentDay();
        String currentDate = "20240104";
        String url = GameBuilderUtils.findUrl(currentDate);
        Document webScrape = GameBuilderUtils.getWebsiteForScrape(url);

        if(GameBuilderUtils.checkForGame(webScrape))   {

            GameInfo currentGame = GameBuilderUtils.findGameInfo(webScrape, currentDate);

            if(currentGame != null)   {
                try   {
                    createGameDayThread(currentGame);
                }
                catch (DateTimeParseException | InterruptedException e)   {
                    e.printStackTrace();
                }
            }


        }

    }


    private void createGameDayThread(GameInfo currentGame) throws InterruptedException {

        TextChannel channel = jda.getTextChannelById("1190095971107995708");
        ForumChannel forumChannel = jda.getForumChannelById("1191530176958447656");

        createThreadChannel(channel, currentGame);
        String threadName = buildThreadName(currentGame);

        createForumPost(forumChannel, threadName);

        ThreadChannel activeThread = null;
        if (forumChannel != null) {
            activeThread = findActiveThread(forumChannel, threadName);
        }


        if (activeThread != null) {
           sendForumMessage(currentGame, activeThread);
        }
        else   {
            System.out.println("Could not find channel");
        }


    }

    private void createThreadChannel(TextChannel channel, GameInfo currentGame) {
        if (channel != null) {
            channel.createThreadChannel(currentGame.getAwayTeam() + " VS " + currentGame.getHomeTeam()).queue();
        } else {
            System.out.println("Channel not found.");
        }
    }

    private String buildThreadName(GameInfo currentGame)   {

        String opponent = determineOpponent(currentGame);

        return opponent + " | " + currentGame.getTime() + " | " + DateTimeUtils.reverseDate(currentGame.getDate());


    }

    private String determineOpponent(GameInfo currentGame)   {

        if (currentGame.getHomeTeam().contains("FLORIDA")) {
            return currentGame.getAwayTeam();
        }
        else   {
            return currentGame.getHomeTeam();
        }

    }

    private void createForumPost(ForumChannel forumChannel, String threadName)   {

        if(forumChannel != null)   {
            forumChannel.createForumPost(threadName, MessageCreateData.fromContent("In this thread, you will find the pregame poll, the link to the livestream, a 3 stars poll, the post game show, espn highlights, and the morning after talk show")).queue();
        }
    }

    private ThreadChannel findActiveThread(ForumChannel forumChannel, String threadName) throws InterruptedException   {
        Thread.sleep(3000);  //pause for discord creating the post

        List<ThreadChannel> threads = forumChannel.getThreadChannels();

        for(ThreadChannel thread : threads)   {
            if(thread.getName().equals(threadName))   {
                return thread;
            }
        }
        return null;
    }

    private void sendForumMessage(GameInfo currentGame, ThreadChannel activeThread)   {

        EmbedBuilder embedBuilder = new EmbedBuilder();

        embedBuilder.setColor(Color.RED);
        embedBuilder.setTitle(currentGame.getAwayTeam() + " VS " + currentGame.getHomeTeam() + " Pregame Poll");
        embedBuilder.setThumbnail(currentGame.getHomeTeamLogo());
        embedBuilder.addField("Date: ", DateTimeUtils.reverseDate(currentGame.getDate()), true);
        embedBuilder.addField("Game Winner", "Predict the winner! (Cat Emoji for Panthers, X for the other team!", false);
        embedBuilder.setFooter("Game Information", currentGame.getAwayTeamLogo());
        embedBuilder.setTimestamp(Instant.now());


        activeThread.sendMessageEmbeds(embedBuilder.build()).queue(message -> {
            message.addReaction(Emoji.fromUnicode("U+1F63C")).queue(); // cat
            message.addReaction(Emoji.fromUnicode("U+1F645")).queue(); // X

        }, throwable -> {
            System.err.println("Error sending message: " + throwable.getMessage());
        });


    }


}
