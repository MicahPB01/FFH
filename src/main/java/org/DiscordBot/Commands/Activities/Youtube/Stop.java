package org.DiscordBot.Commands.Activities.Youtube;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.DiscordBot.Command;
import org.DiscordBot.Commands.Activities.Youtube.PlaybackHandlers.GuildMusicManager;
import org.DiscordBot.Commands.Activities.Youtube.PlaybackHandlers.PlayerManager;

public class Stop implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        musicManager.scheduler.queue.clear();
        musicManager.audioPlayer.stopTrack();
        musicManager.audioPlayer.setPaused(false);

        event.getChannel().sendMessage("Playback has been stopped and the queue has been cleared").queue();

        AudioManager audioManager = event.getGuild().getAudioManager();

        audioManager.closeAudioConnection();



    }

    @Override
    public String getDescription() {
        return "Stops the playback and removes the queue from Avalanche if currently playing";
    }
}
