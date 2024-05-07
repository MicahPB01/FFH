package org.panther.CommandInteraction.Commands;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import org.panther.CommandInteraction.Command;
import org.panther.Utilities.AppLogger;

import java.util.logging.Logger;

/**
 * simple ping command
 * HElps get the status of the bot
 */

public class Ping implements Command {
    private static final Logger LOGGER = AppLogger.getLogger();



    @Override
    public void execute(SlashCommandInteractionEvent event) {
        LOGGER.fine("Pinging");
        event.reply("Pong!").queue();
    }
}
