package org.DiscordBot.Commands;

import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;
import org.DiscordBot.Commands.Activities.Psych.*;
import org.DiscordBot.Commands.Activities.Youtube.Skip;
import org.DiscordBot.Commands.Activities.Youtube.Stop;
import org.DiscordBot.Commands.Activities.Youtube.Volume;
import org.DiscordBot.Commands.Activities.Youtube.Youtube;
import org.DiscordBot.Commands.Add.ObtainProfile;
import org.DiscordBot.Commands.Activities.Question;
import org.DiscordBot.Commands.Activities.Smile;
import org.DiscordBot.MessageListener;





import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();
    private MessageListener messageListener;
    PsychGameState psychGameState = new PsychGameState();


    AudioPlayerManager playerManager = new DefaultAudioPlayerManager();





    public CommandHandler()   {
        commands.put("ping", new Ping());
        commands.put("help", new Help(commands));
        commands.put("profile", new ObtainProfile());
        commands.put("question", new Question());
        commands.put("smile", new Smile());
        commands.put("psych", new PsychJoin(psychGameState));
        commands.put("psychstart", new PsychStart(psychGameState));
        commands.put("giveup", new GiveUp(psychGameState));
        commands.put("cured", new Cured(psychGameState));
        commands.put("audio", new Youtube(playerManager));
        commands.put("volume", new Volume());
        commands.put("stop", new Stop());
        commands.put("skip", new Skip());
    }

    public void handle(MessageReceivedEvent event) {
        String[] args = event.getMessage().getContentRaw().split("\\s+");
        if (args.length > 0 && commands.containsKey(args[0].toLowerCase().substring(1))) { // Note the substring to remove '!'
            commands.get(args[0].toLowerCase().substring(1)).execute(event, args);
        }
    }
    public void setMessageListener(MessageListener messageListener) {
        this.messageListener = messageListener;
        commands.put("create", new Create(messageListener));
        commands.put("edit", new Edit(messageListener));
    }



}
