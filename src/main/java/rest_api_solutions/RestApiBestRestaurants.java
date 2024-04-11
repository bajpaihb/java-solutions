package rest_api_solutions;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.stream.Stream;

public class RestApiBestRestaurants {
    private static String restUrl = "https://jsonmock.hackerrank.com/api/food_outlets";

    public static String getBestRestaurant(String city, int max_cost) throws IOException, ParseException {
        String best_restaurant = "";
        int page = 1;
            String sb_response = getResponsePerPage(city, page);

            JsonObject obj = new Gson().fromJson(sb_response, JsonObject.class);
            int total_pages = obj.get("total_pages").getAsInt();
            JsonArray data = obj.getAsJsonArray("data");

            Map<String, Integer> restaurant_costs = new HashMap<String, Integer>();
            int max_rating = 0;
            for (JsonElement e : data) {
                int cost = e.getAsJsonObject().get("estimated_cost").getAsInt();
                int user_rating_from_json = e.getAsJsonObject().getAsJsonObject("user_rating").get("average_rating").getAsInt();
                String name = e.getAsJsonObject().get("name").getAsString();
                if (cost < max_cost) {
                    if (user_rating_from_json > max_rating) {
                        max_rating = user_rating_from_json;
                        restaurant_costs.put(name, cost);
                    } else if (user_rating_from_json == max_rating) {
                        restaurant_costs.put(name, cost);
                    }
                }

                Stream<Map.Entry<String, Integer>> sorted = restaurant_costs.entrySet().stream().sorted(Map.Entry.comparingByValue());
                best_restaurant = sorted.findFirst().get().getKey();
            }

            return total_pages==page? best_restaurant : getResponsePerPage(city, page+1);
    }


        private static String getResponsePerPage (String city,int page) throws IOException {
            String newUrl = restUrl + "?city=" + city + "&page=" + page;
            System.out.println("New URL : " + newUrl);

            URL url = new URL(newUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // checking the response code
            int responseCode = connection.getResponseCode();
            if (responseCode != 200) {
                throw new IOException("Connection could not be established. HTTP response code: " + responseCode);
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String response;
            StringBuilder sb = new StringBuilder();
            while ((response = br.readLine()) != null) {
                sb.append(response);
            }

            br.close();
            connection.disconnect();
            return sb.toString();
        }
    }


