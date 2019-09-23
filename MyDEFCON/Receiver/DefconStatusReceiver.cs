using Android.App;
using Android.Content;
using CommonServiceLocator;
using MyDEFCON.Services;
using System;

namespace MyDEFCON.Receiver
{
    [BroadcastReceiver(Enabled = true)]
    [IntentFilter(new string[] { "com.marcusrunge.MyDEFCON.STATUS_RECEIVER_ACTION" })]
    public class DefconStatusReceiver : BroadcastReceiver
    {
        IEventService _eventService;

        public DefconStatusReceiver()
        {
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
        }

        public override void OnReceive(Context context, Intent intent)
        {
            try
            {
                var defconStatus = intent.GetStringExtra("DefconStatus");
                if (defconStatus.Equals("0")) { }
                else _eventService.OnDefconStatusChangedEvent(new DefconStatusChangedEventArgs(int.Parse(defconStatus)));

                //Toast.MakeText(context, "DEFCON " + defconStatus, ToastLength.Short).Show();
            }
            catch (Exception) { }
        }
    }
}