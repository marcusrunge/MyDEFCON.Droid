using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;

namespace MyDEFCON.Services
{
    [Service(Exported = false, Name = "com.marcusrunge.MyDEFCON.UdpClientService")]
    public class UdpClientService : Service
    {
        private UdpClient _udpClient = null;

        [return: GeneratedEnum]
        public override StartCommandResult OnStartCommand(Intent intent, [GeneratedEnum] StartCommandFlags flags, int startId)
        {
            _udpClient = new UdpClient(4536);
            Task.Run(async () =>
            {
                try
                {
                    while (true)
                    {
                        var udpReceiveResult = await _udpClient.ReceiveAsync();
                        var defconStatus = Encoding.ASCII.GetString(udpReceiveResult.Buffer);
                        if (int.TryParse(defconStatus, out int parsedDefconStatus))
                        {
                            if (parsedDefconStatus > 0 && parsedDefconStatus < 6)
                            {
                                new SettingsService().SaveSetting("DefconStatus", defconStatus.ToString());
                            }
                            else if (parsedDefconStatus == 0)
                            {

                            }
                            Intent defconIntent = new Intent("com.marcusrunge.MyDEFCON.DEFCON_UPDATE");
                            defconIntent.PutExtra("DefconStatus", defconStatus);
                            SendBroadcast(defconIntent);
                        }
                    }
                }
                catch { }
            });

            //return base.OnStartCommand(intent, flags, startId);
            return StartCommandResult.Sticky;
        }
        public override IBinder OnBind(Intent intent)
        {
            return null;
        }
    }
}