using System;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using CommonServiceLocator;

namespace MyDEFCON.Services
{
    [Service(Exported = false, Name = "com.marcusrunge.MyDEFCON.UdpClientService")]
    public class UdpClientService : Service
    {
        private UdpClient _udpClient = null;        
        ISettingsService _settingsService;
        IEventService _eventService;        
        private static DateTimeOffset _lastConnect;
        private bool _isConnectionBlocked, _isServiceRunning;
        CancellationTokenSource _cancellationTokenSource;
        CancellationToken _cancellationToken;

        public override void OnCreate()
        {
            base.OnCreate();
            _cancellationTokenSource = new CancellationTokenSource();
            _cancellationToken = _cancellationTokenSource.Token;
        }

        public override void OnDestroy()
        {
            _isServiceRunning = false;            
            _cancellationTokenSource.Cancel();
            _udpClient.Close();
            _udpClient = null;
            base.OnDestroy();
        }

        [return: GeneratedEnum]
        public override StartCommandResult OnStartCommand(Intent intent, [GeneratedEnum] StartCommandFlags flags, int startId)
        {
            _isServiceRunning = true;
            _isConnectionBlocked = false;
            _lastConnect = DateTimeOffset.MinValue;
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
            _eventService.BlockConnectionEvent += (s, e) => _isConnectionBlocked = (e as BlockConnectionEventArgs).Blocked;
            _udpClient = new UdpClient(4536);
            Task.Run(async () =>
            {
                try
                {
                    while (_isServiceRunning)
                    {
                        var udpReceiveResult = await _udpClient.ReceiveAsync();
                        var defconStatus = Encoding.ASCII.GetString(udpReceiveResult.Buffer);
                        if (int.TryParse(defconStatus, out int parsedDefconStatus) && !_isConnectionBlocked)
                        {
                            if (parsedDefconStatus > 0 && parsedDefconStatus < 6)
                            {
                                new SettingsService().SaveSetting("DefconStatus", defconStatus.ToString());
                                Intent defconIntent = new Intent("com.marcusrunge.MyDEFCON.DEFCON_UPDATE");
                                defconIntent.PutExtra("DefconStatus", defconStatus);
                                SendBroadcast(defconIntent);
                            }
                            else if (parsedDefconStatus == 0 && _settingsService.GetSetting<bool>("IsMulticastEnabled") && DateTimeOffset.Now > _lastConnect.AddSeconds(5))
                            {
                                Intent tcpActionIntent = new Intent("com.marcusrunge.MyDEFCON.TCP_ACTION");
                                tcpActionIntent.PutExtra("RemoteEndPointAddress", udpReceiveResult.RemoteEndPoint.Address.ToString());
                                SendBroadcast(tcpActionIntent);
                                _lastConnect = DateTimeOffset.Now;
                            }
                        }
                        _isConnectionBlocked = false;
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