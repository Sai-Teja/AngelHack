package angel.angelhack;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
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
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    String key = "b661aa39-4ce2-4fba-9087-2a9fde68c8b1";
    String url = "https://api.havenondemand.com/1/api/sync/analyzesentiment/v1";
    Double finalScore = 0d;
    TextView textView;
    private String bearerToken = null;
    ProgressBar progressBar;
    private List<Thread> threads = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = (ProgressBar) findViewById(R.id.progressBar);

        textView=(TextView)findViewById(R.id.textView) ;
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

            String body = "grant_type=client_credentials";

            connection.setRequestProperty("User-Agent", "Artblot");
            connection.setRequestProperty("Authorization", "Basic " + authorizationHeader);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded;charset=UTF-8");
            connection.setRequestProperty("Content-Length", Integer.toString(body.getBytes().length));

            connection.setDoOutput(true);
            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.writeBytes(body);
            wr.flush();
            wr.close();

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

                bearerToken = response.toString();
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

    public String getSearchTweets(String bearerToken, String value) {
            HttpURLConnection connection = null;
            StringBuilder response = new StringBuilder();
            try {
                URL url = new URL("https://api.twitter.com/1.1/search/tweets.json?q=" + Uri.encode(value) + "&result_type=recent&count=100");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = in.readLine();
                    while (line != null) {
                        response.append(line);
                        line = in.readLine();
                    }

                    JSONObject jsonObject = new JSONObject(response.toString());
                    JSONArray results = jsonObject.getJSONArray("statuses");
                    JSONObject object;
                    ArrayList<String> strings = new ArrayList<>();
                    int arrayLength = results.length();
                    for (int i = 0; i < arrayLength; i++) {
                        object = results.getJSONObject(i);
                        strings.add(object.getString("text"));
                        if (i == arrayLength - 1) {
                            String[] myArray = strings.toArray(new String[strings.size()]);
                            sendGetRequest(myArray, arrayLength, true);
                            strings.clear();
                        } else {
                            if (strings.size() == 10) {
                                String[] myArray = strings.toArray(new String[strings.size()]);
                                sendGetRequest(myArray, arrayLength, false);
                                strings.clear();
                            }
                        }
                    }
                    Log.i("TimePass", String.valueOf(response.toString()));
                    in.close();
                } else {
                    System.out.println(connection.getResponseMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            } finally {
                if (connection != null) {
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
        ComponentName cn = new ComponentName(this, MainActivity.class);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(cn));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String tweet) {
                if (progressBar.getVisibility() != View.VISIBLE) {
                    progressBar.setVisibility(View.VISIBLE);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            if (bearerToken == null) {
                                String authorizationHeader = getAuthorizationHeader("FOQIQnJENhEgjYXYaGSIhhSaA", "9B2nTfoFFPeysRhegLm9Yra4r5psBBOAPMqOtqq7T2U27Cxqwr");
                                bearerToken = postOAuth2Token(authorizationHeader);
                            }
                            finalScore = 0d;
                            threads.clear();
                            getSearchTweets(bearerToken, tweet);

                        }
                    }).start();
                } else {
                    Toast.makeText(MainActivity.this, "Another Search in Progress...", Toast.LENGTH_SHORT).show();
                }
                View view = MainActivity.this.getCurrentFocus();
                if (view != null) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                return true;
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

    @Override
    public void setIntent(Intent newIntent) {

    }
//
//    class Work extends AsyncTask<String, Void, Void> {
//
//        private int arrayLength;
//        private boolean lastTweet;
//
//        public Work(int arrayLength, boolean lastTweet) {
//            this.arrayLength = arrayLength;
//            this.lastTweet = lastTweet;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            super.onPreExecute();
//        }
//
//        @Override
//        protected Void doInBackground(String... params) {
//            sendGetRequest(params[0], arrayLength, lastTweet);
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(Void aVoid) {
//            super.onPostExecute(aVoid);
//            if (lastTweet) {
//                Log.i("FinalScore", String.valueOf(finalScore / arrayLength));
//                float ans= (float) ((finalScore / arrayLength) * 100);
//                DecimalFormat df = new DecimalFormat("#.####");
//                textView.setText("Final score : " + df.format(ans) + "%");
//            }
//        }
//    }

    private void sendGetRequest(final String tweets[], final int arrayLength, final boolean lastTweet) {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (String text : tweets) {
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
                            finalScore += res.score;
                        } else {
                            System.out.println(connection.getResponseCode());
                        }
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
                if (lastTweet) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.i("FinalScore", String.valueOf(finalScore / arrayLength));
                            float ans = (float) ((finalScore / arrayLength) * 100);
                            DecimalFormat df = new DecimalFormat("#.####");
                            textView.setText("Final score : " + df.format(ans) + "%");
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }
        });
        threads.add(thread);
        thread.start();
    }
}
