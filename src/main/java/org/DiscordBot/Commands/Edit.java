package org.DiscordBot.Commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.channel.ChannelType;
import net.dv8tion.jda.api.entities.channel.concrete.PrivateChannel;
import net.dv8tion.jda.api.entities.emoji.Emoji;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;
import org.DiscordBot.MessageListener;
import org.DiscordBot.UserProfileSetupState;

public class Edit implements Command {
    private final MessageListener messageListener;

    public Edit(MessageListener messageListener) {
        this.messageListener = messageListener;
    }

    @Override
    public void execute(MessageReceivedEvent event, String[] args) {
        if (event.isFromType(ChannelType.TEXT)) {
            long userId = event.getAuthor().getIdLong();
            long serverId = event.getGuild().getIdLong();



            messageListener.setUserServer(userId, serverId);

            event.getAuthor().openPrivateChannel().queue(privateChannel -> {
                sendEditableFieldEmbed(privateChannel, "Nickname", "Edit your nickname", "\u2705", userId, "EDIT_NICKNAME");
                sendEditableFieldEmbed(privateChannel, "Alma Mater", "Edit your Alma Mater", "\u2705", userId, "EDIT_ALMA_MATER");
                sendEditableFieldEmbed(privateChannel, "Contact Info", "Edit your contact information", "\u2705", userId, "EDIT_CONTACT_INFO");
                sendEditableFieldEmbed(privateChannel, "Birthday", "Edit your birthday", "\u2705", userId, "EDIT_BIRTHDAY");
                sendEditableFieldEmbed(privateChannel, "Social Media", "Edit your Social Media Handles", "\u2705", userId, "EDIT_SOCIAL_MEDIA");
                sendEditableFieldEmbed(privateChannel, "General Info", "Edit your general info", "\u2705", userId, "GENERAL_INFO");
                sendEditableFieldEmbed(privateChannel, "Photo", "Upload a new photo", "\u2705", userId, "EDIT_PHOTO");
            });
        } else {
            event.getChannel().sendMessage("This command can only be used in server channels.").queue();
        }
    }

    @Override
    public String getDescription() {
        return "Used to edit your profile. You will DMed by the bot and will react to one of the options to change a specific field.";
    }

    private void sendEditableFieldEmbed(PrivateChannel channel, String title, String description, String reactionEmoji, long userId, String state) {
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle(title);
        embed.setDescription(description);
        channel.sendMessageEmbeds(embed.build()).queue(message -> {
            message.addReaction(Emoji.fromUnicode(reactionEmoji)).queue();
            messageListener.storeEditingMessage(userId, message.getIdLong(), UserProfileSetupState.valueOf(state));
        });
    }
}
