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

@AndroidEntryPoint
class MyDefconWidget : AppWidgetProvider() {
    @Inject
    lateinit var core: Core
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
    }

    override fun onDisabled(context: Context) {
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        if (intent?.action == "com.marcusrunge.mydefcon.DEFCON_UPDATE") {
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val componentName =
                context?.packageName?.let { ComponentName(it, MyDefconWidget::class.java.name) }
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            context?.let { onUpdate(it, appWidgetManager, appWidgetIds) }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.mydefcon_widget)
        val mainActivity = Intent(context, SplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainActivity,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.apply {
            views.setTextViewText(R.id.appwidget_text, core.preferences.status.toString())
            when (core.preferences.status) {
                1 -> {
                    views.setTextColor(R.id.appwidget_text, context.getColor(R.color.grey_700))
                    views.setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon1_shape
                    )
                }
                2 -> {
                    views.setTextColor(R.id.appwidget_text, context.getColor(R.color.red_900))
                    views.setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon2_shape
                    )
                }
                3 -> {
                    views.setTextColor(R.id.appwidget_text, context.getColor(R.color.yellow_A200V4))
                    views.setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon3_shape
                    )
                }
                4 -> {
                    views.setTextColor(R.id.appwidget_text, context.getColor(R.color.green_800))
                    views.setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon4_shape
                    )
                }
                5 -> {
                    views.setTextColor(R.id.appwidget_text, context.getColor(R.color.blue_800))
                    views.setInt(
                        R.id.appwidget_text,
                        "setBackgroundResource",
                        R.drawable.app_widget_defcon5_shape
                    )
                }
            }
            setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

