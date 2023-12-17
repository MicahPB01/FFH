package org.DiscordBot.Commands.Activities.Psych;

import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;

import java.util.Set;

public class PsychStart implements Command {
    private static final int MIN_PLAYERS = 2;
    private PsychGameState gameState;
    private Set<User> players;

    public PsychStart(PsychGameState gameState) {
        this.gameState = gameState;
        this.players = gameState.getPlayers();
    }


    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (gameState.isGameInProgress()) {
            event.getChannel().sendMessage("A game is already in progress.").queue();
            return;
        }

        if (gameState.getNumberOfPlayers() < MIN_PLAYERS) {
            event.getChannel().sendMessage("At least " + MIN_PLAYERS + " players are required to start the game. Currently, there are only " + gameState.getNumberOfPlayers() + " players.").queue();
            return;
        }

        gameState.startGame(null, players, null);
        event.getChannel().sendMessage("The Psychiatrist game has started!").queue();

    }
}
