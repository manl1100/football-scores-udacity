package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;
import barqsoft.footballscores.service.FootballScoresIntentService;

/**
 * Created by Manuel Sanchez on 8/17/15
 */
public class FootballScoresWidgetProvider extends AppWidgetProvider {

    public static final String ACTION_DATA_UPDATED = "action_data_updated";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (ACTION_DATA_UPDATED.equals(intent.getAction())) {
            context.startService(new Intent(context, FootballScoresIntentService.class));
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        context.startService(new Intent(context, FootballScoresIntentService.class));
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        context.startService(new Intent(context, FootballScoresIntentService.class));

//        // update widgets
//        for (int id : appWidgetIds) {
//            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_football_score);
//
//            // set pending intent
//            Intent intent = new Intent(context, MainActivity.class);
//            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
//            views.setOnClickPendingIntent(R.id.widget, pendingIntent);
//
//            appWidgetManager.updateAppWidget(id, views);
//        }
    }
}
