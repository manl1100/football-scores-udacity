package barqsoft.footballscores;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by yehya khaled on 2/26/2015.
 */
public class ViewHolder {

    public TextView homeTeamName;
    public TextView awayTeamName;
    public TextView score;
    public TextView date;
    public ImageView homeCrest;
    public ImageView awayCrest;
    public double matchId;

    public ViewHolder(View view) {
        homeTeamName = (TextView) view.findViewById(R.id.home_name);
        awayTeamName = (TextView) view.findViewById(R.id.away_name);
        score = (TextView) view.findViewById(R.id.score_textview);
        date = (TextView) view.findViewById(R.id.data_textview);
        homeCrest = (ImageView) view.findViewById(R.id.home_crest);
        awayCrest = (ImageView) view.findViewById(R.id.away_crest);
    }
}
