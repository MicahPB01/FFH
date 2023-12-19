package org.DiscordBot.Commands.Activities.Youtube;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;
import org.DiscordBot.Commands.Activities.Youtube.PlaybackHandlers.GuildMusicManager;
import org.DiscordBot.Commands.Activities.Youtube.PlaybackHandlers.PlayerManager;

public class Volume implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        if(args.length < 2)   {
            event.getChannel().sendMessage("Please specify a volume level").queue();
            return;
        }


        int volume = Integer.parseInt(args[1]);

        if(volume > 150 || volume < 0)   {
            event.getChannel().sendMessage("Volume level should be between 0 and 150").queue();
            return;
        }

        GuildMusicManager musicManager = PlayerManager.getINSTANCE().getMusicManager(event.getGuild());
        musicManager.audioPlayer.setVolume(volume);
        event.getChannel().sendMessage("Set volume to " + volume).queue();


    }

    @Override
    public String getDescription() {
        return "Allows you to change the volume of the bot. Follow \"!volume\" with a number 0-150";
    }
}
