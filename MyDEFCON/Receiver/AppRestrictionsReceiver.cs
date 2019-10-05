
using Android.App;
using Android.Content;
using CommonServiceLocator;
using MyDEFCON.Services;

namespace MyDEFCON.Receiver
{
    [BroadcastReceiver(Enabled = true)]
    [IntentFilter(new string[] { Intent.ActionApplicationRestrictionsChanged })]
    public class AppRestrictionsReceiver : BroadcastReceiver
    {
        IEventService _eventService;

        public override void OnReceive(Context context, Intent intent)
        {
            _eventService = ServiceLocator.Current.GetInstance<EventService>();
            var restrictionsManager = (RestrictionsManager)context.GetSystemService(Context.RestrictionsService);
            var applicationRestrictions = restrictionsManager.ApplicationRestrictions;
            var restrictionEntries = restrictionsManager.GetManifestRestrictions(context.ApplicationContext.PackageName);
            foreach (var restrictionEntry in restrictionEntries)
            {
                switch (restrictionEntry.Key)
                {
                    case "defconStatus":
                        _eventService.OnDefconStatusChangedEvent(new DefconStatusChangedEventArgs(applicationRestrictions.GetInt("defconStatus")));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}