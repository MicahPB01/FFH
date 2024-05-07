package org.panther.CommandInteraction.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.panther.CommandInteraction.Command;
import org.panther.Database;
import org.panther.Models.PlayerVoteCount;
import org.panther.Utilities.AppLogger;

import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Command to retrieve and display the top 10 players based on fan star vate
 */


public class Overall implements Command {

    private static final Logger LOGGER = AppLogger.getLogger();
    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.fine("Getting overall star votes");

        List<PlayerVoteCount> topPlayers = getTopTenPlayersByVotes();
        if(topPlayers.isEmpty())   {
            event.reply("No votes have been tallied yet").setEphemeral(true).queue();
        }
        else   {
            EmbedBuilder embed = new EmbedBuilder();
            embed.setTitle("Top 10 Players by Votes");
            embed.setColor(Color.RED);
            for(PlayerVoteCount player : topPlayers)   {
                embed.addField(player.playerName(), "Total Votes: " + player.voteCount(), false);
            }
            event.replyEmbeds(embed.build()).queue();
        }
    }



    private List<PlayerVoteCount> getTopTenPlayersByVotes()   {
        LOGGER.fine("Checking data base for top 10 players by vote");
        List<PlayerVoteCount> topTenPlayers = new ArrayList<>();
        String sql = "SELECT CONCAT(p.first_name, ' ', p.last_name) AS full_name, SUM(vs.vote_count) AS total_votes " +
                "FROM vote_summary vs " +
                "JOIN players p ON vs.player_id = p.id " +
                "GROUP BY vs.player_id " +
                "ORDER BY total_votes DESC " +
                "LIMIT 10";

        try (Connection conn = Database.getConnection()) {
            assert conn != null;
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    while (rs.next()) {
                        String playerName = rs.getString("full_name");
                        int totalVotes = rs.getInt("total_votes");
                        topTenPlayers.add(new PlayerVoteCount(playerName, totalVotes));
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.warning(e.getMessage());
        }

        LOGGER.fine("Got players");
        return topTenPlayers;
    }


}
