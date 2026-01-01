package com.marcusrunge.mydefcon

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.marcusrunge.mydefcon.core.interfaces.Core
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Implementation of the App Widget functionality.
 * This class is responsible for updating the app widget on the home screen.
 */
@AndroidEntryPoint
class MyDefconWidget : AppWidgetProvider() {
    @Inject
    lateinit var core: Core

    /**
     * Called in response to the AppWidgetManager.ACTION_APPWIDGET_UPDATE broadcast when this
     * widget is added to a host, as well as periodically.
     *
     * @param context The Context in which this receiver is running.
     * @param appWidgetManager A AppWidgetManager object you can use to update the AppWidget.
     * @param appWidgetIds The appWidgetIds for which an update is needed.
     */
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    /**
     * Called when the first instance of the widget is created.
     * @param context The Context in which this receiver is running.
     */
    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    /**
     * Called when the last instance of the widget is disabled.
     * @param context The Context in which this receiver is running.
     */
    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    /**
     * This is called for every broadcast and before each of the other callback methods.
     * It receives all intents, including those for widget updates.
     *
     * @param context The Context in which this receiver is running.
     * @param intent The Intent being received.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        // Check for the custom DEFCON update action
        if (intent?.action == "com.marcusrunge.mydefcon.DEFCON_UPDATE") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName =
                context?.packageName?.let { ComponentName(it, MyDefconWidget::class.java.name) }
            // Get all IDs for this widget provider
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            // Trigger an update for all widgets
            context?.let { onUpdate(it, appWidgetManager, appWidgetIds) }
        }
    }

    /**
     * Updates a single app widget instance.
     *
     * @param context The context.
     * @param appWidgetManager The widget manager.
     * @param appWidgetId The ID of the widget to update.
     */
    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // Construct the RemoteViews object
        val views = RemoteViews(context.packageName, R.layout.mydefcon_widget)

        // Create an Intent to launch SplashActivity when the widget is clicked
        val mainActivity = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainActivity,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Update the widget's views
        views.apply {
            // Set the DEFCON status text
            setTextViewText(R.id.appwidget_text, core.preferences?.status.toString())

            // Update the background and text color based on the current DEFCON status
            when (core.preferences?.status) {
                1 -> {
                    setTextColor(R.id.appwidget_text, context.getColor(R.color.grey_700))
                    setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon1_shape
                    )
                }

                2 -> {
                    setTextColor(R.id.appwidget_text, context.getColor(R.color.red_900))
                    setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon2_shape
                    )
                }

                3 -> {
                    setTextColor(R.id.appwidget_text, context.getColor(R.color.yellow_A200V4))
                    setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon3_shape
                    )
                }

                4 -> {
                    setTextColor(R.id.appwidget_text, context.getColor(R.color.green_800))
                    setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon4_shape
                    )
                }

                5 -> {
                    setTextColor(R.id.appwidget_text, context.getColor(R.color.blue_800))
                    setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon5_shape
                    )
                }
            }
            // Set the click listener for the entire widget
            setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}
