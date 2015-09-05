package barqsoft.footballscores.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import barqsoft.footballscores.data.FootballScoresContract.ScoresTable;

/**
 * Created by yehya khaled on 2/25/2015.
 */
public class ScoresDBHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "Scores.db";
    private static final int DATABASE_VERSION = 2;

    public ScoresDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CreateScoresTable = "CREATE TABLE " + FootballScoresContract.SCORES_TABLE + " ("
                + FootballScoresContract.ScoresTable._ID + " INTEGER PRIMARY KEY,"
                + FootballScoresContract.ScoresTable.DATE_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresTable.TIME_COL + " INTEGER NOT NULL,"
                + FootballScoresContract.ScoresTable.HOME_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresTable.AWAY_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresTable.LEAGUE_COL + " INTEGER NOT NULL,"
                + FootballScoresContract.ScoresTable.HOME_GOALS_COL + " TEXT NOT NULL,"
                + FootballScoresContract.ScoresTable.AWAY_GOALS_COL + " TEXT NOT NULL,"
                + ScoresTable.MATCH_ID + " INTEGER NOT NULL,"
                + FootballScoresContract.ScoresTable.MATCH_DAY + " INTEGER NOT NULL,"
                + " UNIQUE (" + FootballScoresContract.ScoresTable.MATCH_ID + ") ON CONFLICT REPLACE"
                + " );";
        db.execSQL(CreateScoresTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //Remove old values when upgrading.
        db.execSQL("DROP TABLE IF EXISTS " + FootballScoresContract.SCORES_TABLE);
    }
}
