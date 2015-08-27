package barqsoft.footballscores.widget;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;

import java.net.URI;

import barqsoft.footballscores.data.FootballScoresContract;

/**
 * Created by Manuel Sanchez on 8/17/15
 */
public class FootballScoresWidgetIntentService extends IntentService {

    private static final String[] FOOTBALL_SCORES_COLUMNS = {
            FootballScoresContract.scores_table.MATCH_ID,
            FootballScoresContract.scores_table.DATE_COL,
            FootballScoresContract.scores_table.TIME_COL,
            FootballScoresContract.scores_table.HOME_COL,
            FootballScoresContract.scores_table.AWAY_COL,
            FootballScoresContract.scores_table.HOME_GOALS_COL,
            FootballScoresContract.scores_table.AWAY_GOALS_COL,
            FootballScoresContract.scores_table.LEAGUE_COL,
            FootballScoresContract.scores_table.MATCH_DAY
    };

    private static final int MATCH_ID = 0;
    private static final int DATE_COL = 1;
    private static final int TIME_COL =2;
    private static final int HOME_COL =3;
    private static final int AWAY_COL =4;
    private static final int HOME_GOALS_COL = 5;
    private static final int AWAY_GOALS_COL = 6;
    private static final int LEAGUE_COL = 7;
    private static final int MATCH_DAY =8;


    public FootballScoresWidgetIntentService() {
        super("FootballScoresWidgetIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
//        Uri uri = FootballScoresContract.scores_table.buildScoreWithDate();
        Uri uri = FootballScoresContract.scores_table.buildScoreWithLeague();
//        Uri uri = FootballScoresContract.scores_table.buildScoreWithId();
        getContentResolver().query(uri, FOOTBALL_SCORES_COLUMNS, null, null, null);
    }
}
