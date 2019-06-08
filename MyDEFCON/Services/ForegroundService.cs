
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.V4.Content;
using CommonServiceLocator;
using MyDEFCON.Receiver;
using System;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace MyDEFCON.Services
{
    [Service]
    public class ForegroundService : Service
    {
        bool _isStarted, _isConnectionBlocked;
        ISettingsService _settingsService;
        IEventService _eventService;
        Task _task;
        UdpClient _udpClient;
        static DateTimeOffset _lastConnect;

        public override void OnCreate()
        {
            base.OnCreate();
            _lastConnect = DateTimeOffset.MinValue;
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
            _eventService.BlockConnectionEvent += (s, e) => _isConnectionBlocked = (e as BlockConnectionEventArgs).Blocked;
            _udpClient = new UdpClient(4536);
            _task = Task.Factory.StartNew(async () =>
            {
                try
                {
                    while (true)
                    {
                        var udpReceiveResult = await _udpClient.ReceiveAsync();
                        var defconStatus = Encoding.ASCII.GetString(udpReceiveResult.Buffer);
                        if (int.TryParse(defconStatus, out int parsedDefconStatus) /*&& !_isConnectionBlocked*/)
                        {
                            if (parsedDefconStatus > 0 && parsedDefconStatus < 6)
                            {
                                new SettingsService().SaveSetting("DefconStatus", defconStatus.ToString());

                                Intent widgetIntent = new Intent(this, typeof(MyDefconWidget));
                                widgetIntent.SetAction("com.marcusrunge.MyDEFCON.DEFCON_UPDATE");
                                widgetIntent.PutExtra("DefconStatus", defconStatus.ToString());

                                Intent statusReceiverIntent = new Intent(this, typeof(DefconStatusReceiver));
                                statusReceiverIntent.SetAction("com.marcusrunge.MyDEFCON.STATUS_RECEIVER_ACTION");
                                statusReceiverIntent.PutExtra("DefconStatus", defconStatus.ToString());

                                SendBroadcast(widgetIntent);
                                SendBroadcast(statusReceiverIntent);
                            }
                            //else if (parsedDefconStatus == 0 && _settingsService.GetSetting<bool>("IsMulticastEnabled") && DateTimeOffset.Now > _lastConnect.AddSeconds(5))
                            //{
                            //    Intent tcpActionIntent = new Intent(this, typeof(TcpActionReceiver));
                            //    tcpActionIntent.SetAction("com.marcusrunge.MyDEFCON.TCP_ACTION");
                            //    tcpActionIntent.PutExtra("RemoteEndPointAddress", udpReceiveResult.RemoteEndPoint.Address.ToString());
                            //    SendBroadcast(tcpActionIntent);
                            //    _lastConnect = DateTimeOffset.Now;
                            //}
                        }
                    }
                }
                catch { }
            });
            _eventService.DefconStatusChangedEvent += (s, e) =>
            {
                var notificationManager = (NotificationManager)GetSystemService(NotificationService);
                notificationManager.Notify(Constants.SERVICE_RUNNING_NOTIFICATION_ID, BuildNotification());
            };
        }

        [return: GeneratedEnum]
        public override StartCommandResult OnStartCommand(Intent intent, [GeneratedEnum] StartCommandFlags flags, int startId)
        {
            if (intent.Action.Equals(Constants.ACTION_START_SERVICE))
            {
                if (_isStarted)
                {
                }
                else
                {
                    RegisterForegroundService();
                    _isStarted = true;
                }
            }
            else if (intent.Action.Equals(Constants.ACTION_STOP_SERVICE))
            {
                StopForeground(true);
                StopSelf();
                _isStarted = false;

            }
            return StartCommandResult.Sticky;
        }

        public override IBinder OnBind(Intent intent)
        {
            return null;
        }

        public override void OnDestroy()
        {
            var notificationManager = (NotificationManager)GetSystemService(NotificationService);
            notificationManager.Cancel(Constants.SERVICE_RUNNING_NOTIFICATION_ID);
            _isStarted = false;
            base.OnDestroy();
        }

        void RegisterForegroundService()
        {
            if (Build.VERSION.SdkInt >= BuildVersionCodes.O)
            {
                var notificationChannel = new NotificationChannel("DefconNotificationChannel", "DefconNotificationChannel", NotificationImportance.Default)
                {
                    Description = "Foreground Service Notification Channel"
                };
                var notificationManager = (NotificationManager)GetSystemService(NotificationService);
                notificationManager.CreateNotificationChannel(notificationChannel);
            }
            
            StartForeground(Constants.SERVICE_RUNNING_NOTIFICATION_ID, BuildNotification());
        }

        Notification BuildNotification()
        {
            var defconStatus = _settingsService.GetSetting<string>("DefconStatus");
            string contentText = "DEFCON " + defconStatus;
            int smallIconResourceId = Resource.Drawable.ic_stat_5;
            switch (defconStatus)
            {
                case "1":
                    smallIconResourceId = Resource.Drawable.ic_stat_1;
                    break;
                case "2":
                    smallIconResourceId = Resource.Drawable.ic_stat_2;
                    break;
                case "3":
                    smallIconResourceId = Resource.Drawable.ic_stat_3;
                    break;
                case "4":
                    smallIconResourceId = Resource.Drawable.ic_stat_4;
                    break;
                default:
                    break;
            }
            return new Notification.Builder(this, "DefconNotificationChannel")
                .SetContentTitle(contentText)
                .SetSmallIcon(smallIconResourceId)
                .SetContentIntent(BuildIntentToShowMainActivity())
                .SetOngoing(true)
                .Build();
        }

        PendingIntent BuildIntentToShowMainActivity()
        {
            var notificationIntent = new Intent(this, typeof(MainActivity));
            notificationIntent.SetAction(Constants.ACTION_MAIN_ACTIVITY);
            notificationIntent.SetFlags(ActivityFlags.SingleTop | ActivityFlags.ClearTask);
            notificationIntent.PutExtra(Constants.SERVICE_STARTED_KEY, true);
            var pendingIntent = PendingIntent.GetActivity(this, 0, notificationIntent, PendingIntentFlags.UpdateCurrent);
            return pendingIntent;
        }
    }
}