package org.panther.Commands.Activities;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.panther.Commands.Command;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONObject;

public class Smile implements Command {
    @Override
    public void execute(MessageReceivedEvent event, String[] args) {

        try {
            String url = "https://www.reddit.com/r/MadeMeSmile/hot.json?limit=15";
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Discord Bot")
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject json = new JSONObject(response.body());
            JSONArray posts = json.getJSONObject("data").getJSONArray("children");

            Random random = new Random();
            JSONObject post = posts.getJSONObject(random.nextInt(posts.length())).getJSONObject("data");

            String postTitle = post.getString("title");
            String postUrl = post.getString("url");

            event.getChannel().sendMessage(postTitle + "\n" + postUrl).queue();
        } catch (Exception e) {
            e.printStackTrace();
            event.getChannel().sendMessage("An error occurred while fetching a post.").queue();
        }




    }

    @Override
    public String getDescription() {
        return "Presents a top post from r/MadeMeSmile.";
    }



}
