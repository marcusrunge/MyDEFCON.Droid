using Android.App;
using Android.Content;
using CommonServiceLocator;
using MyDEFCON.Models;
using MyDEFCON.Services;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using SQLite;
using System.Net.Sockets;
using System.Threading.Tasks;
using Android.Widget;
using System.Text;

namespace MyDEFCON.Receiver
{
    [BroadcastReceiver(Enabled = true)]
    [IntentFilter(new string[] { "com.marcusrunge.MyDEFCON.TCP_ACTION" })]
    public class TcpActionReceiver : BroadcastReceiver
    {
        SQLiteAsyncConnection _sqLiteAsyncConnection;
        IEventService _eventService;
        static bool _isAlreadyConnected = false;
        //TcpClient _tcpClient;
        //Context _context;

        public TcpActionReceiver()
        {
            _sqLiteAsyncConnection = ServiceLocator.Current.GetInstance<ISQLiteDependencies>().AsyncConnection;
            _eventService = ServiceLocator.Current.GetInstance<IEventService>();
            //_tcpClient = new TcpClient();
        }

        public async override void OnReceive(Context context, Intent intent)
        {
            //_context = context;
            /*if (!_isAlreadyConnected) await Task<string>.Run(async () =>
              {
                  //Toast.MakeText(context, "New Checklist Update received...", ToastLength.Short).Show();
                  _isAlreadyConnected = true;
                  try
                  {
                      var remoteEndPointAddress = intent.GetStringExtra("RemoteEndPointAddress");
                      await _tcpClient?.ConnectAsync(remoteEndPointAddress, 4537);
                      NetworkStream networkStream = _tcpClient?.GetStream();
                      string result;
                      using (StreamReader streamReader = new StreamReader(networkStream))
                      {
                          result = await streamReader?.ReadLineAsync();
                          streamReader?.Close();
                      }
                      networkStream?.Close();
                      _tcpClient?.Close();
                      return result;
                  }
                  catch { return ""; }
              }).ContinueWith(UpdateDatabase);*/
            if (!_isAlreadyConnected)
            {
                _isAlreadyConnected = true;
                var remoteEndPointAddress = intent.GetStringExtra("RemoteEndPointAddress");
                TcpClient tcpClient = new TcpClient(remoteEndPointAddress, 4537);
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
                await UpdateDatabase(stringBuilder.ToString());
            }
        }

        private async Task UpdateDatabase(string jsonString)
        {
            try
            {
                var checkListEntries = JsonConvert.DeserializeObject<List<CheckListEntry>>(jsonString);
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
                            await _sqLiteAsyncConnection?.UpdateAsync(foundCheckListEntry);
                        }
                        else if (foundCheckListEntry.UnixTimeStampUpdated < checkListEntry.UnixTimeStampUpdated)
                        {
                            foundCheckListEntry.Item = checkListEntry.Item;
                            foundCheckListEntry.Checked = checkListEntry.Checked;
                            await _sqLiteAsyncConnection?.UpdateAsync(foundCheckListEntry);
                        }
                    }
                    else if (foundCheckListEntry != null) await _sqLiteAsyncConnection?.InsertAsync(checkListEntry);
                }
                _eventService?.OnChecklistUpdatedEvent(new EventArgs());
                //Toast.MakeText(_context, "New Checklist Update received...", ToastLength.Short).Show();
            }
            catch { }
            _isAlreadyConnected = false;
        }
    }
}