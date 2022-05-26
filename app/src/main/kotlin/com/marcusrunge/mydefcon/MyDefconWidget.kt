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
            val componentName = context?.packageName?.let { ComponentName(it, MyDefconWidget::class.java.name) }
            val appWidgetIds = appWidgetManager.getAppWidgetIds(componentName)
            context?.let { onUpdate(it, appWidgetManager, appWidgetIds) }
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.my_defcon_widget)
        val mainActivity = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            mainActivity,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        views.apply {
            views.setTextViewText(R.id.appwidget_text, core.preferences.status.toString())
            setOnClickPendingIntent(R.id.appwidget_root, pendingIntent)
        }
        appWidgetManager.updateAppWidget(appWidgetId, views)
    }
}

