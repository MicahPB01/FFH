package org.panther.Automation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.panther.Models.GameInfo;
import org.panther.Database;
import org.panther.Models.PlayerVoteCount;
import org.panther.Utilities.AppLogger;
import org.panther.Utilities.DateTimeUtils;
import org.panther.Utilities.GameBuilderUtils;

import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

public class GameChecker {
    private final JDA jda;
    private static final ZoneId EST_ZONE_ID = ZoneId.of("America/New_York");
    private static final Logger LOGGER = AppLogger.getLogger();
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

        String currentDate = DateTimeUtils.dateFromCurrentDay();
        String url = "https://api-web.nhle.com/v1/scoreboard/FLA/now";
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        GameInfo currentGame = new GameInfo();
        String testDate = "2024-05-12";

        try   {
            Response response = client.newCall(request).execute();

            if(response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JsonObject jsonObject = JsonParser.parseString(responseBody).getAsJsonObject();
                JsonArray gamesByDate = jsonObject.getAsJsonArray("gamesByDate");

                for (JsonElement element : gamesByDate) {
                    JsonObject dateObject = element.getAsJsonObject();
                    String date = dateObject.get("date").getAsString();
                    if (currentDate.equals(date)) {
                        JsonArray games = dateObject.getAsJsonArray("games");
                        if (games.size() > 0) {
                            JsonObject game = games.get(0).getAsJsonObject();

                            currentGame = GameBuilderUtils.findGameInfo(game);

                            LOGGER.fine("Creating game threads and created game for database");
                            createGameDayThread(currentGame);
                            createDataEntry(currentGame);


                        }
                    }
                }
            }

        } catch (IOException | InterruptedException e) {
            LOGGER.severe(e.getMessage());
        }


    }


    private void createGameDayThread(GameInfo currentGame) throws InterruptedException {

        TextChannel channel = jda.getTextChannelById("1197806332661731428");
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
            LOGGER.warning("Could not find channel for gameday thread");
        }


    }

    private void createThreadChannel(TextChannel channel, GameInfo currentGame) {
        if (channel != null) {
            channel.createThreadChannel(currentGame.getAwayTeam() + " VS " + currentGame.getHomeTeam()).queue();
        } else {
            LOGGER.warning("Could not find channel for thread");
        }
    }

    private String buildThreadName(GameInfo currentGame)   {

        String opponent = GameBuilderUtils.determineOpponent(currentGame);

        return opponent + " | " + currentGame.getTime() + " | " + currentGame.getDate();


    }



    private void createForumPost(ForumChannel forumChannel, String threadName)   {

        if(forumChannel != null)   {
            forumChannel.createForumPost(threadName, MessageCreateData.fromContent("In this thread, you will find the pregame poll, the link to the livestream, a 3 stars poll, the post game show, espn highlights, and the morning after talk show")).queue();
        }
    }

    private void createDataEntry(GameInfo currentGame)   {
        String sql = "INSERT INTO games (game_date, description) VALUES (?, ?)";
        int dateInt = Integer.parseInt(DateTimeUtils.dateFromCurrentDay().replaceAll("-",""));
        int testInt = 20240512;

        try(Connection conn = Database.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql))   {
                pstmt.setInt(1, dateInt);
                pstmt.setString(2, currentGame.getAwayTeam() + " VS " + currentGame.getHomeTeam());

                pstmt.executeUpdate();
            LOGGER.info("New game inserted into database: " + currentGame.getHomeTeam() + " " + currentGame.getAwayTeam());
            }

        catch(SQLException e)   {
            LOGGER.severe(e.getMessage());
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
        embedBuilder.addField("Date: ", DateTimeUtils.reverseDate(DateTimeUtils.removeDateDash(currentGame.getDate())), true);
        embedBuilder.addField("Game Winner", "Predict the winner! (Cat Emoji for Panthers, X for the other team!", false);
        embedBuilder.setFooter("Game Information", currentGame.getAwayTeamLogo());
        embedBuilder.setTimestamp(Instant.now());


        activeThread.sendMessageEmbeds(embedBuilder.build()).queue(message -> {
            message.addReaction(Emoji.fromUnicode("U+1F63C")).queue(); // cat
            message.addReaction(Emoji.fromUnicode("U+1F645")).queue(); // X

        }, throwable -> {
            System.err.println("Error sending message: " + throwable.getMessage());
        });

        LOGGER.fine("Starting updates for three stars");
        sendUpdatedThreeStarsMessage(currentGame, activeThread);


    }

    private void sendUpdatedThreeStarsMessage(GameInfo currentGame, ThreadChannel activeThread) {
        LOGGER.fine("Updating three stars");
        EmbedBuilder embedBuilder = new EmbedBuilder();
        embedBuilder.setTitle("Current Three Stars");
        embedBuilder.setDescription("No stars yet!");
        embedBuilder.setFooter("Voting for this game is currently open");

        // Send the initial message
        activeThread.sendMessageEmbeds(embedBuilder.build()).queue(message -> {
            // Schedule updates for this message
            ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
            Runnable updateTask = () -> updateThreeStarsMessage(message, currentGame);

            int delay = 0;
            int period = 30;
            int totalRuns = 2500; // Scheduling for an extended period, adjust as necessary

            for (int i = 0; i < totalRuns; i++) {
                scheduler.schedule(updateTask, delay + period * i, TimeUnit.SECONDS);
            }

            // Schedule the final update to change the footer after all updates
            scheduler.schedule(() -> {
                EmbedBuilder finalEmbedBuilder = new EmbedBuilder();
                finalEmbedBuilder.setTitle("Current Three Stars");
                finalEmbedBuilder.setDescription("Voting for this game has been closed");
                message.editMessageEmbeds(finalEmbedBuilder.build()).queue();
            }, delay + period * totalRuns, TimeUnit.SECONDS);

            scheduler.shutdown();
        });
    }

    private void updateThreeStarsMessage(Message message, GameInfo currentGame) {
        List<PlayerVoteCount> topThreeStars = getTopThreeStarsForMostRecentGame();

        // Start with a non-empty description to ensure embed is never empty
        String description = topThreeStars.isEmpty() ? "No stars yet." : "Current Top Stars:";

        EmbedBuilder updatedEmbed = new EmbedBuilder();
        updatedEmbed.setTitle(currentGame.getAwayTeam() + " VS " + currentGame.getHomeTeam() + " - Current Three Stars");
        updatedEmbed.setColor(Color.RED);
        updatedEmbed.setDescription(description); // Set initial description


        // Only add fields if there are stars to display
        if (!topThreeStars.isEmpty()) {
            for (int i = 0; i < topThreeStars.size(); i++) {
                PlayerVoteCount star = topThreeStars.get(i);
                updatedEmbed.addField((i + 1) + " Star", star.playerName() + " - Votes: " + star.voteCount(), false);
            }
        }

        // Build the embed and check if it's empty
        MessageEmbed embed = updatedEmbed.build();
        if (embed.isEmpty()) {
            System.err.println("The embed is unexpectedly empty.");
        } else {
            // Update the message with the new information
            message.editMessageEmbeds(embed).queue(null, throwable -> {
                System.err.println("Failed to update three stars message: " + throwable.getMessage());
            });
        }
    }


    public List<PlayerVoteCount> getTopThreeStarsForMostRecentGame() {
        List<PlayerVoteCount> topThreeStars = new ArrayList<>();
        String sql = "SELECT CONCAT(p.first_name, ' ', p.last_name) AS full_name, vs.vote_count " +
                "FROM vote_summary vs " +
                "JOIN players p ON vs.player_id = p.id " +
                "JOIN games g ON vs.game_id = g.id " +
                "WHERE g.game_date = (SELECT MAX(game_date) FROM games) " +
                "ORDER BY vs.vote_count DESC " +
                "LIMIT 3";

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    String playerName = rs.getString("full_name");
                    int voteCount = rs.getInt("vote_count");
                    topThreeStars.add(new PlayerVoteCount(playerName, voteCount));
                }
            }
        } catch (SQLException e) {
            LOGGER.severe(e.getMessage());
        }

        return topThreeStars;
    }





}
