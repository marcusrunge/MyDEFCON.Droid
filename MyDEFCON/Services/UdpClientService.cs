using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using CommonServiceLocator;
using MyDEFCON.Receiver;
using MyDEFCON.Utilities;
using System;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace MyDEFCON.Services
{
    [Service(Exported = false, Name = "com.marcusrunge.MyDEFCON.UdpClientService")]
    public class UdpClientService : Service
    {
        private UdpClient _udpClient = null;
        private ISettingsService _settingsService;
        private IEventService _eventService;
        private static DateTimeOffset _lastConnect;
        private bool _isConnectionBlocked, _isServiceRunning;
        private CancellationTokenSource _cancellationTokenSource;
        private CancellationToken _cancellationToken;

        public override void OnCreate()
        {
            base.OnCreate();
            _cancellationTokenSource = new CancellationTokenSource();
            _cancellationToken = _cancellationTokenSource.Token;
        }

        public override void OnDestroy()
        {
            _isServiceRunning = false;
            if (_cancellationTokenSource != null) _cancellationTokenSource.Cancel();
            if (_udpClient != null) _udpClient.Close();
            base.OnDestroy();
        }

        [return: GeneratedEnum]
        public override StartCommandResult OnStartCommand(Intent intent, [GeneratedEnum] StartCommandFlags flags, int startId)
        {
            _isServiceRunning = true;
            _isConnectionBlocked = false;
            _lastConnect = DateTimeOffset.MinValue;
            try
            {
                _settingsService = ServiceLocator.Current.GetInstance<SettingsService>();
                _eventService = ServiceLocator.Current.GetInstance<EventService>();
            }
            catch (Exception)
            {
                return StartCommandResult.RedeliverIntent;
            }

            _eventService.BlockConnectionEvent += (s, e) => _isConnectionBlocked = (e as BlockConnectionEventArgs).Blocked;
            _udpClient = new UdpClient(4536);
            Task.Run(async () =>
            {
                try
                {
                    while (_isServiceRunning)
                    {
                        var udpReceiveResult = await _udpClient.ReceiveAsync();
                        if (!udpReceiveResult.RemoteEndPoint.Address.ToString().Equals(Networker.GetLocalIp()))
                        {
                            var defconStatus = Encoding.ASCII.GetString(udpReceiveResult.Buffer);
                            if (int.TryParse(defconStatus, out int parsedDefconStatus) && !_isConnectionBlocked)
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

                                    if (!_settingsService.GetSetting<bool>("IsForegroundServiceEnabled") && _settingsService.GetSetting<bool>("isStatusUpdateAlertEnabled"))
                                    {
                                        Notifier.AlertWithVibration();
                                        Notifier.AlertWithAudioNotification(this, _settingsService.GetSetting<int>("StatusUpdateAlertSelection"));
                                    }
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
                            _isConnectionBlocked = false;
                        }
                    }
                }
                catch { }
            }, _cancellationToken);

            //return base.OnStartCommand(intent, flags, startId);
            return StartCommandResult.Sticky;
        }

        public override IBinder OnBind(Intent intent)
        {
            return null;
        }
    }
}