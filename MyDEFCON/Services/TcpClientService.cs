using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using CommonServiceLocator;
using MyDEFCON.Models;
using Newtonsoft.Json;
using System.Net;
using System.Net.Sockets;
using System.Text;
using System.Threading;
using System.Threading.Tasks;

namespace MyDEFCON.Services
{
    [Service(Exported = false, Name = "com.marcusrunge.MyDEFCON.TcpClientService")]
    public class TcpClientService : Service
    {
        private TcpListener _tcpListener = null;
        private CancellationTokenSource _cancellationTokenSource;
        private CancellationToken _cancellationToken;
        private bool _isServiceRunning;

        public override IBinder OnBind(Intent intent)
        {
            return null;
        }

        public override void OnCreate()
        {
            base.OnCreate();
            _cancellationTokenSource = new CancellationTokenSource();
            _cancellationToken = _cancellationTokenSource.Token;
        }

        [return: GeneratedEnum]
        public override StartCommandResult OnStartCommand(Intent intent, [GeneratedEnum] StartCommandFlags flags, int startId)
        {
            _isServiceRunning = true;
            Task.Run(async () =>
            {
                _tcpListener = new TcpListener(IPAddress.Any, 4537);
                _tcpListener.Start();
                while (_isServiceRunning)
                {
                    try
                    {
                        TcpClient tcpClient = await _tcpListener.AcceptTcpClientAsync();
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
            }, _cancellationToken);
            return StartCommandResult.Sticky;
        }

        public override void OnDestroy()
        {
            _isServiceRunning = false;
            try
            {
                if (_cancellationTokenSource != null) _cancellationTokenSource.Cancel();
            }
            catch { }
            try
            {
                if (_tcpListener != null) _tcpListener.Stop();
            }
            catch { }
            base.OnDestroy();
        }
    }
}