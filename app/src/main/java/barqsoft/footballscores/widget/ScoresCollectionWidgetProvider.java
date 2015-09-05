package barqsoft.footballscores.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import barqsoft.footballscores.MainActivity;
import barqsoft.footballscores.R;

/**
 * Created by Manuel Sanchez on 8/30/15
 */
public class ScoresCollectionWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "position";

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(context, getClass()));
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_scores_list_view);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {

        // update widgets
        for (int id : appWidgetIds) {
            // Instantiate the RemoteViews object for the app widget layout.
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_football_scores_collection);

            Intent intent = new Intent(context, ScoresWidgetRemoteViewsService.class);
            // Add the app widget ID to the intent extras.
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
            intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));

            // Set up the RemoteViews object to use a RemoteViews adapter.
            // This adapter connects
            // to a RemoteViewsService  through the specified intent.
            // This is how you populate the data.
            views.setRemoteAdapter(R.id.widget_scores_list_view, intent);

            // The empty view is displayed when the collection has no items.
            // It should be in the same layout used to instantiate the RemoteViews
            // object above.
            views.setEmptyView(R.id.widget_scores_list_view, R.id.widget_empty_view);


            // Set pending intent template to launch app when match is selected
            Intent launchIntent = new Intent(context, MainActivity.class);
            launchIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, launchIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            views.setPendingIntentTemplate(R.id.widget_scores_list_view, pendingIntent);

            appWidgetManager.updateAppWidget(id, views);
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds);

    }

}
