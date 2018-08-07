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
using System.Threading.Tasks;

namespace MyDEFCON.Services
{
    [Service(Exported = false, Name = "com.marcusrunge.MyDEFCON.TcpClientService")]
    public class TcpClientService : Service
    {
        private TcpListener _tcpListener = null;

        public override IBinder OnBind(Intent intent)
        {
            return null;
        }

        [return: GeneratedEnum]
        public override StartCommandResult OnStartCommand(Intent intent, [GeneratedEnum] StartCommandFlags flags, int startId)
        {
            Task.Run(async () =>
            {
                _tcpListener = new TcpListener(IPAddress.Any, 4537);
                _tcpListener.Start();
                while (true)
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
            });
            return StartCommandResult.Sticky;
        }
    }
}