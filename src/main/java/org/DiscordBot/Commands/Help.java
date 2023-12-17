package org.DiscordBot.Commands;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;

import java.util.Map;

public class Help implements Command {
    private final Map<String, Command> commands;


    public Help(Map<String, Command> commands) {
        this.commands = commands;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        StringBuilder sb = new StringBuilder("Available commands:\n");
        for (String command : commands.keySet()) {
            sb.append("!").append(command).append("\n");
        }
        event.getChannel().sendMessage(sb.toString()).queue();
    }
}
