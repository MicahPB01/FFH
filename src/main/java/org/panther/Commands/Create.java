package org.panther.Commands;

import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.panther.MessageListener;
import org.panther.Commands.Add.UserProfileSetupState;

public class Create implements Command {
    private MessageListener messageListener;

    public Create(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.isFromType(ChannelType.TEXT)) { // Ensure it's from a server channel
            long userId = event.getAuthor().getIdLong();
            long serverId = event.getGuild().getIdLong(); // Get the server ID



            // Store the server ID and user ID association
            messageListener.setUserServer(userId, serverId);

            // Set the initial state for the user
            messageListener.setUserState(userId, UserProfileSetupState.NICKNAME);

            // Open a private channel and start the profile setup process
            event.getAuthor().openPrivateChannel().queue((privateChannel) -> {
                privateChannel.sendMessage("Let's set up your profile!\n\nDo you have a nickname? If yes, please type it now.").queue();
            });
        } else {
            event.getChannel().sendMessage("This command can only be used in server channels.").queue();
        }
    }

    @Override
    public String getDescription() {
        return "Used to create a profile. If one is already created, it will replace what is currently there.";
    }
}
