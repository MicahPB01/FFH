package org.DiscordBot.Commands.Activities.Psych;

import net.dv8tion.jda.api.entities.User;

import java.util.*;

public class PsychGameState {
    private User doctor;
    private final Set<User> players;
    private boolean gameInProgress;
    private String condition;
    String[] possibleConditions = {
            "Respond in exactly five words.",
            "Speak like a fast-food chain clerk.",
            "End every sentence with a question.",
            "Pretend to be a famous movie star.",
            "Act like you're from the future.",
            "Speak as if you're underwater.",
            "Imitate a famous cartoon character.",
            "Whisper all your responses.",
            "Use movie titles in answers.",
            "Pretend you're an alien explaining Earth.",
            "Talk like a Shakespearean actor.",
            "Respond as if you're a superhero.",
            "Pretend you're a mischievous wizard.",
            "Answer as if you're a robot.",
            "Behave like a pirate.",
            "Imitate a famous singer.",
            "Act like a historical figure.",
            "Pretend you're a detective.",
            "Talk like a news reporter.",
            "Answer as if you're a grandparent.",
            "Speak with exaggerated politeness.",
            "Act as if you're a spy.",
            "Pretend you're a royal monarch.",
            "Speak as if you're a sports commentator.",
            "Respond as a weary traveler.",
            "Act like you're in a musical.",
            "Pretend you're an astronaut in space.",
            "Speak like a medieval knight.",
            "Act as if you're in a silent movie (use exaggerated expressions).",
            "Imitate a famous scientist.",
            "Behave like a mischievous child.",
            "Talk like you're in a noir film.",
            "Pretend to be a ghost.",
            "Act like an overenthusiastic salesperson.",
            "Speak as if you're a wise sage.",
            "Answer as a skeptical detective.",
            "Pretend you're a character from a fantasy novel.",
            "Speak with exaggerated slowness.",
            "Act as if you're a world traveler.",
            "Imitate a famous athlete.",
            "Pretend you're a secret agent.",
            "Speak like a cowboy/cowgirl.",
            "Act as if you're a famous chef.",
            "Respond as a curious scientist.",
            "Pretend you're a lost tourist.",
            "Speak like a poet.",
            "Act as if you're a vampire.",
            "Imitate a famous philosopher.",
            "Respond as if you're a time traveler from the past.",
            "Answer as if you're always in a hurry."
    };

    public PsychGameState() {
        this.gameInProgress = false;
        this.players = new HashSet<>();
    }

    public void startGame(User doctor, Set<User> players, List<String> conditions) {
        this.doctor = doctor;
        this.gameInProgress = true;
        assignPsychiatristAndCondition();
    }

    public void addPlayer(User player) {
        players.add(player);
    }

    public int getNumberOfPlayers() {
        return players.size();
    }

    public String getCondition()   {
        return condition;
    }


    public void endGame() {
        this.gameInProgress = false;
        this.players.clear();
        // Additional cleanup if needed
    }

    public boolean isGameInProgress() {
        return gameInProgress;
    }

    public User getDoctor() {
        return doctor;
    }

    public Set<User> getPlayers() {
        return players;
    }

    private void assignPsychiatristAndCondition() {
        Random random = new Random();
        int index = random.nextInt(players.size());
        User[] playersArray = players.toArray(new User[0]);
        doctor = playersArray[index];

        // Randomly select a condition for all players except the psychiatrist
        condition = possibleConditions[random.nextInt(possibleConditions.length)];

        for (User player : players) {
            if (!player.equals(doctor)) {
                // Send DM to each player with the condition
                player.openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("You are a PATIENT\nYour condition for the Psychiatrist game is: " + condition).queue();
                });
            } else {
                // Send DM to the psychiatrist
                player.openPrivateChannel().queue(privateChannel -> {
                    privateChannel.sendMessage("You are the Psychiatrist. Try to figure out the common condition!").queue();
                });
            }
        }

        // You might want additional methods for game management
    }
}
