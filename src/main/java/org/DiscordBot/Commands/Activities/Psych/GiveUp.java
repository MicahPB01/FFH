package org.DiscordBot.Commands.Activities.Psych;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;

public class GiveUp implements Command {
    private PsychGameState gameState;

    public GiveUp(PsychGameState gameState)   {
        this.gameState = gameState;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        if (!gameState.isGameInProgress()) {
            event.getChannel().sendMessage("There is no game in progress currently.").queue();
            return;
        }

        if (!event.getAuthor().equals(gameState.getDoctor())) {
            event.getChannel().sendMessage("Only the Psychiatrist can give up the game.").queue();
            return;
        }

        // End the game and notify players
        gameState.endGame();
        event.getChannel().sendMessage("The Psychiatrist has given up. The condition was: " + gameState.getCondition()).queue();
    }
}
