package barqsoft.footballscores.service;

import android.app.IntentService;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.Vector;

import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.R;

/**
 * Created by yehya khaled on 3/2/2015.
 */
public class FootballScoresIntentService extends IntentService {
    public static final String LOG_TAG = "ScoresIntentService";

    public FootballScoresIntentService() {
        super("FootballScoresIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // Get data for next two days and previous two days respectively
        getData("n2");
        getData("p2");

        return;
    }

    private void getData(String timeFrame) {
        final String BASE_URL = "http://api.football-data.org/alpha/fixtures";
        final String QUERY_TIME_FRAME = "timeFrame";

        Uri footballScoresURI = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(QUERY_TIME_FRAME, timeFrame).build();

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        String jsonData = null;

        //Opening Connection
        try {
            URL fetch = new URL(footballScoresURI.toString());
            connection = (HttpURLConnection) fetch.openConnection();
            connection.setRequestMethod("GET");
            connection.addRequestProperty("X-Auth-Token", getResources().getString(R.string.api_key));
            connection.connect();

            // Read the input stream into a String
            InputStream inputStream = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            jsonData = buffer.toString();
        } catch (Exception e) {
            /**
             * Notify user of connection issue
             * This is caused either by malformed URL or IOException
             */
            Log.e(LOG_TAG, "Exception here" + e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG_TAG, "Error Closing Stream");
                }
            }
        }
        try {
            if (jsonData != null) {
                // This bit is to check if the data contains any matches. If not, we call processJson on the dummy data
                JSONArray matches = new JSONObject(jsonData).getJSONArray("fixtures");
                if (matches.length() == 0) {
                    // if there is no data, call the function on dummy data
                    // this is expected behavior during the off season.
                    processJSONdata(getString(R.string.dummy_data), getApplicationContext(), false);
                    return;
                }


                processJSONdata(jsonData, getApplicationContext(), true);
            } else {
                //Could not Connect
                Log.d(LOG_TAG, "Could not connect to server.");
            }
        } catch (JSONException e) {
            /**
             * Should notify user of failure
             * Is caused from JSON exception
             */
            Log.e(LOG_TAG, e.getMessage());
        }
    }

    private void processJSONdata(String JSONdata, Context mContext, boolean isReal) {
        //JSON data
        final String BUNDESLIGA1 = "394";
        final String BUNDESLIGA2 = "395";
        final String LIGUE1 = "396";
        final String LIGUE2 = "397";
        final String PREMIER_LEAGUE = "398";
        final String PRIMERA_DIVISION = "399";
        final String SEGUNDA_DIVISION = "400";
        final String SERIE_A = "401";
        final String PRIMERA_LIGA = "402";
        final String Bundesliga3 = "403";
        final String EREDIVISIE = "404";
        final String SEASON_LINK = "http://api.football-data.org/alpha/soccerseasons/";
        final String MATCH_LINK = "http://api.football-data.org/alpha/fixtures/";
        final String FIXTURES = "fixtures";
        final String LINKS = "_links";
        final String SOCCER_SEASON = "soccerseason";
        final String SELF = "self";
        final String MATCH_DATE = "date";
        final String HOME_TEAM = "homeTeamName";
        final String AWAY_TEAM = "awayTeamName";
        final String RESULT = "result";
        final String HOME_GOALS = "goalsHomeTeam";
        final String AWAY_GOALS = "goalsAwayTeam";
        final String MATCH_DAY = "matchday";

        //Match data
        String league;
        String mDate;
        String mTime;
        String home;
        String away;
        String homeGoals;
        String awayGoals;
        String matchId;
        String matchDay;


        try {
            JSONArray matches = new JSONObject(JSONdata).getJSONArray(FIXTURES);
            Vector<ContentValues> values = new Vector<>(matches.length());

            for (int i = 0; i < matches.length(); i++) {

                JSONObject match_data = matches.getJSONObject(i);
                league = match_data.getJSONObject(LINKS).getJSONObject(SOCCER_SEASON).getString("href");
                league = league.replace(SEASON_LINK, "");

                if (league.equals(PREMIER_LEAGUE) ||
                        league.equals(SERIE_A) ||
                        league.equals(BUNDESLIGA1) ||
                        league.equals(BUNDESLIGA2) ||
                        league.equals(PRIMERA_DIVISION) ||
                        league.equals(LIGUE1) ||
                        league.equals(LIGUE2) ||
                        league.equals(SEGUNDA_DIVISION) ||
                        league.equals(PRIMERA_LIGA) ||
                        league.equals(Bundesliga3) ||
                        league.equals(EREDIVISIE)) {

                    matchId = match_data.getJSONObject(LINKS).getJSONObject(SELF).getString("href");
                    matchId = matchId.replace(MATCH_LINK, "");

                    if (!isReal) {
                        //This if statement changes the match ID of the dummy data so that it all goes into the database
                        matchId = matchId + Integer.toString(i);
                    }

                    mDate = match_data.getString(MATCH_DATE);
                    mTime = mDate.substring(mDate.indexOf("T") + 1, mDate.indexOf("Z"));
                    mDate = mDate.substring(0, mDate.indexOf("T"));
                    SimpleDateFormat matchDate = new SimpleDateFormat("yyyy-MM-ddHH:mm:ss");
                    matchDate.setTimeZone(TimeZone.getTimeZone("UTC"));

                    try {
                        Date parseDate = matchDate.parse(mDate + mTime);
                        SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd:HH:mm");
                        newDate.setTimeZone(TimeZone.getDefault());
                        mDate = newDate.format(parseDate);
                        mTime = mDate.substring(mDate.indexOf(":") + 1);
                        mDate = mDate.substring(0, mDate.indexOf(":"));

                        if (!isReal) {
                            //This if statement changes the dummy data's date to match our current date range.
                            Date fragmentDate = new Date(System.currentTimeMillis() + ((i - 2) * 86400000));
                            SimpleDateFormat mformat = new SimpleDateFormat("yyyy-MM-dd");
                            mDate = mformat.format(fragmentDate);
                        }

                    } catch (ParseException e) {
                        /**
                         * Date parsing exception
                         */
                        Log.d(LOG_TAG, "Error parsing date format");
                        Log.e(LOG_TAG, e.getMessage());
                    }

                    home = match_data.getString(HOME_TEAM);
                    away = match_data.getString(AWAY_TEAM);
                    homeGoals = match_data.getJSONObject(RESULT).getString(HOME_GOALS);
                    awayGoals = match_data.getJSONObject(RESULT).getString(AWAY_GOALS);
                    matchDay = match_data.getString(MATCH_DAY);

                    ContentValues matchValues = new ContentValues();
                    matchValues.put(FootballScoresContract.ScoresTable.MATCH_ID, matchId);
                    matchValues.put(FootballScoresContract.ScoresTable.DATE_COL, mDate);
                    matchValues.put(FootballScoresContract.ScoresTable.TIME_COL, mTime);
                    matchValues.put(FootballScoresContract.ScoresTable.HOME_COL, home);
                    matchValues.put(FootballScoresContract.ScoresTable.AWAY_COL, away);
                    matchValues.put(FootballScoresContract.ScoresTable.HOME_GOALS_COL, homeGoals);
                    matchValues.put(FootballScoresContract.ScoresTable.AWAY_GOALS_COL, awayGoals);
                    matchValues.put(FootballScoresContract.ScoresTable.LEAGUE_COL, league);
                    matchValues.put(FootballScoresContract.ScoresTable.MATCH_DAY, matchDay);

                    Log.v(LOG_TAG,matchId);
                    Log.v(LOG_TAG,mDate);
                    Log.v(LOG_TAG,mTime);
                    Log.v(LOG_TAG,home);
                    Log.v(LOG_TAG,away);
                    Log.v(LOG_TAG,homeGoals);
                    Log.v(LOG_TAG,awayGoals);

                    values.add(matchValues);
                }
            }
            ContentValues[] insertData = new ContentValues[values.size()];
            values.toArray(insertData);
            int numberOfRecordsInserted = mContext.getContentResolver()
                    .bulkInsert(FootballScoresContract.BASE_CONTENT_URI, insertData);

            Log.v(LOG_TAG,"Successfully Inserted : " + String.valueOf(numberOfRecordsInserted));
        } catch (JSONException e) {
            /**
             * Problem parsing json data
             */
            Log.e(LOG_TAG, e.getMessage());
        }

    }
}

