package org.panther.Commands.Activities.Youtube;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.panther.Commands.Command;
import org.panther.Commands.Activities.Youtube.PlaybackHandlers.GuildMusicManager;
import org.panther.Commands.Activities.Youtube.PlaybackHandlers.PlayerManager;

public class Skip implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        musicManager.scheduler.nextTrack();
        event.getChannel().sendMessage("Skipping current track!").queue();
    }


    @Override
    public String getDescription() {
        return "Skips the current song and moves to the next one in the queue";
    }
}
