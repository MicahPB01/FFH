package org.DiscordBot.Commands.Activities;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.DiscordBot.Command;

import java.awt.*;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Question implements Command {


    private Queue<String> questionsQueue;

    public Question()   {
        List<String> questions = List.of(
                "If you could live anywhere in the world, where would it be?",
                "What's your favorite book, and why?",
                "If you could have any superpower, what would it be?",
                "What's the most interesting place you've ever visited?",
                "If you could meet any historical figure, who would you choose and why?",
                "What's your favorite hobby or pastime?",
                "What's the best movie you've seen recently?",
                "If you could learn any skill instantly, what would it be?",
                "What's your favorite thing to do on weekends?",
                "What's the best piece of advice you've ever received?",
                "If you could have dinner with any three people (alive or dead), who would they be?",
                "What would be your dream job?",
                "If you could travel to any planet, which one would you choose?",
                "What's your favorite childhood memory?",
                "What's a goal you're currently working toward?",
                "What's the most daring thing you've ever done?",
                "What's your favorite type of cuisine?",
                "If you could be any fictional character, who would you choose?",
                "What's a talent you wish you had?",
                "What's something you've always wanted to try but haven't yet?",
                "What's your idea of a perfect day?",
                "Who has been the most influential person in your life?",
                "What's your favorite animal and why?",
                "If you could start a charity, what would it be for?",
                "What's your favorite quote or saying?",
                "What would you do if you won the lottery?",
                "What's your favorite sport to watch or play?",
                "What's the best concert you've ever been to?",
                "What's one thing you can't live without?",
                "What's your favorite way to relax after a long day?",
                "If you could change one thing about the world, what would it be?",
                "What's a subject you wish you knew more about?",
                "What's your favorite thing about yourself?",
                "What's something that always makes you laugh?",
                "If you could live in any movie, which one would it be?",
                "What's your favorite song or artist at the moment?",
                "What's the best vacation you've ever been on?",
                "What's a fear you've overcome?",
                "If you could bring back any fashion trend, what would it be?",
                "What's your favorite family tradition?",
                "What's the best gift you've ever received?",
                "What's the most unusual food you've ever tried?",
                "Do you believe in extraterrestrial life? Why or why not?",
                "What's your favorite memory from school?",
                "What would your perfect weekend involve?",
                "If you could switch lives with anyone for a day, who would it be?",
                "What's something you've never done but would like to try?",
                "What's the most beautiful place you've ever seen?",
                "What's a book you think everyone should read?",
                "What's your favorite comfort food?",
                "What's the most adventurous thing you've ever done?",
                "Who is your favorite fictional character?",
                "What's your favorite thing to cook or bake?",
                "If you could eliminate one thing from your daily routine, what would it be?",
                "What's a language you'd love to learn?",
                "What's something that makes you unique?",
                "What's a hobby you'd like to take up?",
                "What's your favorite way to spend time outdoors?",
                "If you could live in any TV show, which one would it be?",
                "What's your favorite season and why?",
                "If you could have a conversation with your younger self, what would you say?",
                "What's a piece of technology you can't live without?",
                "What's the most important lesson you've learned in life?",
                "What's an event in history that fascinates you?",
                "What's something you're looking forward to in the future?",
                "What's your favorite myth or legend?",
                "What's the best piece of life advice you've been given?",
                "What's something you thought you'd grow out of but haven't?",
                "What's a talent or skill you admire in others?",
                "If you could have any animal as a pet, what would it be?",
                "What's a movie that made a significant impact on you?",
                "What's your favorite outdoor activity?",
                "If you could time travel, where and when would you go?",
                "What's a challenge you're proud of overcoming?",
                "What's something you wish you were better at?",
                "What's your favorite way to spend a rainy day?",
                "What's a song that brings back memories?",
                "What's the most meaningful gift you've ever received?",
                "What's something you've always wanted to learn?",
                "What's the most valuable thing you've learned this year?",
                "What's something you were afraid of as a child?",
                "What's the best advice you've ever given?",
                "What's a habit you're trying to break or form?",
                "What's your idea of a dream vacation?",
                "If you could choose any era to live in, which would it be?",
                "What's a personal goal you're working toward?",
                "What's your favorite board game or card game?",
                "If you could write a book, what genre would it be?",
                "What's something you're grateful for today?",
                "What's a country you'd love to visit?",
                "What's a piece of advice you'd give to your future self?",
                "What's something you've done that took you out of your comfort zone?",
                "What's your favorite way to de-stress?",
                "What's a childhood movie that you still enjoy?",
                "What's something you're passionate about?",
                "If you could witness any event past, present, or future, what would it be?",
                "What's something you've recently become interested in?",
                "What's a skill you've recently acquired or want to learn?",
                "What's your favorite type of weather?",
                "If you could open a business, what kind of business would it be?",
                "What's something that you find really relaxing?",
                "What's a habit you think everyone should adopt?",
                "If you could be famous for one thing, what would it be?",
                "What's a food you hated as a child but love now?",
                "What's a random act of kindness you've experienced?",
                "What's your favorite family recipe?",
                "What's a subject you'd like to learn more about?",
                "What's a talent you wish you had?",
                "What's something that recently made you smile?",
                "If you could be a character in any book, who would you choose?",
                "What's a city you've always wanted to visit?",
                "What's a song that you can listen to on repeat?",
                "What's a tradition you want to start or continue?",
                "What's a new hobby you'd like to try out?",
                "What's an achievement you're proud of?",
                "What's something that motivates you?",
                "What's the best way to start the day?",
                "What's a question you wish people would ask you?"
        );

        resetQuestions(questions);
    }



    @Override
    public void execute(MessageReceivedEvent event, String[] args) {




        if(questionsQueue.isEmpty())   {
            List<String> questions = List.of(
                    "If you could live anywhere in the world, where would it be?",
                    "What's your favorite book, and why?",
                    "If you could have any superpower, what would it be?",
                    "What's the most interesting place you've ever visited?",
                    "If you could meet any historical figure, who would you choose and why?",
                    "What's your favorite hobby or pastime?",
                    "What's the best movie you've seen recently?",
                    "If you could learn any skill instantly, what would it be?",
                    "What's your favorite thing to do on weekends?",
                    "What's the best piece of advice you've ever received?",
                    "If you could have dinner with any three people (alive or dead), who would they be?",
                    "What would be your dream job?",
                    "If you could travel to any planet, which one would you choose?",
                    "What's your favorite childhood memory?",
                    "What's a goal you're currently working toward?",
                    "What's the most daring thing you've ever done?",
                    "What's your favorite type of cuisine?",
                    "If you could be any fictional character, who would you choose?",
                    "What's a talent you wish you had?",
                    "What's something you've always wanted to try but haven't yet?",
                    "What's your idea of a perfect day?",
                    "Who has been the most influential person in your life?",
                    "What's your favorite animal and why?",
                    "If you could start a charity, what would it be for?",
                    "What's your favorite quote or saying?",
                    "What would you do if you won the lottery?",
                    "What's your favorite sport to watch or play?",
                    "What's the best concert you've ever been to?",
                    "What's one thing you can't live without?",
                    "What's your favorite way to relax after a long day?",
                    "If you could change one thing about the world, what would it be?",
                    "What's a subject you wish you knew more about?",
                    "What's your favorite thing about yourself?",
                    "What's something that always makes you laugh?",
                    "If you could live in any movie, which one would it be?",
                    "What's your favorite song or artist at the moment?",
                    "What's the best vacation you've ever been on?",
                    "What's a fear you've overcome?",
                    "If you could bring back any fashion trend, what would it be?",
                    "What's your favorite family tradition?",
                    "What's the best gift you've ever received?",
                    "What's the most unusual food you've ever tried?",
                    "Do you believe in extraterrestrial life? Why or why not?",
                    "What's your favorite memory from school?",
                    "What would your perfect weekend involve?",
                    "If you could switch lives with anyone for a day, who would it be?",
                    "What's something you've never done but would like to try?",
                    "What's the most beautiful place you've ever seen?",
                    "What's a book you think everyone should read?",
                    "What's your favorite comfort food?",
                    "What's the most adventurous thing you've ever done?",
                    "Who is your favorite fictional character?",
                    "What's your favorite thing to cook or bake?",
                    "If you could eliminate one thing from your daily routine, what would it be?",
                    "What's a language you'd love to learn?",
                    "What's something that makes you unique?",
                    "What's a hobby you'd like to take up?",
                    "What's your favorite way to spend time outdoors?",
                    "If you could live in any TV show, which one would it be?",
                    "What's your favorite season and why?",
                    "If you could have a conversation with your younger self, what would you say?",
                    "What's a piece of technology you can't live without?",
                    "What's the most important lesson you've learned in life?",
                    "What's an event in history that fascinates you?",
                    "What's something you're looking forward to in the future?",
                    "What's your favorite myth or legend?",
                    "What's the best piece of life advice you've been given?",
                    "What's something you thought you'd grow out of but haven't?",
                    "What's a talent or skill you admire in others?",
                    "If you could have any animal as a pet, what would it be?",
                    "What's a movie that made a significant impact on you?",
                    "What's your favorite outdoor activity?",
                    "If you could time travel, where and when would you go?",
                    "What's a challenge you're proud of overcoming?",
                    "What's something you wish you were better at?",
                    "What's your favorite way to spend a rainy day?",
                    "What's a song that brings back memories?",
                    "What's the most meaningful gift you've ever received?",
                    "What's something you've always wanted to learn?",
                    "What's the most valuable thing you've learned this year?",
                    "What's something you were afraid of as a child?",
                    "What's the best advice you've ever given?",
                    "What's a habit you're trying to break or form?",
                    "What's your idea of a dream vacation?",
                    "If you could choose any era to live in, which would it be?",
                    "What's a personal goal you're working toward?",
                    "What's your favorite board game or card game?",
                    "If you could write a book, what genre would it be?",
                    "What's something you're grateful for today?",
                    "What's a country you'd love to visit?",
                    "What's a piece of advice you'd give to your future self?",
                    "What's something you've done that took you out of your comfort zone?",
                    "What's your favorite way to de-stress?",
                    "What's a childhood movie that you still enjoy?",
                    "What's something you're passionate about?",
                    "If you could witness any event past, present, or future, what would it be?",
                    "What's something you've recently become interested in?",
                    "What's a skill you've recently acquired or want to learn?",
                    "What's your favorite type of weather?",
                    "If you could open a business, what kind of business would it be?",
                    "What's something that you find really relaxing?",
                    "What's a habit you think everyone should adopt?",
                    "If you could be famous for one thing, what would it be?",
                    "What's a food you hated as a child but love now?",
                    "What's a random act of kindness you've experienced?",
                    "What's your favorite family recipe?",
                    "What's a subject you'd like to learn more about?",
                    "What's a talent you wish you had?",
                    "What's something that recently made you smile?",
                    "If you could be a character in any book, who would you choose?",
                    "What's a city you've always wanted to visit?",
                    "What's a song that you can listen to on repeat?",
                    "What's a tradition you want to start or continue?",
                    "What's a new hobby you'd like to try out?",
                    "What's an achievement you're proud of?",
                    "What's something that motivates you?",
                    "What's the best way to start the day?",
                    "What's a question you wish people would ask you?"
            );



            resetQuestions(questions);
        }

        String question = questionsQueue.poll();
        assert question != null;
        EmbedBuilder embed = new EmbedBuilder();
        embed.setTitle("Random Question"); // You can set a title for your embed
        embed.setDescription(question);
        embed.setColor(Color.CYAN); // You can choose any color you like

        // Send the embed to the channel
        event.getChannel().sendMessageEmbeds(embed.build()).queue();

    }

    @Override
    public String getDescription() {
        return "Presents a random question to spark conversation.";
    }

    private void resetQuestions(List<String> questions)   {
        List<String> shuffled = new LinkedList<>(questions);
        Collections.shuffle(shuffled);
        questionsQueue = new LinkedList<>(shuffled);
    }




}
