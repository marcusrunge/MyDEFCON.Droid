using Android.Content;
using MyDEFCON.Services;

namespace MyDEFCON.Utilities
{
    public class Restrictor
    {
        public static void ResolveRestrictions(Context context, IEventService eventService)
        {
            var restrictionsManager = (RestrictionsManager)context.GetSystemService(Context.RestrictionsService);
            var applicationRestrictions = restrictionsManager.ApplicationRestrictions;
            var restrictionEntries = restrictionsManager.GetManifestRestrictions(context.ApplicationContext.PackageName);
            foreach (var restrictionEntry in restrictionEntries)
            {
                switch (restrictionEntry.Key)
                {
                    case "defconStatus":
                        int resolvedDefconStatus = applicationRestrictions.GetInt("defconStatus", -1);
                        if (resolvedDefconStatus > 0) eventService.OnDefconStatusChangedEvent(new DefconStatusChangedEventArgs(resolvedDefconStatus));
                        break;
                    default:
                        break;
                }
            }
        }
    }
}