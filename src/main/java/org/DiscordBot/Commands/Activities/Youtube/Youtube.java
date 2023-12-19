package org.DiscordBot.Commands.Activities.Youtube;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.managers.AudioManager;
import org.DiscordBot.Command;
import org.DiscordBot.Commands.Activities.Youtube.PlaybackHandlers.PlayerManager;

import java.net.URI;
import java.net.URISyntaxException;

public class Youtube implements Command {
    private final AudioPlayerManager audioPlayerManager;

    public Youtube(AudioPlayerManager audioPlayerManager)   {
        this.audioPlayerManager = audioPlayerManager;
    }



    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (args.length < 2) {
            event.getChannel().sendMessage("Please provide a link.").queue();
            return;
        }



        String link = args[1];
        System.out.println("Loading link: " + link);
        Member member = event.getMember();
        if (member == null) {
            event.getChannel().sendMessage("Having trouble getting requesting user.").queue();
            return;
        }

        GuildVoiceState voiceState = member.getVoiceState();
        if (voiceState == null || !voiceState.inAudioChannel()) {
            event.getChannel().sendMessage("Please enter a voice channel first!").queue();
            return;
        }

        AudioManager audioManager = event.getGuild().getAudioManager();
        if (!audioManager.isConnected()) {
            audioManager.openAudioConnection(voiceState.getChannel());
        }


        long channelID = event.getChannel().getIdLong();

        TextChannel textChannel = event.getGuild().getTextChannelById(channelID);

        PlayerManager.getINSTANCE().loadAndPlay(event.getGuild().getTextChannelById(channelID), link);

    }

    public boolean isUrl(String url)   {
        try{
            new URI(url);
            return true;
        }
        catch (URISyntaxException e)   {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public String getDescription() {
        return "!audio [link to video] will play the audio of the linked Youtube video. Default volume is 20";
    }
}
