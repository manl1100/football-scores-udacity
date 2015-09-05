package barqsoft.footballscores.widget;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import java.text.SimpleDateFormat;
import java.util.Date;

import barqsoft.footballscores.R;
import barqsoft.footballscores.Utility;
import barqsoft.footballscores.data.FootballScoresContract;

/**
 * Created by Manuel Sanchez on 9/4/15
 */
public class ScoresListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

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
    private static final int TIME_COL = 2;
    private static final int HOME_COL = 3;
    private static final int AWAY_COL = 4;
    private static final int HOME_GOALS_COL = 5;
    private static final int AWAY_GOALS_COL = 6;
    private static final int LEAGUE_COL = 7;
    private static final int MATCH_DAY = 8;

    private Cursor data = null;
    private Context mContext;

    public ScoresListRemoteViewsFactory(Context mContext, Intent intent) {
        this.mContext = mContext;
    }

    @Override
    public void onCreate() {

    }

    @Override
    public void onDataSetChanged() {
        if (data != null) {
            data.close();
        }

        final long identityToken = Binder.clearCallingIdentity();
        Uri uri = FootballScoresContract.scores_table.buildScoreWithDate();
        data = mContext.getContentResolver().query(uri,
                FOOTBALL_SCORES_COLUMNS,
                null,
                new String[]{new SimpleDateFormat("yyyy-MM-dd").format(new Date(System.currentTimeMillis()))},
                FootballScoresContract.scores_table.HOME_GOALS_COL + " ASC");

        Binder.restoreCallingIdentity(identityToken);
    }

    @Override
    public void onDestroy() {
        if (data != null) {
            data.close();
            data = null;
        }
    }

    @Override
    public int getCount() {
        return data == null ? 0 : data.getCount();
    }

    @Override
    public RemoteViews getViewAt(int position) {

        if (position == AdapterView.INVALID_POSITION ||
                data == null || !data.moveToPosition(position)) {
            return null;
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_football_score);

        String homeTeam = data.getString(HOME_COL);
        int homeGoals = data.getInt(HOME_GOALS_COL);
        String awayTeam = data.getString(AWAY_COL);
        int awayGoals = data.getInt(AWAY_GOALS_COL);
        String matchTime = data.getString(TIME_COL);


        // Add the data to the RemoteViews
        views.setTextViewText(R.id.home_team, homeTeam);
        views.setTextViewText(R.id.away_team, awayTeam);
        views.setTextViewText(R.id.match_score, Utility.getScores(homeGoals, awayGoals));

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return new RemoteViews(mContext.getPackageName(), R.layout.widget_football_score);
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int position) {
        if (data.moveToPosition(position))
            return data.getLong(MATCH_ID);
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
