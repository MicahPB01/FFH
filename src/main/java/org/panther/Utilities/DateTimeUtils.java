package org.panther.Utilities;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DateTimeUtils {

    public static String findTime(String time)   {
        System.out.println(time);
        Pattern pattern = Pattern.compile("\\d.*?ET");
        Matcher matcher = pattern.matcher(time);

        if (matcher.find()) {
            time = matcher.group(0);
            return time;
        }

        return "Couldn't find time";
    }

    public static String reverseDate(String date)   {
        System.out.println(date);

        String year = date.substring(0,4);
        String month = date.substring(4,6);
        String day = date.substring(6,8);

        return month + "/" + day + "/" + year;

    }

    //Find the date for the requested game score or info
    public static String findDate(SlashCommandInteractionEvent event)   {
        String date;

        //if a date is provided, grab the included date, otherwise the date will be set as the current date
        if (event.getOption("date") != null) {

            date = dateFromCommand(Objects.requireNonNull(event.getOption("date")).getAsString());

        }
        else  {
            date = dateFromCurrentDay();
        }

        return date;
    }

    public static String dateFromCommand(String dateFromCommand)   {
        return dateFromCommand.replaceAll("/", "");
    }

    public static String dateFromCurrentDay()   {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

        return now.format(formatter);
    }


}
