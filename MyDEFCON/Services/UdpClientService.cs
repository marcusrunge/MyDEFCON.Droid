using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;
using System.Threading.Tasks;
using Android.App;
using Android.Content;
using Android.OS;
using Android.Runtime;
using CommonServiceLocator;
using MyDEFCON.Models;
using Newtonsoft.Json;
using SQLite;

namespace MyDEFCON.Services
{
    [Service(Exported = false, Name = "com.marcusrunge.MyDEFCON.UdpClientService")]
    public class UdpClientService : Service
    {
        private UdpClient _udpClient = null;
        IEventService _eventService;
        ISettingsService _settingsService;
        SQLiteAsyncConnection _sqLiteAsyncConnection;

        [return: GeneratedEnum]
        public override StartCommandResult OnStartCommand(Intent intent, [GeneratedEnum] StartCommandFlags flags, int startId)
        {
            _sqLiteAsyncConnection = ServiceLocator.Current.GetInstance<ISQLiteDependencies>().AsyncConnection;
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
            _settingsService = ServiceLocator.Current.GetInstance<ISettingsService>();
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
                            else if (parsedDefconStatus == 0 && _settingsService.GetSetting<bool>("IsMulticastEnabled"))
                            {
                                try
                                {
                                    TcpClient tcpClient = new TcpClient();
                                    await tcpClient.ConnectAsync(udpReceiveResult.RemoteEndPoint.Address.ToString(), 4537);
                                    var isTcpClientAvailable = tcpClient.Available;
                                    StringBuilder stringBuilder = new StringBuilder();
                                    using (NetworkStream networkStream = tcpClient.GetStream())
                                    {
                                        byte[] receiveBuffer = new byte[tcpClient.ReceiveBufferSize];
                                        int bytes = -1;
                                        do
                                        {
                                            bytes = await networkStream.ReadAsync(receiveBuffer, 0, receiveBuffer.Length);
                                            stringBuilder.Append(Encoding.ASCII.GetString(receiveBuffer));
                                            if (stringBuilder.ToString().IndexOf("\0") != -1) break;
                                        } while (bytes != 0);
                                    }

                                    var checkListEntries = JsonConvert.DeserializeObject<List<CheckListEntry>>(stringBuilder.ToString());
                                    foreach (var checkListEntry in checkListEntries)
                                    {
                                        CheckListEntry foundCheckListEntry = (await _sqLiteAsyncConnection.FindAsync<CheckListEntry>(c => c.UnixTimeStampCreated == checkListEntry.UnixTimeStampCreated));
                                        if (foundCheckListEntry != null)
                                        {
                                            if (foundCheckListEntry.Deleted != checkListEntry.Deleted)
                                            {
                                                foundCheckListEntry.Deleted = checkListEntry.Deleted;
                                                foundCheckListEntry.Visibility = checkListEntry.Visibility;
                                                foundCheckListEntry.Checked = true;
                                                await _sqLiteAsyncConnection.UpdateAsync(foundCheckListEntry);
                                            }
                                            else if (foundCheckListEntry.UnixTimeStampUpdated < checkListEntry.UnixTimeStampUpdated)
                                            {
                                                foundCheckListEntry.Item = checkListEntry.Item;
                                                foundCheckListEntry.Checked = checkListEntry.Checked;
                                                await _sqLiteAsyncConnection.UpdateAsync(foundCheckListEntry);
                                            }
                                        }
                                        else await _sqLiteAsyncConnection.InsertAsync(checkListEntry);
                                    }                                    
                                    tcpClient.Close();
                                    tcpClient.Dispose();
                                    _eventService.OnChecklistUpdatedEvent();
                                }
                                catch { }
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