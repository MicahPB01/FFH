package org.panther.Utilities;

import com.fasterxml.jackson.databind.annotation.JsonPOJOBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONObject;
import org.panther.Database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PlayerUpdater {

    public static void updateDatabase(String jsonData)   {
        JSONObject obj = new JSONObject(jsonData);
        JSONArray forwards = obj.getJSONArray("forwards");
        JSONArray defensemen = obj.getJSONArray("defensemen");
        JSONArray goalies = obj.getJSONArray("goalies");

        for(int i = 0; i < forwards.length(); i++)   {
            JSONObject player = forwards.getJSONObject(i);
            int id = player.getInt("id");
            String headshot = player.getString("headshot");
            String firstName = player.getJSONObject("firstName").getString("default");
            String lastName = player.getJSONObject("lastName").getString("default");
            int sweaterNumber = player.getInt("sweaterNumber");
            String positionCode = player.getString("positionCode");
            String shootsCatches = player.getString("shootsCatches");
            int heightInInches = player.getInt("heightInInches");
            int weightInPounds = player.getInt("weightInPounds");
            int heightInCentimeters = player.getInt("heightInCentimeters");
            int weightInKilograms = player.getInt("weightInKilograms");
            String birthDate = player.getString("birthDate");
            String birthCity = player.getJSONObject("birthCity").getString("default");
            String birthCountry = player.getString("birthCountry");

            insertOrUpdatePlayer(id, headshot, firstName, lastName, sweaterNumber, positionCode, shootsCatches, heightInInches, weightInPounds, heightInCentimeters, weightInKilograms, birthDate, birthCity, birthCountry);

        }

        for(int i = 0; i < defensemen.length(); i++)   {
            JSONObject player = defensemen.getJSONObject(i);
            int id = player.getInt("id");
            String headshot = player.getString("headshot");
            String firstName = player.getJSONObject("firstName").getString("default");
            String lastName = player.getJSONObject("lastName").getString("default");
            int sweaterNumber = player.getInt("sweaterNumber");
            String positionCode = player.getString("positionCode");
            String shootsCatches = player.getString("shootsCatches");
            int heightInInches = player.getInt("heightInInches");
            int weightInPounds = player.getInt("weightInPounds");
            int heightInCentimeters = player.getInt("heightInCentimeters");
            int weightInKilograms = player.getInt("weightInKilograms");
            String birthDate = player.getString("birthDate");
            String birthCity = player.getJSONObject("birthCity").getString("default");
            String birthCountry = player.getString("birthCountry");

            insertOrUpdatePlayer(id, headshot, firstName, lastName, sweaterNumber, positionCode, shootsCatches, heightInInches, weightInPounds, heightInCentimeters, weightInKilograms, birthDate, birthCity, birthCountry);

        }

        for(int i = 0; i < goalies.length(); i++)   {
            JSONObject player = goalies.getJSONObject(i);
            int id = player.getInt("id");
            String headshot = player.getString("headshot");
            String firstName = player.getJSONObject("firstName").getString("default");
            String lastName = player.getJSONObject("lastName").getString("default");
            int sweaterNumber = player.getInt("sweaterNumber");
            String positionCode = player.getString("positionCode");
            String shootsCatches = player.getString("shootsCatches");
            int heightInInches = player.getInt("heightInInches");
            int weightInPounds = player.getInt("weightInPounds");
            int heightInCentimeters = player.getInt("heightInCentimeters");
            int weightInKilograms = player.getInt("weightInKilograms");
            String birthDate = player.getString("birthDate");
            String birthCity = player.getJSONObject("birthCity").getString("default");
            String birthCountry = player.getString("birthCountry");

            insertOrUpdatePlayer(id, headshot, firstName, lastName, sweaterNumber, positionCode, shootsCatches, heightInInches, weightInPounds, heightInCentimeters, weightInKilograms, birthDate, birthCity, birthCountry);

        }



    }

    private static void insertOrUpdatePlayer(int id, String headShot, String firstName, String lastName, int sweaterNumber, String positionCode, String shootsCatches,
                                             int heightInInches, int weightInPounds, int heightInCentimeters, int weightInKilograms, String birthDate, String birthCity, String birthCountry)   {

        String sql = "INSERT INTO players (id, headshot_url, first_name, last_name, sweater_number, position_code, shoots_catches, height_in_inches, weight_in_pounds, height_in_centimeters, weight_in_kilograms, birth_date, birth_city, birth_country, birth_state_province)" +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE id = id;";

        System.out.println("Inserting " + firstName + " " + lastName);

        try (Connection conn = Database.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.setString(2, headShot);
            pstmt.setString(3, firstName);
            pstmt.setString(4, lastName);
            pstmt.setInt(5, sweaterNumber);
            pstmt.setString(6, positionCode);
            pstmt.setString(7, shootsCatches);
            pstmt.setInt(8, heightInInches);
            pstmt.setInt(9, weightInPounds);
            pstmt.setInt(10, heightInCentimeters);
            pstmt.setInt(11, weightInKilograms);
            pstmt.setString(12, birthDate);
            pstmt.setString(13, birthCity);
            pstmt.setString(14, birthCountry);
            pstmt.setString(15, "NA");


            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }




    }




}
