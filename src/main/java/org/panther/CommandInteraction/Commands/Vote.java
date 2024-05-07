package org.panther.CommandInteraction.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.panther.CommandInteraction.Command;
import org.panther.Database;
import org.panther.Utilities.AppLogger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Objects;
import java.util.logging.Logger;

public class Vote implements Command {
    private static final Logger LOGGER = AppLogger.getLogger();

    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.fine("Tallying votes");

        String firstStar = Objects.requireNonNull(event.getOption("firststar")).getAsString();
        String secondStar = Objects.requireNonNull(event.getOption("secondstar")).getAsString();
        String thirdStar = Objects.requireNonNull(event.getOption("thirdstar")).getAsString();

        int gameID = findMostRecentGameID();

        processVote(gameID, firstStar, secondStar, thirdStar);

        event.reply("Your votes have been tallied!").queue();

    }



    private static void processVote(int gameID, String firstStar, String secondStar, String thirdStar) {
        LOGGER.fine("processing vote");
        Map<String, Integer> stars = Map.of(
                firstStar, 3,
                secondStar, 2,
                thirdStar, 1
        );

        stars.forEach((starName, points) -> {
            int playerID = findPlayerIDByName(starName);
            if (playerID != -1) {
                updateVoteSummary(gameID, playerID, points);
            } else {
                System.out.println("Player not found");
            }
        });
    }

    private static int findMostRecentGameID() {
        LOGGER.fine("Getting most recent game ID");
        String sql = "SELECT id FROM games ORDER BY id DESC LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        LOGGER.warning("Most recent game not found. Unable to tally votes");
        return -1; // Indicates not found
    }

    private static void updateVoteSummary(int gameId, int playerId, int points) {
        String sql = "INSERT INTO vote_summary (game_id, player_id, vote_count) VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE vote_count = vote_count + VALUES(vote_count)";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, gameId);
            pstmt.setInt(2, playerId);
            pstmt.setInt(3, points);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            LOGGER.severe(e.toString());
        }
    }

    private static int findPlayerIDByName(String fullName) {
        LOGGER.fine("Getting player id from name");
        String[] names = fullName.split(" ", 2);  // Assumes name is in "First Last" format
        String sql = "SELECT id FROM players WHERE first_name = ? AND last_name = ? LIMIT 1";
        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, names[0]); // first_name
            pstmt.setString(2, names.length > 1 ? names[1] : ""); // last_name
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            LOGGER.severe(e.toString());
        }
        LOGGER.warning("Could not find player name");
        return -1; // Indicates not found
    }


}
