﻿
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using Android.Support.V4.Content;
using CommonServiceLocator;
using MyDEFCON.Models;
using MyDEFCON.Receiver;
using Newtonsoft.Json;
using System;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Unity;
using Unity.ServiceLocation;

namespace MyDEFCON.Services
{
    [Service]
    public class ForegroundService : Service
    {
        bool _isStarted;
        ISettingsService _settingsService;
        Task _udpClientTask/*, _tcpClientTask*/;
        static DateTimeOffset _lastConnect;
        CancellationTokenSource _cancellationTokenSource;
        CancellationToken _cancellationToken;
        ForegroundDefconStatusReceiver _defconStatusReceiver;
        UdpClient udpClient;

        public delegate void CallBack(string defconStatus);

        public override void OnCreate()
        {
            base.OnCreate();
            _cancellationTokenSource = new CancellationTokenSource();
            _cancellationToken = _cancellationTokenSource.Token;
            _lastConnect = DateTimeOffset.MinValue;
            if (!ServiceLocator.IsLocationProviderSet)
            {
                var unityContainer = new UnityContainer();
                unityContainer.RegisterInstance<ISettingsService>(SettingsService.Instance());
                UnityServiceLocator unityServiceLocator = new UnityServiceLocator(unityContainer);
                ServiceLocator.SetLocatorProvider(() => unityServiceLocator);
            }
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
            _defconStatusReceiver = new ForegroundDefconStatusReceiver(new CallBack((x) =>
            {
                var notificationManager = (NotificationManager)GetSystemService(NotificationService);
                notificationManager.Notify(Constants.SERVICE_RUNNING_NOTIFICATION_ID, BuildNotification());
            }));
            LocalBroadcastManager.GetInstance(this).RegisterReceiver(_defconStatusReceiver, new IntentFilter("com.marcusrunge.MyDEFCON.DEFCON_UPDATE"));
        }

        private async void UdpClientAction()
        {
            try
            {
                udpClient = new UdpClient(4536);
                while (_isStarted)
                {
                    var udpReceiveResult = await udpClient.ReceiveAsync();
                    var defconStatus = Encoding.ASCII.GetString(udpReceiveResult.Buffer);
                    if (int.TryParse(defconStatus, out int parsedDefconStatus))
                    {
                        if (parsedDefconStatus > 0 && parsedDefconStatus < 6)
                        {
                            new SettingsService().SaveSetting("DefconStatus", defconStatus.ToString());

                            Intent widgetIntent = new Intent(this, typeof(MyDefconWidget));
                            widgetIntent.SetAction("com.marcusrunge.MyDEFCON.DEFCON_UPDATE");
                            widgetIntent.PutExtra("DefconStatus", defconStatus.ToString());

                            Intent statusReceiverIntent = new Intent(this, typeof(ForegroundDefconStatusReceiver));
                            statusReceiverIntent.SetAction("com.marcusrunge.MyDEFCON.STATUS_RECEIVER_ACTION");
                            statusReceiverIntent.PutExtra("DefconStatus", defconStatus.ToString());

                            SendBroadcast(widgetIntent);
                            SendBroadcast(statusReceiverIntent);
                        }
                        else if (parsedDefconStatus == 0 && _settingsService.GetSetting<bool>("IsMulticastEnabled") && DateTimeOffset.Now > _lastConnect.AddSeconds(5))
                        {
                            Intent tcpActionIntent = new Intent(this, typeof(TcpActionReceiver));
                            tcpActionIntent.SetAction("com.marcusrunge.MyDEFCON.TCP_ACTION");
                            tcpActionIntent.PutExtra("RemoteEndPointAddress", udpReceiveResult.RemoteEndPoint.Address.ToString());
                            SendBroadcast(tcpActionIntent);
                            _lastConnect = DateTimeOffset.Now;
                        }
                    }
                }
                if (udpClient != null)
                {
                    udpClient.Close();
                    udpClient.Dispose();
                }
            }
            catch { }
        }

        private async void TcpClientAction()
        {
            TcpListener tcpListener = new TcpListener(IPAddress.Any, 4537);
            tcpListener.Start();
            while (_isStarted)
            {
                try
                {
                    TcpClient tcpClient = await tcpListener.AcceptTcpClientAsync();
                    NetworkStream networkStream = tcpClient.GetStream();
                    var sqLiteAsyncConnection = ServiceLocator.Current.GetInstance<ISQLiteDependencies>().AsyncConnection;
                    var checkListEntries = await sqLiteAsyncConnection.QueryAsync<CheckListEntry>("SELECT * FROM CheckListEntry");
                    var json = JsonConvert.SerializeObject(checkListEntries);
                    byte[] jsonBytes = Encoding.ASCII.GetBytes(json);
                    await networkStream.WriteAsync(jsonBytes, 0, jsonBytes.Length);
                    networkStream.Close();
                    tcpClient.Close();
                }
                catch { }
            }
            tcpListener.Stop();
            tcpListener.Server.Dispose();
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
                    //_tcpClientTask = Task.Factory.StartNew(TcpClientAction, _cancellationToken);
                    _udpClientTask = Task.Factory.StartNew(UdpClientAction, _cancellationToken);
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
            _cancellationTokenSource.Cancel();
            LocalBroadcastManager.GetInstance(this).UnregisterReceiver(_defconStatusReceiver);
            udpClient.Close();
            udpClient.Dispose();
            _udpClientTask.Dispose();
            base.OnDestroy();
        }

        void RegisterForegroundService()
        {
            if (Build.VERSION.SdkInt >= BuildVersionCodes.O)
            {
                var notificationChannel = new NotificationChannel("DefconNotificationChannel", "DefconNotificationChannel", NotificationImportance.Low)
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

        [BroadcastReceiver(Enabled = true)]
        [IntentFilter(new string[] { "com.marcusrunge.MyDEFCON.STATUS_RECEIVER_ACTION" })]
        public class ForegroundDefconStatusReceiver : BroadcastReceiver
        {
            private static CallBack _callBack;

            public ForegroundDefconStatusReceiver()
            {

            }

            public ForegroundDefconStatusReceiver(CallBack callBack)
            {
                _callBack = callBack;
            }

            public override void OnReceive(Context context, Intent intent)
            {
                var defconStatus = intent.GetStringExtra("DefconStatus");
                if (defconStatus.Equals("0")) { }
                else _callBack(defconStatus);
            }
        }
    }
}