package org.panther.Commands.Activities.Psych;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.panther.Commands.Command;

public class PsychJoin implements Command {

    private PsychGameState gameState;

    public PsychJoin(PsychGameState gameState) {
        this.gameState = gameState;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (gameState.isGameInProgress()) {
            event.getChannel().sendMessage("A game is already in progress. Please wait until it's over.").queue();
            return;
        }


        gameState.addPlayer(event.getAuthor());

        event.getChannel().sendMessage(event.getAuthor().getAsMention() + " has joined the Psychiatrist game.").queue();
    }

    @Override
    public String getDescription() {
        return "This command registers you to play in the Psychiatrist game." +
                "";
    }
}
