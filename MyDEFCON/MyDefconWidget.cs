using Android.App;
using Android.Appwidget;
using Android.Content;
using Android.Graphics;
using Android.OS;
using Android.Widget;
using MyDEFCON.Services;

namespace MyDEFCON
{
    [BroadcastReceiver/*(Enabled = true, Label = "MyDEFCON")*/]
    [IntentFilter(new string[] { "android.appwidget.action.APPWIDGET_UPDATE", "android.appwidget.action.ACTION_APPWIDGET_OPTIONS_CHANGED", "com.marcusrunge.MyDEFCON.DEFCON_UPDATE", "android.intent.action.APPLICATION_RESTRICTIONS_CHANGED" })]
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
            return int.Parse(defconStatus) switch
            {
                1 => Color.ParseColor("#FFFFFFFF"),
                2 => Color.ParseColor("#FFFF7100"),
                3 => Color.ParseColor("#FFFFFF00"),
                4 => Color.ParseColor("#FF00F200"),
                _ => Color.ParseColor("#FF0066FF"),
            };
        }

        private Color GetDarkColor(string defconStatus)
        {
            return int.Parse(defconStatus) switch
            {
                1 => Color.ParseColor("#FF404040"),
                2 => Color.ParseColor("#FF400C00"),
                3 => Color.ParseColor("#FF404000"),
                4 => Color.ParseColor("#FF003500"),
                _ => Color.ParseColor("#FF002340"),
            };
        }

        public override void OnReceive(Context context, Intent intent)
        {
            base.OnReceive(context, intent);
            //Toast.MakeText(context, intent.Action + " in widged received...", ToastLength.Short).Show();
            string defconStatus = null;
            if (intent.Action.Equals("com.marcusrunge.MyDEFCON.DEFCON_UPDATE"))
            {
                defconStatus = intent.GetStringExtra("DefconStatus");
            }
            else if (intent.Action.Equals("android.intent.action.APPLICATION_RESTRICTIONS_CHANGED"))
            {
                var restrictionsManager = (RestrictionsManager)context.GetSystemService(Context.RestrictionsService);
                var applicationRestrictions = restrictionsManager.ApplicationRestrictions;
                var restrictionEntries = restrictionsManager.GetManifestRestrictions(context.ApplicationContext.PackageName);
                foreach (var restrictionEntry in restrictionEntries)
                {
                    switch (restrictionEntry.Key)
                    {
                        case "defconStatus":
                            defconStatus = applicationRestrictions.GetInt("defconStatus").ToString();
                            break;

                        default:
                            break;
                    }
                }
            }
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

        private int GetApplicationDefconStatus()
        {
            int defconStatus = 5;
            var returnedDefconStatus = new SettingsService().GetSetting<string>("DefconStatus");
            if (!string.IsNullOrEmpty(returnedDefconStatus)) return int.Parse(returnedDefconStatus);
            return defconStatus;
        }
    }
}