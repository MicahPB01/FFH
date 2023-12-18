package org.DiscordBot.Commands.Activities.Psych;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;

public class Cured implements Command {
    private PsychGameState gameState;

    public Cured(PsychGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (!gameState.isGameInProgress()) {
            event.getChannel().sendMessage("There is no game in progress currently.").queue();
            return;
        }

        if (!event.getAuthor().equals(gameState.getDoctor())) {
            event.getChannel().sendMessage("Only the Psychiatrist can declare the game cured.").queue();
            return;
        }

        // End the game and notify players
        gameState.endGame();
        event.getChannel().sendMessage("The Psychiatrist has declared the game cured!").queue();
    }

    @Override
    public String getDescription() {
        return "This is used by the Psychiatrist when they properly diagnose the patients. This will end the game with a victory.";
    }
}
