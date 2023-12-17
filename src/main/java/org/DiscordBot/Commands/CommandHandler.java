package org.DiscordBot.Commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;
import org.DiscordBot.Commands.Add.GeneralInfo;
import org.DiscordBot.Commands.Add.ObtainProfile;
import org.DiscordBot.MessageListener;

import java.util.HashMap;
import java.util.Map;

public class CommandHandler {
    private final Map<String, Command> commands = new HashMap<>();
    private MessageListener messageListener;




    public CommandHandler()   {
        commands.put("ping", new Ping());
        commands.put("help", new Help(commands));
        commands.put("addinfo", new GeneralInfo());
        commands.put("profile", new ObtainProfile());
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
