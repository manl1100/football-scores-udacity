package barqsoft.footballscores;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import barqsoft.footballscores.data.FootballScoresContract;
import barqsoft.footballscores.service.FootballScoresIntentService;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainScreenFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public ScoresAdapter mAdapter;
    public static final int SCORES_LOADER = 0;
    private String[] fragmentDate = new String[1];

    public MainScreenFragment() {
    }

    private void updateScores() {
        Intent serviceStart = new Intent(getActivity(), FootballScoresIntentService.class);
        getActivity().startService(serviceStart);
    }

    public void setFragmentDate(String date) {
        fragmentDate[0] = date;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, final Bundle savedInstanceState) {
        updateScores();
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        View emptyView = rootView.findViewById(R.id.empty_view);

        mAdapter = new ScoresAdapter(getActivity(), null, 0);
        final ListView scoreList = (ListView) rootView.findViewById(R.id.scores_list);
        scoreList.setEmptyView(emptyView);
        scoreList.setAdapter(mAdapter);

        getLoaderManager().initLoader(SCORES_LOADER, null, this);

        mAdapter.detail_match_id = MainActivity.selectedMatchId;

        scoreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ViewHolder selected = (ViewHolder) view.getTag();
                mAdapter.detail_match_id = selected.match_id;
                MainActivity.selectedMatchId = (int) selected.match_id;
                mAdapter.notifyDataSetChanged();
            }
        });
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        return new CursorLoader(getActivity(), FootballScoresContract.ScoresTable.buildScoreWithDate(),
                null, null, fragmentDate, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            cursor.moveToNext();
        }
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mAdapter.swapCursor(null);
    }


}
