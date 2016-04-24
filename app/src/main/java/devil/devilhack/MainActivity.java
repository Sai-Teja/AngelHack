package devil.devilhack;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.gson.Gson;

import org.apache.commons.lang3.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    String key = "b661aa39-4ce2-4fba-9087-2a9fde68c8b1";
    String url = "https://api.havenondemand.com/1/api/sync/analyzesentiment/v1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        sendTweetRequest("#MakeDonaldDrumpfagain");// This is for Twitter. It includes a POST and a GET example.

        new Thread(new Runnable() {
            @Override
            public void run() {
                // I need to sign up to Twitter and get an API key and secret from their developer console.
                // Then I need to pass it as base64("Consumer Key"+":"+"Consumer Secret")
                String authorizationHeader = getAuthorizationHeader("FOQIQnJENhEgjYXYaGSIhhSaA", "9B2nTfoFFPeysRhegLm9Yra4r5psBBOAPMqOtqq7T2U27Cxqwr");

                // I open an HTTPS connection and POST it to get an OAuth 2 bearer token from them required to actually *use* their API.
                String bearerToken = postOAuth2Token(authorizationHeader);

                // With the bearer token, I am able to make any "application authentication" call to their API. This is as opposed to "user context"
                // authentication, which I would require to do anything *as* an actual user. For example, I would need user context authentication
                // to make a Tweet via a "POST statuses/update" call.

                // Let's use GET search/tweets to see if anyone has tweeted about "cats"
                String jsonTweets = getSearchTweets(bearerToken, "#MakeDonaldDrumpfagain");
                System.out.println(jsonTweets);
            }
        }).start();
    }

    public static String getAuthorizationHeader(String consumerKey, String consumerSecret) {
        return Base64.encodeToString((consumerKey + ":" + consumerSecret).getBytes(), Base64.NO_WRAP);
    }

    public static String postOAuth2Token(String authorizationHeader)
    {
        HttpURLConnection connection = null;
        String bearerToken = null;

        try {
            URL url = new URL("https://api.twitter.com/oauth2/token");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("POST");

            // Form the body of the request
            String body = "grant_type=client_credentials";

            // Set required headers.
            connection.setRequestProperty("User-Agent", "Artblot");
            connection.setRequestProperty("Authorization", "Basic " + authorizationHeader);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));

            // Set parameters required in the body of the POST request.
            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();

            // Send the request.
            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line = in.readLine();
                while(line != null)
                {
                    response.append(line);
                    line = in.readLine();
                }
                in.close();

                // This is the JSON string coming back. You would pass this into some kind of a JSON parser,
                // which would do something nicer.
                bearerToken = response.toString();
                // For this example, I'm not really parsing the JSON correctly. I'm just getting the access token out using raw string methods.
                bearerToken = bearerToken.substring(bearerToken.indexOf("\"access_token\":\"")+"\"access_token\":\"".length(), bearerToken.lastIndexOf("\"}"));
            }
            else {
                System.out.println(connection.getResponseMessage());
            }
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return bearerToken;
    }

    public String getSearchTweets(String bearerToken, String value)
    {
        HttpURLConnection connection = null;
        StringBuilder response = new StringBuilder();
        try {
            URL url = new URL("https://api.twitter.com/1.1/search/tweets.json?q=" + Uri.encode(value) + "&count=100");
            connection = (HttpURLConnection)url.openConnection();
            connection.setRequestMethod("GET");

            // Set required headers.
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String line = in.readLine();
                while(line != null)
                {
                    response.append(line);
                    line = in.readLine();
                }

                JSONObject jsonObject = new JSONObject(response.toString());
                JSONArray results = jsonObject.getJSONArray("statuses");
                JSONObject object;
                for (int i = 0; i < 100; i++) {
                    object = results.getJSONObject(i);
                    sendGetRequest(object.getString("text"));
                }
                Log.i("TimePass", String.valueOf(response.toString()));
                in.close();
            }
            else {
                System.out.println(connection.getResponseMessage());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            if(connection != null) {
                connection.disconnect();
            }
        }
        return response.toString();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        final SearchView searchView = (SearchView) searchItem.getActionView();
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        // Assumes current activity is the searchable activity
        ComponentName cn = new ComponentName(this, SearchableActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });

        MenuItemCompat.setOnActionExpandListener(searchItem, new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Do something when collapsed
                return true;  // Return true to collapse action view
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                // Do something when expanded
                return true;  // Return true to expand action view
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    private void sendGetRequest(final String text) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL sent = new URL(url);
                    HttpURLConnection connection = (HttpURLConnection) sent.openConnection();
                    connection.setRequestMethod("POST");

                    Uri.Builder builder = new Uri.Builder()
                            .appendQueryParameter("apikey", key)
                            .appendQueryParameter("text", text);
                    String query = builder.build().getEncodedQuery();

                    OutputStream os = connection.getOutputStream();
                    BufferedWriter writer = new BufferedWriter(
                            new OutputStreamWriter(os, "UTF-8"));
                    writer.write(query);
                    writer.flush();
                    try {
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    os.close();
                    connection.connect();
                    if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        BufferedReader reader1 = new BufferedReader(
                                new InputStreamReader(connection.getInputStream()));

                        StringBuilder result1 = new StringBuilder();
                        String line1;
                        while ((line1 = reader1.readLine()) != null) {
                            result1.append(line1);
                        }
                        JSONObject jsonObject = new JSONObject(result1.toString());
                        JSONObject aggregate = jsonObject.getJSONObject("aggregate");
                        Gson gson = new Gson();
                        Log.i("TimePass", "Tweet: " + text + "Result: " + aggregate.toString() + "\n");
                        GsonResult res = gson.fromJson(aggregate.toString(), GsonResult.class);
                    } else {
                        System.out.println(connection.getResponseCode());
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }
}
