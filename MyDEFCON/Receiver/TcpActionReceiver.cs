using Android.App;
using Android.Content;
using CommonServiceLocator;
using MyDEFCON.Models;
using MyDEFCON.Services;
using Newtonsoft.Json;
using SQLite;
using System.Collections.Generic;
using System.Net.Sockets;
using System.Text;

namespace MyDEFCON.Receiver
{
    [BroadcastReceiver(Enabled = true)]
    [IntentFilter(new string[] { "com.marcusrunge.MyDEFCON.TCP_ACTION" })]
    public class TcpActionReceiver : BroadcastReceiver
    {
        SQLiteAsyncConnection _sqLiteAsyncConnection;
        IEventService _eventService;

        public TcpActionReceiver()
        {
            _sqLiteAsyncConnection = ServiceLocator.Current.GetInstance<ISQLiteDependencies>().AsyncConnection;
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
        }

        public async override void OnReceive(Context context, Intent intent)
        {
            try
            {
                var remoteEndPointAddress = intent.GetStringExtra("RemoteEndPointAddress");
                TcpClient tcpClient = new TcpClient();
                await tcpClient.ConnectAsync(remoteEndPointAddress, 4537);
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
                    networkStream.Close();
                    tcpClient.Close();
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
                _eventService.OnChecklistUpdatedEvent();
            }
            catch { }
        }
    }
}