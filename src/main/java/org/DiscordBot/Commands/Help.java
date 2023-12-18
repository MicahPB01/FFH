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
        StringBuilder helpMessage = new StringBuilder("Available Commands:\n");

        for (Map.Entry<String, Command> commandEntry : commands.entrySet()) {
            String commandName = commandEntry.getKey();
            String description = commandEntry.getValue().getDescription();
            helpMessage.append("**!").append(commandName).append("**: ").append(description).append("\n");
            // Note the addition of "!" before the command name
        }

        event.getChannel().sendMessage(helpMessage.toString()).queue();
    }

    @Override
    public String getDescription() {
        return "Displays a list of available commands and their descriptions.";
    }
}
