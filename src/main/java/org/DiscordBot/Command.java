package org.DiscordBot;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

public interface InterfaceCommand {
    void execute(MessageReceivedEvent event, String[] args);
}
