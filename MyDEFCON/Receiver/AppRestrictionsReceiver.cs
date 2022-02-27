using Android.App;
using Android.Content;
using CommonServiceLocator;
using MyDEFCON.Services;
using MyDEFCON.Utilities;

namespace MyDEFCON.Receiver
{
    [BroadcastReceiver(Enabled = true)]
    [IntentFilter(new string[] { Intent.ActionApplicationRestrictionsChanged })]
    public class AppRestrictionsReceiver : BroadcastReceiver
    {
        public override void OnReceive(Context context, Intent intent) => Restrictor.ResolveRestrictions(context, ServiceLocator.Current.GetInstance<EventService>());
    }
}