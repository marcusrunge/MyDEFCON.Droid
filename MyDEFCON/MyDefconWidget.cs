using Android.App;
using Android.Content;
using Android.Widget;
using Android.Appwidget;
using MyDEFCON.Services;
using Android.Graphics;
using Android.OS;
using System;

namespace MyDEFCON
{
    [BroadcastReceiver(Enabled = true, Label = "MyDEFCON")]
    [IntentFilter(new string[] { "android.appwidget.action.APPWIDGET_UPDATE", "android.appwidget.action.ACTION_APPWIDGET_OPTIONS_CHANGED", "com.marcusrunge.MyDEFCON.DEFCON_UPDATE"})]
    [MetaData("android.appwidget.provider", Resource = "@xml/mydefconwidgetprovider")]

    public class MyDefconWidget : AppWidgetProvider
    {

        public override void OnUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds)
        {
            base.OnUpdate(context, appWidgetManager, appWidgetIds);
            var defconStatus = GetApplicationDefconStatus().ToString();
            for (int i = 0; i < appWidgetIds.Length; i++)
            {
                int appWidgetId = appWidgetIds[i];
                Intent intent = new Intent(context, typeof(MainActivity));
                PendingIntent pendingIntent = PendingIntent.GetActivity(context, 0, intent, 0);
                RemoteViews remoteViews = new RemoteViews(context.PackageName, Resource.Layout.mydefcon_widget);
                remoteViews.SetTextViewText(Resource.Id.mydefconWidgetTextView, defconStatus);
                remoteViews.SetTextColor(Resource.Id.mydefconWidgetTextView, GetLightColor(defconStatus));
                remoteViews.SetInt(Resource.Id.mydefconWidgetLinearLayout, "setBackgroundColor", GetDarkColor(defconStatus));
                remoteViews.SetInt(Resource.Id.mydefconFrameLayout, "setBackgroundColor", GetLightColor(defconStatus));
                remoteViews.SetOnClickPendingIntent(Resource.Id.mydefconWidgetLinearLayout, pendingIntent);
                appWidgetManager.UpdateAppWidget(appWidgetId, remoteViews);
            }
        }
        public override void OnAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions)
        {
            var defconStatus = GetApplicationDefconStatus().ToString();
            //base.OnAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
            var widgetHeight = newOptions.GetInt(AppWidgetManager.OptionAppwidgetMinHeight);
            Intent intent = new Intent(context, typeof(MainActivity));
            PendingIntent pendingIntent = PendingIntent.GetActivity(context, 0, intent, 0);
            ComponentName componentName = new ComponentName(context, Java.Lang.Class.FromType(typeof(MyDefconWidget)).Name);
            RemoteViews remoteViews = new RemoteViews(context.PackageName, Resource.Layout.mydefcon_widget);
            remoteViews.SetTextViewTextSize(Resource.Id.mydefconWidgetTextView, 2, widgetHeight * (float)0.5);
            remoteViews.SetTextViewText(Resource.Id.mydefconWidgetTextView, defconStatus);
            remoteViews.SetTextColor(Resource.Id.mydefconWidgetTextView, GetLightColor(defconStatus));
            remoteViews.SetInt(Resource.Id.mydefconWidgetLinearLayout, "setBackgroundColor", GetDarkColor(defconStatus));
            remoteViews.SetInt(Resource.Id.mydefconFrameLayout, "setBackgroundColor", GetLightColor(defconStatus));
            remoteViews.SetOnClickPendingIntent(Resource.Id.mydefconWidgetLinearLayout, pendingIntent);
            appWidgetManager.UpdateAppWidget(componentName, remoteViews);
        }
        private Color GetLightColor(string defconStatus)
        {
            switch (int.Parse(defconStatus))
            {
                case 1: return Color.ParseColor("#FFFFFFFF");
                case 2: return Color.ParseColor("#FFFF7100");
                case 3: return Color.ParseColor("#FFFFFF00");
                case 4: return Color.ParseColor("#FF00F200");
                default: return Color.ParseColor("#FF0066FF");
            }
        }

        private Color GetDarkColor(string defconStatus)
        {
            switch (int.Parse(defconStatus))
            {
                case 1: return Color.ParseColor("#FF404040");
                case 2: return Color.ParseColor("#FF400C00");
                case 3: return Color.ParseColor("#FF404000");
                case 4: return Color.ParseColor("#FF003500");
                default: return Color.ParseColor("#FF002340");
            }
        }

        public override void OnReceive(Context context, Intent intent)
        {
            base.OnReceive(context, intent);
            //Toast.MakeText(context, intent.Action + " in widged received...", ToastLength.Short).Show();
            if (intent.Action.Equals("com.marcusrunge.MyDEFCON.DEFCON_UPDATE"))
            {
                var defconStatus = intent.GetStringExtra("DefconStatus");
                if (defconStatus != null && !defconStatus.Equals("0"))
                {
                    Intent mainActivityIntent = new Intent(context, typeof(MainActivity));
                    PendingIntent pendingIntent = PendingIntent.GetActivity(context, 0, mainActivityIntent, 0);
                    ComponentName componentName = new ComponentName(context, Java.Lang.Class.FromType(typeof(MyDefconWidget)).Name);
                    AppWidgetManager appWidgetManager = AppWidgetManager.GetInstance(context);
                    RemoteViews remoteViews = new RemoteViews(context.PackageName, Resource.Layout.mydefcon_widget);
                    remoteViews.SetTextViewText(Resource.Id.mydefconWidgetTextView, defconStatus);
                    remoteViews.SetTextColor(Resource.Id.mydefconWidgetTextView, GetLightColor(defconStatus));
                    remoteViews.SetInt(Resource.Id.mydefconWidgetLinearLayout, "setBackgroundColor", GetDarkColor(defconStatus));
                    remoteViews.SetInt(Resource.Id.mydefconFrameLayout, "setBackgroundColor", GetLightColor(defconStatus));
                    remoteViews.SetOnClickPendingIntent(Resource.Id.mydefconWidgetLinearLayout, pendingIntent);
                    appWidgetManager.UpdateAppWidget(componentName, remoteViews);
                }
            }
        }

        public override void OnEnabled(Context context)
        {
            base.OnEnabled(context);            
        }

        private int GetApplicationDefconStatus()
        {
            int defconStatus = 5;
            var returnedDefconStatus = new SettingsService().GetSetting<string>("DefconStatus");
            if (!String.IsNullOrEmpty(returnedDefconStatus)) return int.Parse(returnedDefconStatus);
            return defconStatus;
        }
    }
}