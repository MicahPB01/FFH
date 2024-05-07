package org.panther.Utilities;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.logging.Logger;

public class DataFetcher {
    private static final Logger LOGGER = AppLogger.getLogger();

    public static String fetchPlayerData() throws IOException, InterruptedException   {
        LOGGER.fine("Fetching player data");
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://api-web.nhle.com/v1/roster/FLA/current")
                .build();

        Response response = client.newCall(request).execute();


        assert response.body() != null;
        return response.body().string();
    }





}
